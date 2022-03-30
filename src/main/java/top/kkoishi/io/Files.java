package top.kkoishi.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * @author KKoishi_
 */
public final class Files {

    private static final int BUFFER_SIZE = 1 << 13;
    public static final String SHELL_EXTENSION = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win") ? ".bat" : "sh";

    public static String openAsText (File f) {
        try {
            final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            final StringBuilder sb = new StringBuilder();
            int len;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((len = bis.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
            bis.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String read (File f) throws IOException {
        try {
            return openAsText(f);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static Stream<String> readStream (String path) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(path));
        return reader.lines();
    }


    public static Stream<String> readStream (File file) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        return reader.lines();
    }


    public static byte[] readRaw (String path) throws IOException {
        final FileInputStream fis = new FileInputStream(path);
        byte[] bs = fis.readAllBytes();
        fis.close();
        return bs;
    }


    public static byte[] readRaw (File file) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        byte[] bs = fis.readAllBytes();
        fis.close();
        return bs;
    }


    public static void write (String path, String content) throws IOException {
        final BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        bw.write(content);
        bw.close();
    }


    public static void write (String path, byte[] bytes) throws IOException {
        final FileOutputStream fos = new FileOutputStream(path);
        fos.write(bytes);
        fos.close();
    }


    public static void write (File file, String content) throws IOException {
        final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file, false));
        bos.write(content.getBytes(StandardCharsets.UTF_8));
        bos.close();
    }


    public static void write (File file, byte[] bytes) throws IOException {
        final FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
    }

    public static void append (String path, String content) throws IOException {
        final BufferedWriter bw = new BufferedWriter(new FileWriter(path, true));
        bw.write(content);
        bw.close();
    }

    public static void append (String path, byte[] bytes) throws IOException {
        final FileOutputStream fos = new FileOutputStream(path, true);
        fos.write(bytes);
        fos.close();
    }


    public static void append (File file, String content) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        bw.write(content);
        bw.close();
    }


    public static void append (File file, byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write(bytes);
        fos.close();
    }

    public static boolean copyAndGet (String src, String to) {
        try {
            copy(src, to);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void copy (String src, String to) throws IOException {
        final FileInputStream fis = new FileInputStream(src);
        final FileOutputStream fos = new FileOutputStream(src);
        int len;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((len = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fis.close();
        fos.flush();
        fos.close();
    }

    public static void delete (File f) {
        if (f.isDirectory()) {
            final File[] fs = f.listFiles();
            if (fs == null) {
                return;
            }
            for (File file : fs) {
                delete(file);
            }
        } else {
            if (f.exists()) {
                f.delete();
            }
        }
    }
}
