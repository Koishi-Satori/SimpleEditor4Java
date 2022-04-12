package top.kkoishi.easy.swing;

import top.kkoishi.easy.Main;
import top.kkoishi.io.FileChooser;
import top.kkoishi.io.OptionLoader;
import top.kkoishi.lang.PropertiesLoader;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.awt.Font.BOLD;
import static java.awt.Font.ITALIC;
import static java.awt.Font.PLAIN;
import static java.awt.Font.TRUETYPE_FONT;
import static java.awt.Font.createFont;

/**
 * @author DELL
 */
public final class FormatPane extends JPanel {

    public static FormatPane display (Font defaultFont) {
        final FormatPane instance = new FormatPane();
        instance.font = defaultFont;
        instance.applyFont();
        final var frame = new JFrame("Font字体") {
            private void dispose (Object o) {
                removeAll();
                dispose();
                instance.isDisposed = true;
            }
        };
        instance.setAction(frame::dispose);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing (WindowEvent e) {
                frame.removeAll();
                frame.dispose();
                instance.isDisposed = true;
                super.windowClosing(e);
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setSize(620, 450);
        frame.add(instance);
        frame.setVisible(true);
        System.gc();
        return instance;
    }

    public static final ConcurrentHashMap<String, Integer> FONT_STYLE_MAP = new ConcurrentHashMap<>() {{
        put("Plain(常规)", PLAIN);
        put("Bold(粗体)", BOLD);
        put("Italic(斜体)", ITALIC);
        put("Bold & Italic(粗斜体)", BOLD + ITALIC);
    }};

    private static final ConcurrentHashMap<String, Font> CUSTOM_FONT_SET = new ConcurrentHashMap<>();

    public static Font[] genFontArray () throws PropertiesLoader.IllegalOrBadPropertyFormatException, IOException {
        return getFonts(e -> JOptionPane.showMessageDialog(null, e.getMessage())).toArray(Font[]::new);
    }

    private static JTextPane createDisplay (Font font) {
        final JTextPane display = new JTextPane();
        display.setText("ABCabc锟斤拷");
        display.setFont(font);
        display.setEditable(false);
        display.setEnabled(false);
        display.setBackground(new Color(255, 255, 255));
        return display;
    }

    public static Font[] getDefaultFonts () {
        try {
            return genFontArray();
        } catch (PropertiesLoader.IllegalOrBadPropertyFormatException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
            return new Font[0];
        }
    }

    public static Font getFontOrDefault (String name, Font defaultValue) {
        return CUSTOM_FONT_SET.getOrDefault(name, defaultValue);
    }

    private static Font readFont (File file, Consumer<Exception> action) {
        FileInputStream fis = null;
        Font font = null;
        try {
            fis = new FileInputStream(file);
            font = createFont(TRUETYPE_FONT, fis);
            CUSTOM_FONT_SET.put(font.getName(), font);
        } catch (IOException | FontFormatException e) {
            action.accept(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    action.accept(e);
                }
            }
        }
        return font;
    }

    /**
     * Get all the available fonts
     *
     * @param exceptionAction the action when throws an exception.
     * @return font list.
     * @throws PropertiesLoader.IllegalOrBadPropertyFormatException bad format config file.
     * @throws IOException when an I/O exception occurs.
     */
    public static List<Font> getFonts (Consumer<Exception> exceptionAction) throws PropertiesLoader.IllegalOrBadPropertyFormatException, IOException {
        final OptionLoader loader = new OptionLoader(StandardCharsets.UTF_8);
        loader.load("./data/format/outer_font.list");
        loader.translate();
        final List<Font> fonts = new LinkedList<>(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()));
        for (final FileChooser.Option option : loader.getOptions()) {
            for (final String str : option.getExtensions()) {
                fonts.add(readFont(new File(str), exceptionAction));
            }
        }
//        loader.getOptions().stream()
//                .map(FileChooser.Option::getExtensions)
//                .map(Arrays::stream)
//                .flatMap(FormatPane::concatToFile)
//                .map(file -> readFont(file, exceptionAction))
//                .forEach(fonts::add);
//        loader.getOptions().stream()
//                .map(FileChooser.Option::getExtensions)
//                .map(Arrays::stream)
//                .forEach(s -> s.map(File::new).map(file -> readFont(file, exceptionAction))
//                        .forEach(fonts::add));
        return fonts;
    }

    private static Stream<File> concatToFile (Stream<String> stream) {
        return stream.map(File::new);
    }

    private final JList<Font> fontList = new JList<>();

    private final JList<String> styleList = new JList<>();

    private final JList<Integer> sizeList = new JList<>();

    private Font font = EditorComponent.accessFont();

    private final JTextPane display = createDisplay(font);

    private boolean isDisposed = false;

    private final JButton confirm = new JButton("Confirm确认");

    private Consumer<Object> action;

    public boolean isDisposed () {
        return isDisposed;
    }

    public void setDisposed (boolean disposed) {
        isDisposed = disposed;
    }

    /**
     * Construct a FormatPane instance with a default border layout.
     */
    public FormatPane () {
        super(null);
        bindAction();
        loadComp();
        setSize(620, 450);
    }

    private void loadComp () {
        fontList.setListData(getDefaultFonts());
        fontList.setCellRenderer(new FontCellRender());
        final JScrollPane fontListPane = new JScrollPane(fontList);
        styleList.setListData(new String[] {"Plain(常规)", "Bold(粗体)", "Italic(斜体)", "Bold & Italic(粗斜体)"});
        final JScrollPane fontStylePane = new JScrollPane(styleList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        final Vector<Integer> sizeVector = new Vector<>(41);
        IntStream.rangeClosed(8, 50).forEach(sizeVector::addElement);
        sizeList.setListData(sizeVector);
        final JScrollPane fontSizePane = new JScrollPane(sizeList);
        fontListPane.setBounds(30, 50, 150, 150);
        fontStylePane.setBounds(230, 50, 150, 150);
        fontSizePane.setBounds(430, 50, 100, 150);
        display.setBounds(50, 250, 200, 100);
        confirm.setBounds(350, 320, 100, 30);
        add(fontListPane);
        add(fontStylePane);
        add(fontSizePane);
        add(display);
        add(confirm);
    }

    public void setAction (Consumer<Object> action) {
        this.action = action;
        confirm.addActionListener(this.action::accept);
    }

    private void bindAction () {
        fontList.addListSelectionListener(this::freshFontName);
        styleList.addListSelectionListener(this::freshFontStyle);
        sizeList.addListSelectionListener(this::freshFontSize);
    }

    private void freshFontSize (ListSelectionEvent event) {
        font = new Font(font.getName(), font.getStyle(), sizeList.getSelectedValue());
        display.setFont(font);
    }

    private void freshFontName (ListSelectionEvent event) {
        font = fontList.getSelectedValue();
        display.setFont(font.deriveFont((float) 12));
    }

    @SuppressWarnings("all")
    private void freshFontStyle (ListSelectionEvent event) {
        final String styleName = styleList.getSelectedValue();
        final int style = FONT_STYLE_MAP.getOrDefault(styleName, -1);
        if (style == -1) {
            JOptionPane.showMessageDialog(null, "Error style name:" + styleName);
        } else {
            font = new Font(font.getName(), style, font.getSize());
            display.setFont(font);
        };
    }


    private void applyFont () {
        final String name = font.getName();
        Font cpy = Font.getFont(name);
        if (cpy == null) {
            System.out.println(Main.PROC);
            cpy = CUSTOM_FONT_SET.getOrDefault(name, new Font(Main.PROC.getProperty("font_name"), ITALIC, 12));
        }
        cpy = cpy.deriveFont(font.getStyle()).deriveFont((float) font.getSize());
        display.setFont(cpy);
        font = cpy;
        System.out.println("apply font:" + font);
        fontList.setSelectedValue(cpy, true);
        sizeList.setSelectedValue(font.getSize(), true);
        String styleName = null;
        for (Map.Entry<String, Integer> entry : FONT_STYLE_MAP.entrySet()) {
            if (entry.getValue().intValue() == font.getStyle()) {
                styleName = entry.getKey();
                break;
            }
        }
        if (styleName == null) {
            styleName = "Plain(常规)";
        }
        styleList.setSelectedValue(styleName, true);
    }

    public Font font () {
        return font;
    }

    public static void main (String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, PropertiesLoader.IllegalOrBadPropertyFormatException, IOException, InterruptedException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        final var fp = display(new Font(null, ITALIC, 15));
        while (!fp.isDisposed) {
            Thread.sleep(35);
        }
        System.out.println(fp.font());
    }
}
