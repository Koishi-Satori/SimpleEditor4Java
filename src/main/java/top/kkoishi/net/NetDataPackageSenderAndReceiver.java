package top.kkoishi.net;

import top.kkoishi.easy.lang.FileTransportInterruptedException;
import top.kkoishi.easy.lang.IllegalTransportProtocolException;
import top.kkoishi.io.Files;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class NetDataPackageSenderAndReceiver {

    public static final int BUFFER_SIZE = 1024;
    public static final byte[] ACCEPT_SYMBOL = {0X0A, 0X0A, 0X0C, 0X0E};

    /**
     * The file start transport mark.
     * The default one is:
     * <pre>
     * 0X4B, 0X6F, 0X69, 0X73 ,0X68 ,0X69 ,0X20 ,0X61 , 0X6E , 0X64 , 0X20 , 0X59 , 0X75 , 0X79 , 0X75 ,0X6B ,0X6F ,0X21
     * ("Koishi and Yuyuko!" in utf8!)
     * </pre>
     */
    protected static byte[] fileStartTransportMark = new byte[]{0X4B, 0X6F, 0X69, 0X73, 0X68, 0X69, 0X20, 0X61, 0X6E, 0X64, 0X20, 0X59, 0X75, 0X79, 0X75, 0X6B, 0X6F, 0X21};

    /**
     * The file transport ends mark.
     * The default one is:
     * <pre>
     * 0X59, 0X75, 0X79, 0X75, 0X6b, 0X6f, 0X20, 0X73, 0X61, 0X6d, 0X61, 0X20, 0X73, 0X69, 0X68, 0X61, 0X20, 0X73, 0X69, 0X68, 0X61
     * ("Yuyuko sama siha siha" in utf8!)
     * </pre>
     */
    protected static byte[] fileTransportEndMark = new byte[]{0X59, 0X75, 0X79, 0X75, 0X6B, 0X6F, 0X20, 0X73, 0X61, 0X6F,
            0X61, 0X20, 0X73, 0X69, 0X68, 0X61, 0X20, 0X73, 0X69, 0X68, 0X61};

    public static byte[] getFileTransportEndMark () {
        return fileTransportEndMark;
    }

    public static void setFileTransportEndMark (byte[] fileTransportEndMark) {
        NetDataPackageSenderAndReceiver.fileTransportEndMark = fileTransportEndMark;
    }

    public static byte[] getFileStartTransportMark () {
        return fileStartTransportMark;
    }

    public static void setFileStartTransportMark (byte[] fileStartTransportMark) {
        NetDataPackageSenderAndReceiver.fileStartTransportMark = fileStartTransportMark;
    }

    public static DataOutputStream getDataOutputStream (Socket socket) throws IOException {
        return new DataOutputStream(socket.getOutputStream());
    }

    public static DataInputStream getDataInputStream (Socket socket) throws IOException {
        return new DataInputStream(socket.getInputStream());
    }

    private NetDataPackageSenderAndReceiver () {
    }

    public static void sendUtf8 (Socket socket, String utf8string) throws IOException {
        final var out = getDataOutputStream(socket);
        out.writeUTF(utf8string);
        out.flush();
    }

    public static String readUtf8 (Socket socket) throws IOException {
        final var in = getDataInputStream(socket);
        return in.readUTF();
    }

    public static void sendInt (Socket socket, int v) throws IOException {
        final var out = getDataOutputStream(socket);
        out.writeInt(v);
        out.flush();
    }

    public static int readInt (Socket socket) throws IOException {
        final var in = getDataInputStream(socket);
        return in.readInt();
    }

    public static void sendBytes (Socket socket, byte[] bytes) throws IOException {
        final var out = getDataOutputStream(socket);
        sendInt(socket, bytes.length);
        out.write(bytes);
        out.flush();
    }

    public static byte[] readBytes (Socket socket) throws IOException {
        final var in = getDataInputStream(socket);
        final int dataLength = readInt(socket);
        final byte[] data = new byte[dataLength];
        if (in.read(data) != -1) {
            return data;
        } else {
            throw new InterruptedIOException();
        }
    }

    public static void sendFile (Socket socket, String srcName, String dirPath, File src) throws IOException,
            FileTransportInterruptedException {
        sendBytes(socket, fileStartTransportMark);
        sendUtf8(socket, dirPath);
        sendUtf8(socket, srcName);
        if (!Arrays.equals(readBytes(socket), ACCEPT_SYMBOL)) {
            throw new FileTransportInterruptedException("Can not send file for:" + readUtf8(socket));
        }
        sendBytes(socket, new byte[]{0X11, 0X45, 0X14, 0X19, 0X19, 0X8, 0X10, 0X01});
        final var in = new FileInputStream(src);
        final byte[] buffer = in.readNBytes(in.available());
        sendBytes(socket, buffer);
        in.close();
        if (!Arrays.equals(ACCEPT_SYMBOL, readBytes(socket))) {
            final String cause = readUtf8(socket);
            throw new FileTransportInterruptedException(cause);
        }
        //The symbol of transport of file has finished.
        //Default one is utf8-bytes of "Yuyuko sama siha siha"!
        //Yuyuko the best!
        sendBytes(socket, fileTransportEndMark);
    }

    public static void sendFile (Socket socket, File src, String dirPath) throws IOException, FileTransportInterruptedException {
        //The symbol of start transport file info.
        //Default one is utf8-bytes of "Koishi and Yuyuko!"
        //Yuyuko sama siha siha!
        sendBytes(socket, fileStartTransportMark);
        sendUtf8(socket, dirPath);
        sendUtf8(socket, src.getName());
        if (!Arrays.equals(readBytes(socket), ACCEPT_SYMBOL)) {
            throw new FileTransportInterruptedException("Can not send file for:" + readUtf8(socket));
        }
        //start transport symbol.(after removing hex symbol, the mark:114514191981001)
        sendBytes(socket, new byte[]{0X11, 0X45, 0X14, 0X19, 0X19, 0X8, 0X10, 0X01});
        final var in = new FileInputStream(src);
        final byte[] buffer = in.readNBytes(in.available());
        sendBytes(socket, buffer);
        in.close();
        if (!Arrays.equals(ACCEPT_SYMBOL, readBytes(socket))) {
            final String cause = readUtf8(socket);
            throw new FileTransportInterruptedException(cause);
        }
        //The symbol of transport of file has finished.
        //Default one is utf8-bytes of "Yuyuko sama siha siha"!
        //Yuyuko the best!
        sendBytes(socket, fileTransportEndMark);
    }

    public static void readFile (Socket socket) throws IOException, IllegalTransportProtocolException, FileTransportInterruptedException {
        byte[] mark = readBytes(socket);
        if (Arrays.equals(fileStartTransportMark, mark)) {
            final String dirPath = readUtf8(socket);
            final String name = readUtf8(socket);
            final File requireFile = new File(dirPath + name);
            System.out.println("New File Requirement:" + requireFile.getAbsolutePath());
            if (requireFile.exists()) {
                sendBytes(socket, new byte[]{0X0A, 0X0A, 0X0C, 0X0F});
                return;
            }
            sendBytes(socket, ACCEPT_SYMBOL);
            mark = readBytes(socket);
            if (Arrays.equals(new byte[]{0X11, 0X45, 0X14, 0X19, 0X19, 0X8, 0X10, 0X01}, mark)) {
                final byte[] buffer = readBytes(socket);
                try {
                    if (new File(dirPath).mkdirs()) {
                        Files.write(requireFile, buffer);
                        System.out.println("Receive file:" + requireFile.getCanonicalPath());
                    } else {
                        Files.write(requireFile, buffer);
                        if (!requireFile.exists()) {
                            throw new IOException("Can not access the required file:" + name);
                        }
                        System.out.println("Receive file(Directly override):" + requireFile.getCanonicalPath());
                    }
                    sendBytes(socket, ACCEPT_SYMBOL);
                    mark = readBytes(socket);
                    if (!Arrays.equals(mark, fileTransportEndMark)) {
                        throw new FileTransportInterruptedException("Receive error bytes:" + toHexString(mark));
                    }
                } catch (Exception e) {
                    sendBytes(socket, new byte[]{0X0A, 0X0A, 0X0C, 0X0F});
                    sendUtf8(socket, e.getMessage());
                    throw new FileTransportInterruptedException(e);
                }
            } else {
                throw new IllegalTransportProtocolException("Receive error bytes:" + toHexString(mark));
            }
        } else {
            throw new IllegalTransportProtocolException("Receive error bytes:" + toHexString(mark));
        }
    }

    public static String toHexString (byte[] bytes) {
        final StringBuilder sb = new StringBuilder("[");
        final int len = bytes.length;
        for (int i = 0; ; i++) {
            sb.append("%02x".formatted(bytes[i]));
            if (i >= bytes.length - 1) {
                return sb.append("]").toString();
            }
            sb.append(", ");
        }
    }

    public static byte[] extendArray (byte[] srcArr, byte[] addedArr) {
        final int oldLen = srcArr.length;
        final int addLen = addedArr.length;
        srcArr = Arrays.copyOf(srcArr, oldLen + addLen);
        System.arraycopy(addedArr, 0, srcArr, oldLen - 1, addLen);
        return srcArr;
    }
}
