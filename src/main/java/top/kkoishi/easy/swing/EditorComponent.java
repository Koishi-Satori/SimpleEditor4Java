package top.kkoishi.easy.swing;

import top.kkoishi.easy.Main;
import top.kkoishi.concurrent.DefaultThreadFactory;
import top.kkoishi.easy.lang.Unicode;
import top.kkoishi.easy.net.client.FeedBackClient;
import top.kkoishi.io.FileChooser;
import top.kkoishi.io.Files;
import top.kkoishi.io.OptionLoader;
import top.kkoishi.lang.PropertiesLoader;
import top.kkoishi.swing.IconButton;
import top.kkoishi.swing.JVMStateDisplay;
import top.kkoishi.swing.PopMenu;
import top.kkoishi.util.EnhancedTrie;
import top.kkoishi.easy.util.PassageMatcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import static java.awt.event.KeyEvent.*;
import static javax.swing.KeyStroke.getKeyStroke;
import static top.kkoishi.easy.Main.DATE_FORMAT;
import static top.kkoishi.easy.Main.NATIVE_FILE_LOC;
import static top.kkoishi.easy.Main.NATIVE_LANG;
import static top.kkoishi.easy.Main.PROC;
import static top.kkoishi.easy.Main.writeProc;

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
    public static String TITLE_TRANSLATE_UNICODE = NATIVE_LANG.getProperty("TITLE_TRANSLATE_UNICODE");
    public static String TITLE_DECODE_UNICODE = NATIVE_LANG.getProperty("TITLE_DECODE_UNICODE");
    public static String TITLE_UNICODE = NATIVE_LANG.getProperty("TITLE_UNICODE");
    public static String TITLE_FORMAT = NATIVE_LANG.getProperty("TITLE_FORMAT");
    public static String TITLE_FONT = NATIVE_LANG.getProperty("TITLE_FONT");
    public static String TITLE_CHARSET = NATIVE_LANG.getProperty("TITLE_CHARSET");
    public static String TITLE_INSERT_DATE = NATIVE_LANG.getProperty("TITLE_INSERT_DATE");
    public static String TITLE_DATE_FORMAT = NATIVE_LANG.getProperty("TITLE_DATE_FORMAT");
    public static String TITLE_FRAME_SETTINGS = NATIVE_LANG.getProperty("TITLE_FRAME_SETTINGS");
    public static String TITLE_INSERT_TIME = NATIVE_LANG.getProperty("TITLE_INSERT_TIME");
    public static String TITLE_EDIT = NATIVE_LANG.getProperty("TITLE_EDIT");
    public static String TITLE_FIND = NATIVE_LANG.getProperty("TITLE_FIND");
    public static String TITLE_REPLACE = NATIVE_LANG.getProperty("TITLE_REPLACE");
    public static String TITLE_SELECT_ALL = NATIVE_LANG.getProperty("TITLE_SELECT_ALL");
    public static String TITLE_AUTO_SCROLL = NATIVE_LANG.getProperty(
            Boolean.parseBoolean(PROC.getProperty("auto_scroll")) ? "TITLE_AUTO_SCROLL_0" : "TITLE_AUTO_SCROLL_1");
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
    public static String TITLE_HOW_TO_USE = NATIVE_LANG.getProperty("TITLE_HOW_TO_USE");
    public static String TITLE_RELOAD_ALL = NATIVE_LANG.getProperty("TITLE_RELOAD_ALL");
    public static String TITLE_STAT = NATIVE_LANG.getProperty("TITLE_STAT");
    public static String TITLE_DICT = NATIVE_LANG.getProperty("TITLE_DICT");

    public static File backgroundFile = null;

    private static String fontName = Main.PROC.getProperty("font_name");

    private static final List<String> MENU_ITEM_TEXTS = new CopyOnWriteArrayList<>();

    /*-------------------------------------------------- Const Pool End --------------------------------------------------*/

    static {
        MENU_ITEM_TEXTS.addAll(Arrays.asList(TITLE_FILE, TITLE_NEW_FRAME, TITLE_NEW_FRAME_AND_CR, TITLE_OPEN, TITLE_SAVE,
                TITLE_SAVE_AS, TITLE_RELOAD_ALL, TITLE_TRANSLATE_UNICODE, TITLE_DECODE_UNICODE, TITLE_UNICODE, TITLE_EXIT, TITLE_FORMAT,
                TITLE_FONT, TITLE_CHARSET, TITLE_DATE_FORMAT, TITLE_AUTO_SCROLL, TITLE_FRAME_SETTINGS, TITLE_STAT, TITLE_EDIT,
                TITLE_FIND, TITLE_REPLACE, TITLE_SELECT_ALL, TITLE_INSERT_TIME, TITLE_INSERT_DATE, TITLE_FRAME,
                TITLE_LANGUAGE, TITLE_LOG_VIEW_OUT, TITLE_LOG_VIEW_ERR, TITLE_HELP, TITLE_LOG_REPORT, TITLE_CLEAR_OUT,
                TITLE_CLEAR_ERR, TITLE_CLEAR_ALL, TITLE_HELP_IMPL, TITLE_HOW_TO_USE, TITLE_DICT));
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
        TITLE_TRANSLATE_UNICODE = NATIVE_LANG.getProperty("TITLE_TRANSLATE_UNICODE");
        TITLE_DECODE_UNICODE = NATIVE_LANG.getProperty("TITLE_DECODE_UNICODE");
        TITLE_UNICODE = NATIVE_LANG.getProperty("TITLE_UNICODE");
        TITLE_EXIT = NATIVE_LANG.getProperty("TITLE_EXIT");
        TITLE_FORMAT = NATIVE_LANG.getProperty("TITLE_FORMAT");
        TITLE_FONT = NATIVE_LANG.getProperty("TITLE_FONT");
        TITLE_CHARSET = NATIVE_LANG.getProperty("TITLE_CHARSET");
        TITLE_INSERT_DATE = NATIVE_LANG.getProperty("TITLE_INSERT_DATE");
        TITLE_DATE_FORMAT = NATIVE_LANG.getProperty("TITLE_DATE_FORMAT");
        TITLE_FRAME_SETTINGS = NATIVE_LANG.getProperty("TITLE_FRAME_SETTINGS");
        TITLE_INSERT_TIME = NATIVE_LANG.getProperty("TITLE_INSERT_TIME");
        TITLE_EDIT = NATIVE_LANG.getProperty("TITLE_EDIT");
        TITLE_FIND = NATIVE_LANG.getProperty("TITLE_FIND");
        TITLE_REPLACE = NATIVE_LANG.getProperty("TITLE_REPLACE");
        TITLE_SELECT_ALL = NATIVE_LANG.getProperty("TITLE_SELECT_ALL");
        TITLE_AUTO_SCROLL = NATIVE_LANG.getProperty(
                Boolean.parseBoolean(PROC.getProperty("auto_scroll")) ? "TITLE_AUTO_SCROLL_0" : "TITLE_AUTO_SCROLL_1");
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
        TITLE_HOW_TO_USE = NATIVE_LANG.getProperty("TITLE_HOW_TO_USE");
        TITLE_RELOAD_ALL = NATIVE_LANG.getProperty("TITLE_RELOAD_ALL");
        TITLE_DICT = NATIVE_LANG.getProperty("TITLE_DICT");
        TITLE_STAT = NATIVE_LANG.getProperty("TITLE_STAT");
        applyChangeToMenuItemTextList(TITLE_FILE, TITLE_NEW_FRAME, TITLE_NEW_FRAME_AND_CR, TITLE_OPEN, TITLE_SAVE,
                TITLE_SAVE_AS, TITLE_RELOAD_ALL, TITLE_TRANSLATE_UNICODE, TITLE_DECODE_UNICODE, TITLE_UNICODE, TITLE_EXIT, TITLE_FORMAT,
                TITLE_FONT, TITLE_CHARSET, TITLE_DATE_FORMAT, TITLE_AUTO_SCROLL, TITLE_FRAME_SETTINGS, TITLE_STAT, TITLE_EDIT,
                TITLE_FIND, TITLE_REPLACE, TITLE_SELECT_ALL, TITLE_INSERT_TIME, TITLE_INSERT_DATE, TITLE_FRAME,
                TITLE_LANGUAGE, TITLE_LOG_VIEW_OUT, TITLE_LOG_VIEW_ERR, TITLE_HELP, TITLE_LOG_REPORT, TITLE_CLEAR_OUT,
                TITLE_CLEAR_ERR, TITLE_CLEAR_ALL, TITLE_HELP_IMPL, TITLE_HOW_TO_USE, TITLE_DICT);
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
        display.setAutoscrolls(Boolean.parseBoolean(PROC.getProperty("auto_scroll")));
        System.out.println("Load text pane succ:" + display + "-->AutoScroll State:" + display.getAutoscrolls());
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

    public static Font accessFont () {
        return new Font(fontName, Font.PLAIN, Integer.parseInt(Main.PROC.getProperty("font_size")));
    }


    private static void statistic (JTextComponent textComponent) {
        JOptionPane.showMessageDialog(textComponent, NATIVE_LANG.getProperty("MESSAGE_STAT") +
                textComponent.getText().length(), TITLE_STAT, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void dictEditor () {
        final EnhancedTrie trie;
        try {
            final Properties dict = new Properties(128);
            dict.load(new FileInputStream("./data/dict"));
            trie = new EnhancedTrie();
            for (final Object value : dict.values()) {
                trie.addAll((String) value);
            }
            dict.clear();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
            return;
        }
        final var words = trie.toMap();
        trie.clear();
        final var data = new Vector<String>(words.keySet()) {
            int find (String word) {
                return words.containsKey(word) ? 0 : -1;
            }
        };
        final JTextField field = new JTextField("");
        final JFrame jf = new JFrame(TITLE_DICT);
        final JList<String> wordList = new JList<>(data);
        wordList.addListSelectionListener(l -> field.setText(wordList.getSelectedValue()));
        wordList.setToolTipText(NATIVE_LANG.getProperty("MESSAGE_DICT_CUR"));
        final JButton add = new JButton(NATIVE_LANG.getProperty("MESSAGE_DICT_BUT_ADD"));
        add.addActionListener(l -> {
            final String val = field.getText();
            if (val != null && val.length() != 0) {
                if (data.find(val) != -1) {
                    JOptionPane.showMessageDialog(null, "The value " + val + " is already exists!");
                } else {
                    words.put(val, 1);
                    data.addElement(val);
                    wordList.setListData(data);
                }
            }
        });
        final JButton del = new JButton(NATIVE_LANG.getProperty("MESSAGE_DICT_BUT_DEL"));
        del.addActionListener(l -> {
            final String val = wordList.getSelectedValue();
            if (val == null) {
                return;
            }
            data.remove(val);
            wordList.setListData(data);
        });
        final JButton cancel = new JButton(NATIVE_LANG.getProperty("MESSAGE_DICT_BUT_CANCEL"));
        cancel.addActionListener(l -> {
            wordList.setListData(new String[0]);
            data.clear();
            jf.removeAll();
            jf.dispose();
            words.clear();
        });
        final JButton confirm = new JButton(NATIVE_LANG.getProperty("MESSAGE_DICT_BUT_CONFIRM"));
        confirm.addActionListener(l -> {
            final Properties nDict = new Properties(3 * data.size());
            nDict.put("size", Integer.toString(data.size()));
            int index = 0;
            for (final String datum : data) {
                nDict.put("dict_" + index++, datum);
            }
            try {
                nDict.store(new FileOutputStream("./data/dict"), null);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                nDict.clear();
                return;
            }
            nDict.clear();
            data.clear();
            wordList.setListData(new String[0]);
            jf.removeAll();
            jf.dispose();
            words.clear();
        });
        final JButton set = new JButton(NATIVE_LANG.getProperty("MESSAGE_DICT_BUT_SET"));
        set.addActionListener(l -> {
            final var sel = wordList.getSelectedValue();
            if (sel == null) {
                return;
            }
            final var val = field.getText();
            if (val != null && val.length() != 0) {
                final int index = data.find(val);
                if (index != -1) {
                    JOptionPane.showMessageDialog(null, "The value " + val + " is already exists.");
                } else {
                    data.setElementAt(val, data.indexOf(sel));
                    words.remove(sel);
                    words.put(val, 1);
                    wordList.setListData(data);
                }
            }
        });
        jf.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             *
             * @param e e
             */
            @Override
            public void windowClosing (WindowEvent e) {
                super.windowClosing(e);
                wordList.setListData(new String[0]);
                data.clear();
                jf.removeAll();
                jf.dispose();
                words.clear();
            }
        });
        final JPanel pane = new JPanel(null);
        jf.setSize(500, 300);
        final JScrollPane paneWords = new JScrollPane(wordList);
        paneWords.setBounds(25, 15, 150, 220);
        field.setBounds(200, 20, 200, 30);
        add.setBounds(200, 65, 200, 20);
        del.setBounds(200, 100, 200, 20);
        set.setBounds(200, 135, 200, 20);
        confirm.setBounds(200, 170, 200, 20);
        cancel.setBounds(200, 215, 200, 20);
        pane.add(paneWords);
        pane.add(field);
        pane.add(add);
        pane.add(del);
        pane.add(set);
        pane.add(confirm);
        pane.add(cancel);
        jf.add(pane);
        jf.setVisible(true);
    }

    private static void userManual () {
        final MessageDisplayFrame.HTMLDisplayFrame f;
        try {
            f = MessageDisplayFrame.showHtml(
                    Files.readDirectly(new File("./data/display/user_manual_en.html")),
                    "User Manual");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
            return;
        }
        f.setSize(500, 400);
        f.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             *
             * @param e e
             */
            @Override
            public void windowClosing (WindowEvent e) {
                f.removeAll();
                f.dispose();
            }
        });
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

    private Charset charset = StandardCharsets.UTF_8;

    private final FindPopup findPopup = FindPopup.getInstance(display);

    /*-------------------------------------------------- Field End --------------------------------------------------*/

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
            display.setText(Files.readDirectly(file, charset));
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
        bar.add(createMenu(TITLE_FILE, createItem(TITLE_NEW_FRAME, e -> Main.start(Main.title), getKeyStroke(VK_N, SHIFT_DOWN_MASK)),
                createItem(TITLE_NEW_FRAME_AND_CR, e -> newFile(), getKeyStroke(VK_N, CTRL_DOWN_MASK)),
                createItem(TITLE_OPEN, e -> openFile(), getKeyStroke(VK_O, CTRL_DOWN_MASK)),
                createItem(TITLE_SAVE, e -> save(), getKeyStroke(VK_S, CTRL_DOWN_MASK)),
                createItem(TITLE_SAVE_AS, e -> saveAs(), getKeyStroke(VK_S, SHIFT_DOWN_MASK)),
                createItem(TITLE_RELOAD_ALL, e -> reloadFromDisk(), getKeyStroke(VK_Y, ALT_DOWN_MASK)), null,
                createItem(TITLE_TRANSLATE_UNICODE, e -> encodeUnicode(), getKeyStroke(VK_U, ALT_DOWN_MASK)),
                createItem(TITLE_DECODE_UNICODE, e -> decodeUnicode(), getKeyStroke(VK_U, CTRL_DOWN_MASK)),
                createItem(TITLE_UNICODE, e -> unicode(), getKeyStroke(VK_U, SHIFT_DOWN_MASK)), null,
                createItem(TITLE_EXIT, e -> System.exit(514), getKeyStroke(VK_X, ALT_DOWN_MASK))));
        bar.add(createMenu(TITLE_FORMAT, createItem(TITLE_FONT, EditorComponent.this::format, getKeyStroke(VK_F, ALT_DOWN_MASK)),
                createItem(TITLE_CHARSET, e -> changeCharset(), getKeyStroke(VK_T, ALT_DOWN_MASK)),
                createItem(TITLE_DATE_FORMAT, e -> dateFormat(), getKeyStroke(VK_INSERT, SHIFT_DOWN_MASK)),
                createItem(TITLE_AUTO_SCROLL, e -> autoScroll(), getKeyStroke(VK_A, ALT_DOWN_MASK)),
                createItem(TITLE_FRAME_SETTINGS, e -> frameSetting(), getKeyStroke(VK_HOME, SHIFT_DOWN_MASK)), null,
                createItem(TITLE_STAT, e -> statistic(display), getKeyStroke(VK_I, ALT_DOWN_MASK))));
        bar.add(createMenu(TITLE_EDIT, createItem(TITLE_FIND, e -> find(), getKeyStroke(VK_F, CTRL_DOWN_MASK)),
                createItem(TITLE_REPLACE, e -> replace(), getKeyStroke(VK_R, CTRL_DOWN_MASK)),
                createItem(TITLE_SELECT_ALL, e -> display.selectAll(), getKeyStroke(VK_A, CTRL_DOWN_MASK)), null,
                createItem(TITLE_INSERT_TIME, e -> insertTime(), getKeyStroke(VK_INSERT, ALT_DOWN_MASK)),
                createItem(TITLE_INSERT_DATE, e -> insertDate(Main.PROC.getProperty("date_format")), getKeyStroke(VK_D, ALT_DOWN_MASK))));
        bar.add(createMenu(TITLE_FRAME, createItem(TITLE_LANGUAGE, e -> switchLanguage(), getKeyStroke(VK_S, ALT_DOWN_MASK)),
                createItem(TITLE_LOG_VIEW_OUT, e -> showNoEdit(Files.openOrDefault(new File("./output.log"), "Failed to open.")), getKeyStroke(VK_P, CTRL_DOWN_MASK)),
                createItem(TITLE_LOG_VIEW_ERR, e -> showNoEdit(Files.openOrDefault(new File("./error.log"), "Failed to open.")), getKeyStroke(VK_P, ALT_DOWN_MASK))));
        bar.add(createMenu(TITLE_HELP, createItem(TITLE_LOG_REPORT, e -> report(), getKeyStroke(VK_M, ALT_DOWN_MASK)),
                createItem(TITLE_CLEAR_OUT, e -> clearFile(OUTPUT_LOG), getKeyStroke(VK_L, CTRL_DOWN_MASK)),
                createItem(TITLE_CLEAR_ERR, e -> clearFile(ERROR_LOG), getKeyStroke(VK_L, SHIFT_DOWN_MASK)),
                createItem(TITLE_CLEAR_ALL, e -> clearFiles(OUTPUT_LOG, ERROR_LOG), getKeyStroke(VK_C, ALT_DOWN_MASK)),
                createItem(TITLE_HELP_IMPL, e -> showNoEdit(Files.openOrDefault(new File("./data/help." + Main.PROC.getProperty("language_file")),
                        "Failed to open.")), getKeyStroke(VK_H, ALT_DOWN_MASK)), null,
                createItem(TITLE_HOW_TO_USE, e -> userManual(), getKeyStroke(VK_M, ALT_DOWN_MASK)), null,
                createItem(TITLE_DICT, e -> dictEditor(), getKeyStroke(VK_D, ALT_DOWN_MASK))));
    }

    private void reloadFromDisk () {
        if (file != null) {
            loadFile(file);
        }
    }

    private void changeCharset () {
        final String name = (String) JOptionPane.showInputDialog(EditorComponent.this, NATIVE_LANG.getProperty("MESSAGE_CHARSET_CHANGE"),
                NATIVE_LANG.getProperty("MESSAGE_CHARSET_CHANGE_TITLE"), JOptionPane.INFORMATION_MESSAGE, null,
                PropertiesLoader.CHARSET_LIST.stream().map(Charset::displayName).toArray(String[]::new), charset.displayName());
        if (name != null) {
            charset = Charset.forName(name);
            JOptionPane.showMessageDialog(null, NATIVE_LANG.getProperty("MESSAGE_CHARSET_SET") + charset);
            try {
                if (file == null) {
                    return;
                }
                display.setText(Files.readDirectly(file, charset));
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private static void unicode () {
        // FIXME: 2022/4/12 display error.
        final var commonDisplay = getTextPane();
        final var unicodeDisplay = getTextPane();
        final JLabel annotation = new JLabel("Input common text at left part or input Unicode string at right part.");
        final JPanel port = new JPanel(new BorderLayout());
        port.add(annotation, BorderLayout.SOUTH);
        port.add(new JPanel(new BorderLayout()) {{
            setSize(100, 500);
            add(new JButton("Convert to Unicode->"), BorderLayout.SOUTH);
            add(new JButton("<-Convert to Common"), BorderLayout.NORTH);
        }}, BorderLayout.CENTER);
        commonDisplay.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped (KeyEvent e) {
                final var unboxedStr = commonDisplay.getText();
                try {
                    final var doc = MessageDisplayFrame.getPureTextDocument(Unicode.encodeExcept(unboxedStr));
                    unicodeDisplay.setDocument(doc);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    unicodeDisplay.setDocument(MessageDisplayFrame.getPureTextDocument("Waiting for input correct string..."));
                }
            }
        });
        unicodeDisplay.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped (KeyEvent e) {
                final var unicodeStr = unicodeDisplay.getText();
                try {
                    final var doc = MessageDisplayFrame.getPureTextDocument(Unicode.decode(unicodeStr));
                    commonDisplay.setDocument(doc);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    unicodeDisplay.setDocument(MessageDisplayFrame.getPureTextDocument("Waiting for input correct string..."));
                }
            }
        });
        final JFrame frame = new JFrame("Unicode Display");
        frame.add(port);
        frame.setSize(600, 500);
        frame.setVisible(true);
        port.add(new JScrollPane(commonDisplay) {{
            setSize(250, 500);
        }}, BorderLayout.EAST);
        port.add(new JScrollPane(unicodeDisplay) {{
            setSize(250, 500);
        }}, BorderLayout.WEST);
        commonDisplay.setSize(250, 500);
        unicodeDisplay.setSize(250, 500);
    }

    private void decodeUnicode () {
        try {
            display.setText(Unicode.decode(display.getText()));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void encodeUnicode () {
        try {
            display.setText(Unicode.encodeExcept(display.getText()));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void autoScroll () {
        final boolean state = !Boolean.parseBoolean(PROC.getProperty("auto_scroll"));
        display.setAutoscrolls(state);
        PROC.replace("auto_scroll", Boolean.toString(state));
        try {
            writeProc();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(EditorComponent.this, e.getMessage());
            return;
        }
        resetTitle();
        Main.refreshAllTitle();
    }

    private void replace () {

    }

    private void find () {
        findPopup.show(EditorComponent.this, 0, 0);
    }

    private void insertTime () {
        insertDate("hh:mm:ss");
    }

    private void frameSetting () {
        final String title = JOptionPane.showInputDialog(EditorComponent.this, "Input new title of the frame\n输入新窗口标题", PROC.getProperty("title"));
        if (title != null) {
            PROC.replace("title", title);
            try {
                writeProc();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            changeParentTitle.accept(title);
        }
    }

    private void dateFormat () {
        /*
        Formats:
        yyyy/MM/dd
        dd/MM/yyyy
        MM/dd/yyyy
        hh:mm:ss yyyy/MM/dd
        hh:mm:ss dd/MM/yyyy
        hh:mm:ss MM/dd/yyyy
        yyyy/MM/dd hh:mm:ss
        dd/MM/yyyy hh:mm:ss
        MM/dd/yyyy hh:mm:ss
        hh:mm:ss yyyy/MM/dd
        hh:mm:ss dd/MM/yyyy
        hh:mm:ss MM/dd/yyyy
         */
        final String format = (String) JOptionPane.showInputDialog(EditorComponent.this, NATIVE_LANG.getProperty("MESSAGE_DATE_FORMAT"),
                "DateFormat日期格式", JOptionPane.QUESTION_MESSAGE, null, new Object[]{"yyyy/MM/dd", "dd/MM/yyyy", "MM/dd/yyyy",
                        "yyyy-MM-dd", "dd-MM-yyyy", "MM-dd-yyyy", "yyyy_MM_dd", "dd_MM_yyyy", "MM_dd_yyyy"}, "yyyy/MM/dd");
        if (format != null) {
            PROC.replace("date_format", format);
            try {
                writeProc();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
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

    private static void showNoEdit (String text) {
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
        Main.refreshAllTitle();
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

    @SuppressWarnings("all")
    private void format (ActionEvent e) {
        new Thread(() -> {
            final var fp = FormatPane.display(display.getFont());
            while (!fp.isDisposed()) {
                try {
                    Thread.sleep(35);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    break;
                }
            }
            final Font newFont = fp.font();
            System.gc();
            display.setFont(newFont);
            setFontName(newFont.getFontName());
            PROC.setProperty("font_name", fontName);
            PROC.setProperty("font_size", Integer.toString(newFont.getSize()));
            try {
                Main.writeProc();
                Main.refreshAll();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            } finally {
                fp.removeAll();
            }
            System.gc();
        }).start();
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
            final String name = JOptionPane.showInputDialog("input file name");
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
            ioException.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can not save file:" + ioException.getMessage());
        }
    }

    private JMenu createMenu (String menuName, JMenuItem... items) {
        int separatorAmount = 0;
        final JMenu menu = new JMenu(menuName);
        for (JMenuItem item : items) {
            if (item != null) {
                menu.add(item);
            } else {
                menu.addSeparator();
                ++separatorAmount;
            }
        }
        final int insertPos = applierMenuItemTexts.size() + separatorAmount - items.length;
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
            Files.write(file, display.getText().getBytes(charset));
        } else {
            Files.write(to, display.getText().getBytes(charset));
        }
        System.out.println("Succ to save!");
    }

    public JMenuBar getBar () {
        return bar;
    }

    private void loadComp () {
        System.out.println("Loading comp...");
        final JScrollPane pane = new JScrollPane(display);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
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
                System.out.println("Predict words:" + matcher.predict(new String(cs)));
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
        //Test reloadFromDisk edit.
        matcher.flush(display.getText());
    }

    /*-------------------------------------------------- Runnable End --------------------------------------------------*/

    /*-------------------------------------------------- Override End --------------------------------------------------*/
}
