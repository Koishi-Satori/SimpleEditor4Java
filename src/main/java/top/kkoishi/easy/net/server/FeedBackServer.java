package top.kkoishi.easy.net.server;

import top.kkoishi.concurrent.DefaultThreadFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author KKoishi_
 */
public final class FeedBackServer implements Runnable {

    static final ScheduledThreadPoolExecutor POOL = new ScheduledThreadPoolExecutor(16, new DefaultThreadFactory());

    public static void main (String[] args) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(5141);
        POOL.scheduleAtFixedRate(new FeedBackServer(), 0, 35, TimeUnit.MILLISECONDS);
        while (true) {
            final Socket socket = serverSocket.accept();
            if (socket != null) {
                POOL.schedule(new SocketHandler(socket), 0, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void run () {
        final Scanner scanner = new Scanner(System.in);
        if ("/quit".equals(scanner.nextLine())) {
            System.exit(514);
        }
    }
}
