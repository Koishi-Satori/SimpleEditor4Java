package top.kkoishi.easy;

import top.kkoishi.concurrent.DefaultThreadFactory;
import top.kkoishi.easy.net.client.FeedBackClient;
import top.kkoishi.io.ZipFiles;
import top.kkoishi.easy.swing.EditorComponent;
import top.kkoishi.util.KoishiLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class
 *
 * @author KKoishi_
 */
public final class Main implements Runnable {

    static final List<Image> ICONS = new LinkedList<>();

    private static final ScheduledThreadPoolExecutor POOL_EXECUTOR = new ScheduledThreadPoolExecutor(2, new DefaultThreadFactory());

    public static volatile KoishiLogger out = KoishiLogger.getInstance(System.out);

    public static volatile KoishiLogger err = KoishiLogger.getInstance(System.err);

    public static final Properties PROC = new Properties();

    public static final Properties NATIVE_LANG = new Properties();

    public static final String DATA_PROC = "./data/proc";

    public static String NATIVE_FILE_LOC = "./data/native.en";

    public static String title = PROC.getProperty("title");

    private static final CopyOnWriteArrayList<EditorComponent> TASKS = new CopyOnWriteArrayList<>();

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();

    public static LookAndFeel JDK_DEFAULT_LOOK_AND_FEEL;

    public static void writeProc () throws IOException {
        PROC.store(new FileOutputStream(DATA_PROC), null);
    }

    static {
        KoishiLogger.toFile = true;
        KoishiLogger.outFile = "./output.log";
        KoishiLogger.errFile = "./error.log";
        KoishiLogger.override = false;
        err.useErr = true;
        System.setOut(out);
        System.setErr(err);
        try {
            JDK_DEFAULT_LOOK_AND_FEEL = UIManager.createLookAndFeel("Metal");
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        try {
            PROC.load(new FileInputStream(DATA_PROC));
        } catch (IOException e) {
            e.printStackTrace();
            PROC.clear();
            JOptionPane.showMessageDialog(null, e.getMessage());
            PROC.put("tab", "false");
            PROC.put("replace_tab", "4");
            PROC.put("font_size", 15);
            PROC.put("font_name", "Segoe UI");
            PROC.put("pre_x", "650");
            PROC.put("pre_y", "400");
            PROC.put("title", "Editor with Auto Complete Powered by KKoishi_");
            PROC.put("language_file", "en");
        }
        NATIVE_FILE_LOC = "./data/native." + PROC.getProperty("language_file");
        try {
            NATIVE_LANG.load(new FileInputStream(NATIVE_FILE_LOC));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            NATIVE_FILE_LOC = "./data/native.en";
            try {
                NATIVE_LANG.load(new FileInputStream(NATIVE_FILE_LOC));
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        FeedBackClient.setAction(e -> JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE));
        final File tempDir = new File("./data/temp");
        System.out.println("Decode data in dat file state:" + ZipFiles.decompress(new File("./icons.dat"), "./data/temp"));
        for (File file : Objects.requireNonNull(tempDir.listFiles())) {
            try {
                ICONS.add(ImageIO.read(file));
            } catch (IOException e) {
                e.printStackTrace(System.err);
            } finally {
                System.out.println("Delete temp file state:" + file.delete());
            }
        }
        for (File file : Objects.requireNonNull(tempDir.listFiles())) {
            file.deleteOnExit();
        }
        title = PROC.getProperty("title");
    }

    public static void main (String[] args) {
        if (args.length == 0) {
            start(title);
        } else {
            System.out.println("Env:try open file");
            final File file = new File(args[0]);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "Failed to open file:" + file + "\nThe file does not exist!");
                start(title);
            } else {
                start(file.getName()).loadFile(file);
            }
        }
        System.out.println("Start env threads.");
        POOL_EXECUTOR.scheduleAtFixedRate(new Main(), 100, 25, TimeUnit.MILLISECONDS);
        POOL_EXECUTOR.scheduleAtFixedRate(System::gc, 6000, 25000, TimeUnit.MILLISECONDS);
    }

    public static EditorComponent start (String title, File file) {
        final EditorComponent component = start(title);
        component.loadFile(file);
        return component;
    }

    public static EditorComponent start (String title) {
        final EditorComponent component = new EditorComponent();
        final JFrame jf = new JFrame(title);
        component.setChangeParentTitle(jf::setTitle);
        jf.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        jf.setFont(new Font(PROC.getProperty("font_name"), Font.PLAIN, 12));
        if (!ICONS.isEmpty()) {
            jf.setIconImages(ICONS);
            System.out.println("Successfully load icons!");
        } else {
            System.out.println("Failed to load icons.");
        }
        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing (WindowEvent e) {
                component.kill();
                jf.removeAll();
                jf.dispose();
                super.windowClosing(e);
            }
        });
        jf.add(component);
        jf.setJMenuBar(component.getBar());
        TASKS.add(component);
        jf.setSize(Integer.parseInt(PROC.getProperty("pre_x")), Integer.parseInt(PROC.getProperty("pre_y")));
        jf.setVisible(true);
        return component;
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
        final Queue<EditorComponent> components = new LinkedList<>();
        for (EditorComponent task : TASKS) {
            if (task.isAlive()) {
                continue;
            }
            task.clear();
            components.offer(task);
        }
        if (!components.isEmpty()) {
            TASKS.removeAll(components);
        }
        if (TASKS.isEmpty()) {
            System.exit(514);
        }
    }

    public static CopyOnWriteArrayList<EditorComponent> getTasks () {
        return TASKS;
    }

    public static void refreshAllTitle () {
        TASKS.forEach(EditorComponent::refreshTitle);
    }
}
