package top.kkoishi.easy.net.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class ClientLogSeparator {

    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyy.MM.dd_hh-mm-ss");

    public static final int MS_A_DAY = 1000 * 24 * 3600;

    final long time = System.currentTimeMillis();

    final File logFile;

    final File outFile;

    long sep = MS_A_DAY;

    public long getSep () {
        return sep;
    }

    public void setSep (long sep) {
        this.sep = sep;
    }

    public ClientLogSeparator (File logFile, File outFile) {
        this.logFile = logFile;
        this.outFile = outFile;
    }

    public void handle () throws IOException, ParseException {
        handle0(new BufferedReader(new FileReader(logFile)), new BufferedWriter(new FileWriter(outFile)));
    }

    private void handle0 (BufferedReader reader, BufferedWriter writer) throws IOException, ParseException {
        System.out.println("Start handle0:" + reader);
        boolean succ = false;
        while (true) {
            final String line = reader.readLine();
            if (line == null) {
                break;
            }
            try {
                if (rightFormat(line)) {
                    succ = true;
                    writer.append(line).append('\n');
                } else {
                    succ = false;
                }
            } catch (StringIndexOutOfBoundsException e) {
                if (succ) {
                    writer.append(line).append('\n');
                }
                e.printStackTrace();
            }
        }
        reader.close();
        writer.close();
    }

    private boolean rightFormat (String line) throws ParseException {
        String date = line.substring(line.indexOf(']') + 2);
        date = date.substring(0, date.indexOf(']'));
        return StrictMath.abs(FORMAT.parse(date).
                getTime() - time) <= sep;
    }

    public File getSeparatorFile () {
        return outFile;
    }
}
