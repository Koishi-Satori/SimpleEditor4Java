package top.kkoishi.io;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ZipFiles {
    private ZipFiles () {
    }

    /**
     * Partition method which is invoked by compress method.
     *
     * @param dir     directory file
     * @param pointer zip stream
     * @return if successes
     * @see ZipFiles#compress(List, String, String)
     */
    private static boolean compressDir (File dir, ZipOutputStream pointer) {
        boolean flag = true;
        try {
            assert dir.isDirectory();
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        flag &= compressDir(file, pointer);
                    } else {
                        pointer.putNextEntry(new ZipEntry(file.getParentFile().getName() + "/" + file.getName()));
                        FileInputStream fis = new FileInputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = fis.read(buffer)) != -1) {
                            pointer.write(buffer, 0, len);
                        }
                    }
                }
            } else {
                pointer.putNextEntry(new ZipEntry(dir.getName()));
            }
        } catch (IOException e) {
            return false;
        }
        return flag;
    }

    /**
     * Compress a zip file which resource is a file list,
     * and the location of the zip file must be defined
     *
     * @param in     input files
     * @param outDir zip file's directory
     * @param name   name of the zip file
     * @return true is compress successes, or false.
     */
    public static boolean compress (List<File> in, String outDir, String name) {
        //flag for showing if recursion success.
        boolean flag = true;
        try {
            File out = new File(outDir + "/" + name);
            //clear file content if exists.
            if (out.exists()) {
                FileOutputStream fos = new FileOutputStream(out);
                fos.write(new byte[0]);
                fos.flush();
                fos.close();
            }
            //create zip stream
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outDir + "/" + name));
            for (File file : in) {
                if (file.isDirectory()) {
                    //invoke partition method if the file is a directory.
                    flag &= compressDir(file, zos);
                } else {
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    FileInputStream fis = new FileInputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, len);
                    }
                    // zos.flush();
                    fis.close();
                }
            }
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return flag;
    }

    public static boolean decompress (File in, String outDir) {
        try {
            new File(outDir).mkdirs();
            ZipFile zipFile = new ZipFile(in);
            ZipInputStream zis = new ZipInputStream(new FileInputStream(in));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File out = new File(outDir + "/" + entry.getName());
                out.getParentFile().mkdirs();
                out.createNewFile();
                InputStream stream = zipFile.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(out);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = stream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                stream.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean decompress (File in) {
        return decompress(in, in.getParent());
    }
}
