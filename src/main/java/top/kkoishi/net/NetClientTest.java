package top.kkoishi.net;

import java.io.IOException;
import java.net.Socket;

public final class NetClientTest implements Runnable {

    Socket socket;

    public NetClientTest (Socket socket) {
        this.socket = socket;
    }

    public static void main (String[] args) throws IOException {
        final Socket socket = new Socket("127.0.0.1", 514_1);
        new Thread(new NetClientTest(socket)).start();
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
            NetDataPackageSenderAndReceiver.sendBytes(socket, new byte[]{0x11, 0x53});
            NetDataPackageSenderAndReceiver.sendUtf8(socket, "fuckLzu!");
            NetDataPackageSenderAndReceiver.sendBytes(socket, new byte[]{0x11, 0x50});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
