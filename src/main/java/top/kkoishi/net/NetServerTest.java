package top.kkoishi.net;

import top.kkoishi.concurrent.DefaultThreadFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author KKoishi_
 */
public final class NetServerTest implements Runnable {

    private final Socket socket;

    private static final ScheduledThreadPoolExecutor POOL = new ScheduledThreadPoolExecutor(16, new DefaultThreadFactory());

    public NetServerTest (Socket socket) {
        this.socket = socket;
    }

    public static void main (String[] args) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(5141, 32);
        while (true) {
            final Socket socket = serverSocket.accept();
            if (socket != null) {
                POOL.schedule(new NetServerTest(socket), 0, TimeUnit.MILLISECONDS);
            }
        }
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
        while (true) {
            try {
                final byte[] bytes = NetDataPackageSenderAndReceiver.readBytes(socket);
                final int type = getHeartBeatType(bytes);
                System.out.println("Type:" + type);
                switch (type) {
                    case 0: {
                        System.out.println("Message request.");
                        System.out.println("Get message:" + NetDataPackageSenderAndReceiver.readUtf8(socket));
                        NetDataPackageSenderAndReceiver.sendBytes(socket, new byte[]{0x11, 0x53});
                        break;
                    }
                    case 1: {
                        System.out.println("Receive file.");
                        NetDataPackageSenderAndReceiver.sendBytes(socket, new byte[]{0x11, 0x52});
                        break;
                    }
                    case -2: {
                        System.out.println("End.");
                        return;
                    }
                    default: {
                        System.out.println("Error msg type!");
                        NetDataPackageSenderAndReceiver.sendBytes(socket, new byte[]{0x11, 0x51});
                        break;
                    }
                }
                System.out.println("Finish one loop.");
                Thread.sleep(300);
            } catch (SocketException ex) {
                return;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private static int getHeartBeatType (byte[] bytes) {
        if (Arrays.equals(bytes, new byte[]{0x11, 0x51})) {
            return -1;
        } else if (Arrays.equals(bytes, new byte[]{0x11, 0x52})) {
            return 1;
        } else if (Arrays.equals(bytes, new byte[]{0x11, 0x53})) {
            return 0;
        } else {
            return -2;
        }
    }
}
