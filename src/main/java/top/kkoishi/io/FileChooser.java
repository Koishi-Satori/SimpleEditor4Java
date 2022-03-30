package top.kkoishi.io;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class FileChooser implements Serializable {

    public static class Option {
        private String info;
        private String[] extensions;

        private Option (String info, String... extensions) {
            this.info = info;
            this.extensions = extensions;
        }

        @Override
        public String toString () {
            return "Option{" +
                    "info='" + info + '\'' +
                    ", extensions=" + Arrays.toString(extensions) +
                    '}';
        }
    }

    public static Option buildOption (String info, String... extensions) {
        return new Option(info, extensions);
    }

    private FileChooser () {
    }

    private static String toString (String[] options) {
        final int len = options.length - 1;
        final StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < len; i++) {
            sb.append(options[i]).append(',');
        }
        return sb.append(options[len]).append(')').toString();
    }

    public static <E> List<E> array2List (E[] es) {
        if (es.length == 0) {
            return new CopyOnWriteArrayList<>();
        }
        return new CopyOnWriteArrayList<>(Arrays.asList(es));
    }

    public static File file (Component father, String title, Option... options)
            throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        final JFileChooser fc = new JFileChooser();
        for (Option o : options) {
            final FileNameExtensionFilter filter = new FileNameExtensionFilter(o.info + toString(o.extensions), o.extensions);
            fc.addChoosableFileFilter(filter);
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle(title);
        if (fc.showOpenDialog(father) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
    }

    public static File[] files (Component father, String title, Option... options) {
        final JFileChooser fc = new JFileChooser();
        for (Option o : options) {
            final FileNameExtensionFilter filter = new FileNameExtensionFilter(o.info + toString(o.extensions), o.extensions);
            fc.addChoosableFileFilter(filter);
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(true);
        fc.setDialogTitle(title);
        if (fc.showOpenDialog(father) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFiles();
        }
        return new File[0];
    }

    public static String[] filesPath (Component father, String title, String info, String... extensions) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(info, extensions);
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(title);
        if (chooser.showOpenDialog(father) == JFileChooser.APPROVE_OPTION) {
            final File[] fs = chooser.getSelectedFiles();
            final int len = fs.length;
            String[] paths = new String[len];
            for (int i = 0; i < len; i++) {
                paths[i] = fs[i].getAbsolutePath();
            }
            return paths;
        }
        return new String[0];
    }

    public static String filePath (Component father, String title, String info, String... extensions) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(info, extensions);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(title);
        if (chooser.showOpenDialog(father) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public static File file (Component father, String title, String info, String... extensions) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(info, extensions);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(title);
        if (chooser.showOpenDialog(father) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public static File[] files (Component father, String title, String info, String... extensions) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(info, extensions);
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle(title);
        if (chooser.showOpenDialog(father) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFiles();
        }
        return new File[0];
    }

    public static String dirPath (Component father, String title, String info) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(title);
        if (chooser.showOpenDialog(father) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public static File dir (Component father, String title, String info) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(title);
        if (chooser.showOpenDialog(father) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public static String[] dirsPath (Component father, String title, String info) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(title);
        if (chooser.showOpenDialog(father) == JFileChooser.APPROVE_OPTION) {
            final File[] fs = chooser.getSelectedFiles();
            List<String> list = new ArrayList<>();
            for (File f : fs) {
                list.add(f.getAbsolutePath());
            }
            return list.toArray(new String[0]);
        }
        return new String[0];
    }

    public static File[] dirs (Component father, String title, String info) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(title);
        if (chooser.showOpenDialog(father) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFiles();
        }
        return new File[0];
    }
}
