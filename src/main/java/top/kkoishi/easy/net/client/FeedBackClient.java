package top.kkoishi.easy.net.client;

import top.kkoishi.easy.Main;
import top.kkoishi.easy.lang.BadReportStateException;
import top.kkoishi.easy.lang.FileTransportInterruptedException;
import top.kkoishi.easy.net.ConstPool;
import top.kkoishi.io.Files;
import top.kkoishi.net.NetDataPackageSenderAndReceiver;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @author KKoishi_
 */
public final class FeedBackClient {

    static final File ERR = new File("./error.log");

    static final File OUT = new File("./output.log");

    static Consumer<Exception> action = e -> JOptionPane.showMessageDialog(null, e);

    static Consumer<Object> succAction = o -> JOptionPane.showMessageDialog(null, "Succ!");

    private static boolean kill = false;

    public static boolean isKill () {
        return kill;
    }

    public static Consumer<Exception> getAction () {
        return action;
    }

    public static void setAction (Consumer<Exception> action) {
        FeedBackClient.action = action;
    }

    public static void main (String[] args) {
        kill = false;
        try {
            report(args.length == 0 ? "initial commit" : args[0]);
            succAction.accept(null);
        } catch (IOException | FileTransportInterruptedException | BadReportStateException | ParseException e) {
            e.printStackTrace();
            if (action != null) {
                action.accept(e);
            }
        } finally {
            kill = true;
        }
    }

    private static void report (String commit) throws IOException, FileTransportInterruptedException, BadReportStateException, ParseException {
        final String reportDir = "./report/report_" + ClientLogSeparator.FILE_NAME_FORMAT.format(new Date(System.currentTimeMillis())) + "/";
        final File reportErr = new File("./data" + reportDir + "/error.log");
        final File reportOut = new File("./data" + reportDir + "/output.log");
        final File reportCommit = new File("./data" + reportDir + "/commit.koishi");
        System.out.println(new File( "./data" + reportDir).mkdirs());
        System.out.println(reportErr.getAbsolutePath());
        reportErr.createNewFile();
        reportOut.createNewFile();
        final ClientLogSeparator err = new ClientLogSeparator(ERR, reportErr);
        final ClientLogSeparator out = new ClientLogSeparator(OUT, reportOut);
        err.handle();
        //sep is 60 sec.
        out.setSep(1000 * 300);
        out.handle();
        Files.write(reportCommit, commit);

        final Socket socket = new Socket(Main.PROC.getProperty("feedback_ip"), Integer.parseInt(Main.PROC.getProperty("feedback_port")));
        NetDataPackageSenderAndReceiver.sendBytes(socket, ConstPool.START);
        handleServerQuote(socket);

        NetDataPackageSenderAndReceiver.sendInt(socket, 1);
        NetDataPackageSenderAndReceiver.sendBytes(socket, ConstPool.REPORT_HEAD);

        NetDataPackageSenderAndReceiver.sendInt(socket, 3);
        NetDataPackageSenderAndReceiver.sendFile(socket, reportErr, reportDir);
        NetDataPackageSenderAndReceiver.sendFile(socket, reportOut, reportDir);
        NetDataPackageSenderAndReceiver.sendFile(socket, reportCommit, reportDir);
        NetDataPackageSenderAndReceiver.sendBytes(socket, ConstPool.REPORT_END);
        handleServerFinishFlag(socket);

        NetDataPackageSenderAndReceiver.sendBytes(socket, ConstPool.END);
        socket.close();
    }

    private static void handleServerFinishFlag (Socket socket) throws IOException, BadReportStateException {
        if (!Arrays.equals(ConstPool.FINISHED, NetDataPackageSenderAndReceiver.readBytes(socket))) {
            throw new BadReportStateException("Failed to report:" + NetDataPackageSenderAndReceiver.readUtf8(socket));
        }
    }

    private static void handleServerQuote (Socket socket) throws IOException, BadReportStateException {
        if (!Arrays.equals(ConstPool.ACCEPT, NetDataPackageSenderAndReceiver.readBytes(socket))) {
            throw new BadReportStateException("Server has denied your connection.");
        }
    }
}
