package top.kkoishi.easy.swing;

import top.kkoishi.easy.Main;
import top.kkoishi.concurrent.DefaultThreadFactory;
import top.kkoishi.easy.net.client.FeedBackClient;
import top.kkoishi.io.FileChooser;
import top.kkoishi.io.Files;
import top.kkoishi.io.OptionLoader;
import top.kkoishi.lang.PropertiesLoader;
import top.kkoishi.swing.JVMStateDisplay;
import top.kkoishi.swing.PopMenu;
import top.kkoishi.util.EnhancedTrie;
import top.kkoishi.easy.util.PassageMatcher;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultHighlighter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_M;
import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_O;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_X;
import static javax.swing.KeyStroke.getKeyStroke;
import static top.kkoishi.easy.Main.DATE_FORMAT;
import static top.kkoishi.easy.Main.NATIVE_FILE_LOC;
import static top.kkoishi.easy.Main.NATIVE_LANG;
import static top.kkoishi.easy.Main.PROC;

/**
 * @author KKoishi_
 */
public final class EditorComponent extends JPanel implements Runnable {

    /*-------------------------------------------------- Const Pool Start --------------------------------------------------*/

    /**
     * The path of the output log file.
     */
    public static final String OUTPUT_LOG = "./output.log";

    /**
     * Loader for FileChooser's options.
     */
    public static final OptionLoader OPTION_LOADER = new OptionLoader();

    /**
     * The path of error log file.
     */
    public static final String ERROR_LOG = "./error.log";

    /**
     * Report message in the report JOptionPane.
     */
    public static String MESSAGE_REPORT = NATIVE_LANG.getProperty("MESSAGE_REPORT");

    public static String TITLE_LOG_REPORT = NATIVE_LANG.getProperty("TITLE_LOG_REPORT");
    public static String TITLE_SAVE = NATIVE_LANG.getProperty("TITLE_SAVE");
    public static String TITLE_SAVE_AS = NATIVE_LANG.getProperty("TITLE_SAVE_AS");
    public static String TITLE_NEW_FRAME = NATIVE_LANG.getProperty("TITLE_NEW_FRAME");
    public static String TITLE_NEW_FRAME_AND_CR = NATIVE_LANG.getProperty("TITLE_NEW_FRAME_AND_CR");
    public static String TITLE_OPEN = NATIVE_LANG.getProperty("TITLE_OPEN");
    public static String TITLE_EXIT = NATIVE_LANG.getProperty("TITLE_EXIT");
    public static String TITLE_FORMAT = NATIVE_LANG.getProperty("TITLE_FORMAT");
    public static String TITLE_FONT = NATIVE_LANG.getProperty("TITLE_FONT");
    public static String TITLE_DATE = NATIVE_LANG.getProperty("TITLE_DATE");
    public static String TITLE_FILE = NATIVE_LANG.getProperty("TITLE_FILE");
    public static String TITLE_HELP = NATIVE_LANG.getProperty("TITLE_HELP");
    public static String TITLE_CLEAR_OUT = NATIVE_LANG.getProperty("TITLE_CLEAR_OUT");
    public static String TITLE_CLEAR_ERR = NATIVE_LANG.getProperty("TITLE_CLEAR_ERR");
    public static String TITLE_CLEAR_ALL = NATIVE_LANG.getProperty("TITLE_CLEAR_ALL");
    public static String TITLE_HELP_IMPL = NATIVE_LANG.getProperty("TITLE_HELP_IMPL");
    public static String TITLE_LANGUAGE = NATIVE_LANG.getProperty("TITLE_LANGUAGE");
    public static String TITLE_FRAME = NATIVE_LANG.getProperty("TITLE_FRAME");
    public static String TITLE_LOG_VIEW_OUT = NATIVE_LANG.getProperty("TITLE_LOG_VIEW_OUT");
    public static String TITLE_LOG_VIEW_ERR = NATIVE_LANG.getProperty("TITLE_LOG_VIEW_ERR");

    public static File backgroundFile = null;

    private static String fontName = Main.PROC.getProperty("font_name");


    private static final List<String> MENU_ITEM_TEXTS = new CopyOnWriteArrayList<>();

    /*-------------------------------------------------- Const Pool End --------------------------------------------------*/

    static {
        MENU_ITEM_TEXTS.addAll(Arrays.asList(TITLE_FILE, TITLE_SAVE, TITLE_SAVE_AS, TITLE_NEW_FRAME, TITLE_NEW_FRAME_AND_CR,
                TITLE_OPEN, TITLE_EXIT, TITLE_FORMAT, TITLE_FONT, TITLE_DATE, TITLE_FRAME, TITLE_LANGUAGE,
                TITLE_LOG_VIEW_OUT, TITLE_LOG_VIEW_ERR, TITLE_HELP, TITLE_LOG_REPORT, TITLE_CLEAR_OUT,
                TITLE_CLEAR_ERR, TITLE_CLEAR_ALL, TITLE_HELP_IMPL));
    }

    /*-------------------------------------------------- Static Methods Start --------------------------------------------------*/

    /**
     * Set all the properties.(Font, title, and so on.)
     *
     * @param instance EditorComponent instance.
     */
    public static void setAll (EditorComponent instance) {
        resetTitle();
        instance.refreshTitle();
        instance.changeParentTitle.accept(instance.file == null ? Main.PROC.getProperty("title") : instance.file.getName());
        instance.display.setFont(new Font(fontName, Font.PLAIN, Integer.parseInt(Main.PROC.getProperty("font_size"))));
        DATE_FORMAT.applyPattern(Main.PROC.getProperty("date_format"));
    }

    /**
     * Reset all the text of the JMenu and JMenuItem instance.
     */
    public static void resetTitle () {
        final Properties cpy = new Properties(NATIVE_LANG);
        NATIVE_LANG.clear();
        try {
            NATIVE_LANG.load(new FileInputStream(NATIVE_FILE_LOC));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
            NATIVE_LANG.putAll(cpy);
            return;
        }
        TITLE_SAVE = NATIVE_LANG.getProperty("TITLE_SAVE");
        TITLE_SAVE_AS = NATIVE_LANG.getProperty("TITLE_SAVE_AS");
        TITLE_NEW_FRAME = NATIVE_LANG.getProperty("TITLE_NEW_FRAME");
        TITLE_NEW_FRAME_AND_CR = NATIVE_LANG.getProperty("TITLE_NEW_FRAME_AND_CR");
        TITLE_OPEN = NATIVE_LANG.getProperty("TITLE_OPEN");
        TITLE_EXIT = NATIVE_LANG.getProperty("TITLE_EXIT");
        TITLE_FORMAT = NATIVE_LANG.getProperty("TITLE_FORMAT");
        TITLE_FONT = NATIVE_LANG.getProperty("TITLE_FONT");
        TITLE_DATE = NATIVE_LANG.getProperty("TITLE_DATE");
        TITLE_FILE = NATIVE_LANG.getProperty("TITLE_FILE");
        TITLE_HELP = NATIVE_LANG.getProperty("TITLE_HELP");
        TITLE_CLEAR_OUT = NATIVE_LANG.getProperty("TITLE_CLEAR_OUT");
        TITLE_CLEAR_ERR = NATIVE_LANG.getProperty("TITLE_CLEAR_ERR");
        TITLE_CLEAR_ALL = NATIVE_LANG.getProperty("TITLE_CLEAR_ALL");
        TITLE_HELP_IMPL = NATIVE_LANG.getProperty("TITLE_HELP_IMPL");
        TITLE_LANGUAGE = NATIVE_LANG.getProperty("TITLE_LANGUAGE");
        TITLE_FRAME = NATIVE_LANG.getProperty("TITLE_FRAME");
        TITLE_LOG_REPORT = NATIVE_LANG.getProperty("TITLE_LOG_REPORT");
        TITLE_LOG_VIEW_OUT = NATIVE_LANG.getProperty("TITLE_LOG_VIEW_OUT");
        TITLE_LOG_VIEW_ERR = NATIVE_LANG.getProperty("TITLE_LOG_VIEW_ERR");
        applyChangeToMenuItemTextList(TITLE_FILE, TITLE_SAVE, TITLE_SAVE_AS, TITLE_NEW_FRAME, TITLE_NEW_FRAME_AND_CR,
                TITLE_OPEN, TITLE_EXIT, TITLE_FORMAT, TITLE_FONT, TITLE_DATE, TITLE_FRAME, TITLE_LANGUAGE,
                TITLE_LOG_VIEW_OUT, TITLE_LOG_VIEW_ERR, TITLE_HELP, TITLE_LOG_REPORT, TITLE_CLEAR_OUT,
                TITLE_CLEAR_ERR, TITLE_CLEAR_ALL, TITLE_HELP_IMPL);
    }

    /**
     * Change texts list.
     *
     * @param texts new texts.
     */
    private static void applyChangeToMenuItemTextList (String... texts) {
        if (texts.length != MENU_ITEM_TEXTS.size()) {
            throw new IllegalArgumentException("Require array length is " + MENU_ITEM_TEXTS.size() + " , but got " + texts.length + ".");
        } else {
            MENU_ITEM_TEXTS.clear();
            MENU_ITEM_TEXTS.addAll(Arrays.asList(texts));
        }
    }

    /**
     * Get a text pane with correct properties.
     *
     * @return instance of JTextPane.
     */
    public static JTextPane getTextPane () {
        JTextPane display = new JTextPane();
        display.setRequestFocusEnabled(true);
        display.setHighlighter(new DefaultHighlighter());
        display.setFont(new Font(fontName, Font.PLAIN, Integer.parseInt(Main.PROC.getProperty("font_size"))));
        display.setSelectionColor(Color.PINK);
        display.setCaretColor(Color.MAGENTA);
        if (Boolean.parseBoolean(Main.PROC.getProperty("tab"))) {
            display.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped (KeyEvent e) {
                    if (e.getKeyChar() == '\t') {
                        final int pos = display.getCaretPosition();
                        display.select(pos - 1, pos);
                        display.replaceSelection(" ".repeat(Integer.parseInt(Main.PROC.getProperty("replace_tab"))));
                    }
                }
            });
        }
        display.setAutoscrolls(true);
        display.setOpaque(true);
        System.out.println("Load text pane succ:" + display);
        return display;
    }

    public static BufferedImage adjustSize (final BufferedImage image, int w, int h) {
        BufferedImage cpy = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        cpy.getGraphics().drawImage(image, 0, 0, w, h, null);
        return cpy;
    }

    private static boolean adjustRequire (BufferedImage src, int w, int h) {
        return src.getWidth() > w || src.getHeight() > h;
    }

    /*-------------------------------------------------- Static Method End --------------------------------------------------*/

    /*-------------------------------------------------- Field Start --------------------------------------------------*/

    private File file = null;

    private final List<Consumer<String>> applierMenuItemTexts = new CopyOnWriteArrayList<>();

    private final JTextPane display = getTextPane();

    private final PassageMatcher matcher = PassageMatcher.getInstance(display.getText());

    private final LinkedList<Character> buffer = new LinkedList<>();

    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new DefaultThreadFactory());

    private final PopMenu tip = new PopMenu("Tips");

    private final JVMStateDisplay jvmStateDisplay = new JVMStateDisplay();

    private final JMenuBar bar = new JMenuBar();

    private Consumer<String> changeParentTitle;

    private boolean isAlive = true;

    private int pos = 0;

    /*-------------------------------------------------- Field End --------------------------------------------------*/

    public Consumer<String> getChangeParentTitle () {
        return changeParentTitle;
    }

    public void setChangeParentTitle (Consumer<String> changeParentTitle) {
        this.changeParentTitle = changeParentTitle;
    }


    public void setFontName (String familyName) {
        fontName = familyName;
        System.out.println("Set font name to" + familyName);
    }

    public boolean isAlive () {
        return isAlive;
    }

    public void kill () {
        isAlive = false;
        System.out.println("Closing the frame");
    }

    public void clear () {
        executor.shutdown();
        matcher.clear();
        removeAll();
        System.out.println("Succ to uninstall comp and threads.");
    }

    public void loadFile (File file) {
        try {
            display.setText(Files.read(file));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Main.start(Main.title);
            isAlive = false;
        }
        matcher.flush(display.getText());
        this.file = file;
        System.gc();
        System.out.println("Success to load file:" + file);
    }

    /**
     * Creates a new <code>EditorComponent</code> with a double buffer
     * and a flow layout.
     */
    public EditorComponent () {
        super(new BorderLayout());
        tip.action = this::replaceBuffer;
        initBar();
        loadComp();
        try {
            reloadOptions();
        } catch (IOException | PropertiesLoader.IllegalOrBadPropertyFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void replaceBuffer (Object o) {
        pos = display.getCaretPosition();
        final String word = tip.getSelect();
        if (word != null) {
            display.select(pos - buffer.size(), pos);
            System.out.println("Select buffer:" + display.getSelectedText());
            display.replaceSelection(word);
            buffer.clear();
            System.out.println("Replace Buffer!");
        }
        tip.setVisible(false);
    }

    private void initBar () {
        bar.setFont(new Font(Font.DIALOG, Font.ITALIC, 13));
        bar.setBackground(Color.WHITE);
        bar.setBorder(new LineBorder(new Color(236, 173, 173), 1, true));
        final JMenu fileMenu = new JMenu(TITLE_FILE) {{
            add(createItem(TITLE_SAVE, e -> save(), getKeyStroke(VK_S, CTRL_DOWN_MASK)));
            add(createItem(TITLE_SAVE_AS, e -> saveAs(), getKeyStroke(VK_S, SHIFT_DOWN_MASK)));
            addSeparator();
            add(createItem(TITLE_NEW_FRAME, e -> Main.start(Main.title), getKeyStroke(VK_N, SHIFT_DOWN_MASK)));
            add(createItem(TITLE_NEW_FRAME_AND_CR, e -> newFile(), getKeyStroke(VK_N, CTRL_DOWN_MASK)));
            add(createItem(TITLE_OPEN, e -> openFile(), getKeyStroke(VK_O, CTRL_DOWN_MASK)));
            add(createItem(TITLE_EXIT, e -> System.exit(514), getKeyStroke(VK_X, ALT_DOWN_MASK)));
        }};
        applierMenuItemTexts.add(0, fileMenu::setText);
        bar.add(fileMenu);
        bar.add(createMenu(TITLE_FORMAT, createItem(TITLE_FONT, EditorComponent.this::format, getKeyStroke(VK_F, ALT_DOWN_MASK)),
                createItem(TITLE_DATE, e -> insertDate(Main.PROC.getProperty("date_format")), getKeyStroke(VK_D, ALT_DOWN_MASK))
        ));
        bar.add(createMenu(TITLE_FRAME, createItem(TITLE_LANGUAGE, e -> switchLanguage(), getKeyStroke(VK_S, ALT_DOWN_MASK)),
                createItem(TITLE_LOG_VIEW_OUT, e -> showNoEdit(Files.openAsText(new File("./output.log"))), getKeyStroke(VK_P, CTRL_DOWN_MASK)),
                createItem(TITLE_LOG_VIEW_ERR, e -> showNoEdit(Files.openAsText(new File("./error.log"))), getKeyStroke(VK_P, ALT_DOWN_MASK))));
        bar.add(createMenu(TITLE_HELP, createItem(TITLE_LOG_REPORT, e -> report(), getKeyStroke(VK_M, ALT_DOWN_MASK)),
                createItem(TITLE_CLEAR_OUT, e -> clearFile(OUTPUT_LOG), getKeyStroke(VK_L, CTRL_DOWN_MASK)),
                createItem(TITLE_CLEAR_ERR, e -> clearFile(ERROR_LOG), getKeyStroke(VK_L, SHIFT_DOWN_MASK)),
                createItem(TITLE_CLEAR_ALL, e -> clearFiles(OUTPUT_LOG, ERROR_LOG), getKeyStroke(VK_C, ALT_DOWN_MASK)),
                createItem(TITLE_HELP_IMPL, e -> showNoEdit(getHelpDoc()), getKeyStroke(VK_H, ALT_DOWN_MASK))
        ));
    }


    /**
     * Refresh the parent container's title.
     */
    public void refreshTitle () {
        final Iterator<Consumer<String>> actions = applierMenuItemTexts.iterator();
        final Iterator<String> args = MENU_ITEM_TEXTS.iterator();
        while (actions.hasNext() && args.hasNext()) {
            final Consumer<String> action = actions.next();
            final String arg = args.next();
            System.out.println(arg + "->" + action);
            action.accept(arg);
        }
    }

    private String getHelpDoc () {
        try {
            final BufferedReader reader = new BufferedReader(new FileReader("./data/help." + Main.PROC.getProperty("language_file")));
            final StringBuilder sb = new StringBuilder();

            while (true) {
                final String buffer = reader.readLine();
                if (buffer != null) {
                    sb.append(buffer).append('\n');
                } else {
                    break;
                }
            }
            return sb.toString();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e);
            e.printStackTrace();
            return "Failed to load help doc.";
        }
    }

    private void report () {
        final String initialMsg = JOptionPane.showInputDialog(this, MESSAGE_REPORT, "initial Message");
        final String[] args = initialMsg == null ? new String[0] : new String[]{initialMsg};
        final var task = new TaskDisplay("Report System", null);
        task.setAction(o -> {
            FeedBackClient.main(args);
            TaskDisplay.setDispose(task, true);
        });
        new Thread(task).start();
    }

    private void showNoEdit (String text) {
        new MessageDisplayFrame("Read Only Viewer", MessageDisplayFrame.getPureTextDocument(text));
    }

    private void switchLanguage () {
        System.out.println("Changing language...");
        if ("en".equals(Main.PROC.getProperty("language_file"))) {
            Main.PROC.setProperty("language_file", "cn");
        } else {
            Main.PROC.setProperty("language_file", "en");
        }
        try {
            Main.writeProc();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
            return;
        }
        final Properties properties = new Properties();
        properties.putAll(PROC);
        System.out.println(properties.getProperty("language_file"));
        Main.PROC.clear();
        resetTitle();
        try {
            PROC.load(new FileInputStream("./data/proc"));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
            Main.PROC.putAll(properties);
            return;
        }
        NATIVE_FILE_LOC = "./data/native." + PROC.getProperty("language_file");
        System.out.println(NATIVE_FILE_LOC);
        resetTitle();
        refreshTitle();
        JOptionPane.showMessageDialog(null, "Succ to set language.\n成功设置语言.");
    }

    private void openFile () {
        try {
            final File file = FileChooser.file(null, "top.kkoishi.easy.FileChooser", OptionLoader.list2array(OPTION_LOADER.getOptions()));
            if (file != null) {
                this.file = file;
                changeParentTitle.accept(file.getName());
                loadFile(file);
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void reloadOptions () throws IOException, PropertiesLoader.IllegalOrBadPropertyFormatException {
        OPTION_LOADER.load(new File("./data/extensions." + Main.PROC.getProperty("language_file")));
        OPTION_LOADER.translate();
    }

    private void insertDate (String dateFormat) {
        final int pos = display.getCaretPosition();
        display.select(pos, pos);
        display.replaceSelection(getDate(dateFormat));
    }

    private String getDate (String dateFormat) {
        Main.DATE_FORMAT.applyPattern(dateFormat);
        return Main.DATE_FORMAT.format(new Date(System.currentTimeMillis()));
    }

    private void format (ActionEvent e) {
        //TODO:finish format frame.
    }

    private void clearFile (String path) {
        try {
            Files.write(path, "");
            JOptionPane.showMessageDialog(EditorComponent.this, "Successfully clear the file:" + path);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(EditorComponent.this, ex.getMessage());
        }
    }

    private void clearFiles (String... paths) {
        try {
            for (String path : paths) {
                Files.write(path, "");
            }
            JOptionPane.showMessageDialog(EditorComponent.this, "Successfully clear the files:" + Arrays.toString(paths));
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(EditorComponent.this, ex.getMessage());
        }
    }

    private void save () {
        try {
            if (file == null) {
                saveAs();
                return;
            }
            saveAs(null);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Can not save file:" + ex.getMessage());
        }
    }

    private void newFile () {
        File f = null;
        try {
            f = FileChooser.dir(null, "Choose the dir", "Directory");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        if (f == null) {
            return;
        }
        String name = JOptionPane.showInputDialog("input file name");
        final File s = new File(f.getAbsolutePath() + '/' + name);
        if (!s.exists()) {
            try {
                System.out.println("Create new file:" + s.createNewFile());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Can not create file:" + ex.getMessage());
            }
            System.out.println("Staring new frame.");
            Main.start(s.getName(), s);
        } else {
            JOptionPane.showMessageDialog(null, "Can not save file:" + s + " has already exist!");
        }
    }

    private void saveAs () {
        try {
            File f = FileChooser.dir(null, "Choose the dir", "Directory");
            if (f == null) {
                return;
            }
            String name = JOptionPane.showInputDialog("input file name");
            final File s = new File(f.getAbsolutePath() + '/' + name);
            if (!s.exists()) {
                saveAs(s);
                JOptionPane.showMessageDialog(null, "Success to save the file!");
            } else {
                JOptionPane.showMessageDialog(null, "Can not save file:" + s + " has already exist!");
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null, "Can not save file:" + ioException.getMessage());
        }
    }

    private JMenu createMenu (String menuName, JMenuItem... items) {
        final JMenu menu = new JMenu(menuName) {{
            Arrays.stream(items).forEach(this::add);
        }};
        final int insertPos = applierMenuItemTexts.size() - items.length;
        applierMenuItemTexts.add(insertPos, menu::setText);
        return menu;
    }

    private JMenuItem createItem (String title, ActionListener anAction, KeyStroke keyStroke) {
        System.out.println("Creating MenuItem:" + title + "->Key Stroke:" + keyStroke);
        final JMenuItem item = new JMenuItem(title) {{
            registerKeyboardAction(anAction, keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
            addActionListener(anAction);
            setBackground(Color.WHITE);
        }};
        applierMenuItemTexts.add(item::setText);
        return item;
    }

    private void saveAs (File to) throws IOException {
        System.out.println("Start save file.");
        if (to == null) {
            Files.write(file, display.getText());
        } else {
            Files.write(to, display.getText());
        }
        System.out.println("Succ to save!");
    }

    public JMenuBar getBar () {
        return bar;
    }

    private void loadComp () {
        System.out.println("Loading comp...");
        final JScrollPane pane = new JScrollPane(display);
        add(pane, BorderLayout.CENTER);
        add(jvmStateDisplay, BorderLayout.SOUTH);
        display.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped (KeyEvent e) {
                super.keyTyped(e);
                keyType(e);
            }
        });
        display.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked(e);
                mouseClick();
            }
        });
        System.out.println("Initialing threads...");
        executor.scheduleAtFixedRate(this, 10000, 10000, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(jvmStateDisplay.get(), 0, 30, TimeUnit.MILLISECONDS);
        System.out.println("Finish init editor core!");
    }

    private void mouseClick () {
        int nPos = display.getCaretPosition();
        if (nPos == 0) {
            buffer.clear();
            return;
        }
        if (nPos != pos + 1) {
            buffer.clear();
            pos = nPos;
            {
                final LinkedList<Character> temp = new LinkedList<>();
                char c = display.getText().charAt(nPos - 1);
                while (pos >= 0 && (EnhancedTrie.range(c))) {
                    c = display.getText().charAt(nPos - 1);
                    temp.offer(c);
                    --nPos;
                }
                while (!temp.isEmpty()) {
                    buffer.offer(temp.removeLast());
                }
                if (buffer.isEmpty()) {
                    return;
                }
                buffer.removeFirst();
                System.out.println("Reset buffer to:" + buffer);
            }
            matcher.flush(display.getText());
        }
    }

    private void keyType (KeyEvent e) {
        tip.setVisible(false);
        final char keyChar = e.getKeyChar();
        if ((keyChar >= 'a' && keyChar <= 'z') || (keyChar >= 'A' && keyChar <= 'Z')) {
            ++pos;
            buffer.offer(keyChar);
            if (!buffer.isEmpty()) {
                final char[] cs = new char[buffer.size()];
                int i = 0;
                for (Character c : buffer) {
                    cs[i++] = c;
                }
                System.out.println(cs);
                System.out.println(matcher.predict(new String(cs)));
                Point p = display.getCaret().getMagicCaretPosition();
                if (p == null) {
                    p = new Point(0, 15);
                }
                tip.setWords(matcher.predict(new String(cs)));
                tip.setFocusable(false);
                tip.show(display, p.x, p.y);
            }
        } else {
            if (keyChar == '\b') {
                if (buffer.isEmpty()) {
                    matcher.flush(display.getText());
                    return;
                }
                buffer.removeLast();
                return;
            }
            if (keyChar == ' ' || keyChar == '\t' || keyChar == '\n') {
                System.out.println("Flush Buffer");
                matcher.offer(buffer);
                buffer.clear();
                return;
            }
            final int nPos = display.getCaretPosition();
            if (nPos != pos + 1 && nPos - pos >= buffer.size()) {
                matcher.flush(display.getText());
            }
            buffer.clear();
            pos = display.getCaretPosition();
        }
    }

    /*-------------------------------------------------- Override Start --------------------------------------------------*/

    /*-------------------------------------------------- Component Start --------------------------------------------------*/

    @Override
    public void paint (Graphics g) {
        super.paint(g);
        if (backgroundFile != null) {
            try {
                final BufferedImage img = ImageIO.read(backgroundFile);
                final int w = getWidth(), h = getHeight();
                final int imgW = img.getWidth(), imgH = img.getHeight();
                if (adjustRequire(img, w, h)) {
                    final float rate = (float) imgW / imgH;
                    final float panelRate = (float) w / h;
                    if (panelRate - rate >= 0.00001f) {
                        g.drawImage(adjustSize(img, (int) (w * rate), h), (int) ((w - w * rate) / 2), 0, null);
                    } else {
                        g.drawImage(adjustSize(img, w, (int) (h * rate)), w, (int) ((h - h * rate) / 2), null);
                    }
                } else {
                    g.drawImage(img, (w - imgW) / 2, (h - imgH) / 2, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*-------------------------------------------------- Component End --------------------------------------------------*/

    /*-------------------------------------------------- Runnable Start --------------------------------------------------*/

    @Override
    public void run () {
        matcher.flush(display.getText());
    }

    /*-------------------------------------------------- Runnable End --------------------------------------------------*/

    /*-------------------------------------------------- Override End --------------------------------------------------*/
}
