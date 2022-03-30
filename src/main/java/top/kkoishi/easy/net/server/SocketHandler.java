package top.kkoishi.easy.net.server;

import top.kkoishi.easy.lang.BadReportStateException;
import top.kkoishi.easy.lang.FileTransportInterruptedException;
import top.kkoishi.easy.lang.IllegalTransportProtocolException;
import top.kkoishi.easy.net.ConstPool;
import top.kkoishi.net.NetDataPackageSenderAndReceiver;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.function.Consumer;

public final class SocketHandler implements Runnable {
    final Socket socket;

    boolean finished = false;

    final Consumer<Exception> action;

    public boolean isFinished () {
        return finished;
    }

    public SocketHandler (Socket socket, Consumer<Exception> action) {
        this.socket = socket;
        this.action = action;
    }

    public SocketHandler (Socket socket) {
        this(socket, null);
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run () {
        try {
            if (Arrays.equals(ConstPool.START, NetDataPackageSenderAndReceiver.readBytes(socket))) {
                NetDataPackageSenderAndReceiver.sendBytes(socket, ConstPool.ACCEPT);
                final int loopAmount = NetDataPackageSenderAndReceiver.readInt(socket);
                for (int i = 0; i < loopAmount; i++) {
                    handleClientQuote();
                }
                if (!Arrays.equals(ConstPool.END, NetDataPackageSenderAndReceiver.readBytes(socket))) {
                    throw new BadReportStateException("Bad bytes.");
                }
            } else {
                socket.close();
            }
        } catch (IOException | BadReportStateException e) {
            e.printStackTrace();
            if (action != null) {
                action.accept(e);
            }
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                if (action != null) {
                    action.accept(e);
                }
            } finally {
                finished = true;
            }
        } catch (IllegalTransportProtocolException | FileTransportInterruptedException e) {
            e.printStackTrace();
            if (action != null) {
                action.accept(e);
            }
        } finally {
            finished = true;
        }
    }

    private void handleClientQuote () throws IOException, IllegalTransportProtocolException, FileTransportInterruptedException {
        if (Arrays.equals(ConstPool.REPORT_HEAD, NetDataPackageSenderAndReceiver.readBytes(socket))) {
            receiveReport();
        }
    }

    private void receiveReport () throws IOException, IllegalTransportProtocolException, FileTransportInterruptedException {
        final int loopAmount = NetDataPackageSenderAndReceiver.readInt(socket);
        for (int i = 0; i < loopAmount; i++) {
            NetDataPackageSenderAndReceiver.readFile(socket);
        }
        if (!Arrays.equals(ConstPool.REPORT_END, NetDataPackageSenderAndReceiver.readBytes(socket))) {
            NetDataPackageSenderAndReceiver.sendBytes(socket, ConstPool.BAD_STATE);
            NetDataPackageSenderAndReceiver.sendUtf8(socket, "Bad state!Fuck you!");
        } else {
            NetDataPackageSenderAndReceiver.sendBytes(socket, ConstPool.FINISHED);
        }
    }
}
