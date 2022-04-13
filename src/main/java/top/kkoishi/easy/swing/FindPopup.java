package top.kkoishi.easy.swing;

import top.kkoishi.easy.lang.BoyerMoore;
import top.kkoishi.swing.IconButton;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static top.kkoishi.easy.Main.NATIVE_LANG;

public final class FindPopup extends JPopupMenu {
    private final JTextComponent display;

    private final List<Integer> res = new LinkedList<>();

    private int pos = 0;

    private int mode = 1;

    private int patLength = 0;

    public static FindPopup getInstance (JTextComponent component) {
        return new FindPopup(component);
    }

    /**
     * Constructs a <code>JPopupMenu</code> without an "invoker".
     */
    private FindPopup (JTextComponent display) {
        super();
        this.display = display;
        init();
    }

    private void init () {
        final JTextField input = new JTextField();
        input.setToolTipText("Input word to be searched here.");
        final JPanel container = new JPanel(new BorderLayout());
        container.add(input, BorderLayout.CENTER);
        add(container);
        setBackground(Color.WHITE);
        final IconButton reset = new IconButton(IconButton.getIcon(new File("./data/icon/find/reset.png")),
                IconButton.getIcon(new File("./data/icon/find/reset_enable.png")),
                IconButton.getIcon(new File("./data/icon/find/reset.png")),
                NATIVE_LANG.getProperty("MESSAGE_FIND_BUT_RESET"));
        final IconButton search = new IconButton(IconButton.getIcon(new File("./data/icon/find/search.png")),
                IconButton.getIcon(new File("./data/icon/find/search_enable.png")),
                IconButton.getIcon(new File("./data/icon/find/search.png")),
                NATIVE_LANG.getProperty("MESSAGE_FIND_BUT_APPLY"));
        final IconButton mode = new IconButton(IconButton.getIcon(new File("./data/icon/find/mode.png")),
                IconButton.getIcon(new File("./data/icon/find/mode_enable.png")),
                IconButton.getIcon(new File("./data/icon/find/mode.png")),
                NATIVE_LANG.getProperty("MESSAGE_FIND_BUT_MODE"));
        final IconButton prev = new IconButton(IconButton.getIcon(new File("./data/icon/find/prev.png")),
                IconButton.getIcon(new File("./data/icon/find/prev_enable.png")),
                IconButton.getIcon(new File("./data/icon/find/prev.png")),
                NATIVE_LANG.getProperty("MESSAGE_FIND_BUT_PREV"));
        final IconButton next = new IconButton(IconButton.getIcon(new File("./data/icon/find/next.png")),
                IconButton.getIcon(new File("./data/icon/find/next_enable.png")),
                IconButton.getIcon(new File("./data/icon/find/next.png")),
                NATIVE_LANG.getProperty("MESSAGE_FIND_BUT_NEXT"));
        container.add(mode, BorderLayout.WEST);
        final JPanel subContainer = new JPanel(new BorderLayout());
        final JLabel modeLabel = new JLabel(NATIVE_LANG.getProperty("MESSAGE_FIND_MODE_WORD"), JLabel.CENTER);
        subContainer.add(modeLabel, BorderLayout.EAST);
        subContainer.add(prev, BorderLayout.CENTER);
        subContainer.add(next, BorderLayout.WEST);
        container.add(subContainer, BorderLayout.EAST);
        final JPanel secContainer = new JPanel(new BorderLayout());
        secContainer.add(reset, BorderLayout.WEST);
        final JLabel resDisplay = new JLabel("\t 0 results \t", JLabel.CENTER);
        secContainer.add(resDisplay, BorderLayout.CENTER);
        secContainer.add(search, BorderLayout.EAST);
        add(secContainer);
        setPopupSize(400, 72);
        final Highlighter old = display.getHighlighter();
        final var n = SimpleHighLighter.getDefault();
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible (PopupMenuEvent e) {
                display.setHighlighter(n);
            }

            @Override
            public void popupMenuWillBecomeInvisible (PopupMenuEvent e) {
                n.removeAllHighlights();
                display.setHighlighter(old);
            }

            @Override
            public void popupMenuCanceled (PopupMenuEvent e) {
                n.removeAllHighlights();
                System.runFinalization();
            }
        });
        search.addActionListener(l -> {
            final var pat = input.getText();
            if (pat != null && pat.length() != 0) {
                res.clear();
                n.removeAllHighlights();
                //regex mode
                if (FindPopup.this.mode == 0) {

                } else {
                    pos = 0;
                    res.addAll(BoyerMoore.boyerMoore(pat, display.getText()));
                    patLength = pat.length();
                    resDisplay.setText("\t " + res.size() + " results \t");
                    for (final int re : res) {
                        try {
                            n.add(re, re + patLength);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!res.isEmpty()) {
                        try {
                            n.remove(res.get(pos), res.get(pos) + patLength);
                            n.add(res.get(pos), res.get(pos) + patLength, new Color(212, 11, 234));
                            display.setCaretPosition(res.get(pos));
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        prev.addActionListener(l -> {
            if (pos <= 0) {
                pos = res.size() - 1;
            }
            switchWord(n, false);
        });
        next.addActionListener(l -> {
            if (pos >= res.size() - 1) {
                pos = 0;
            }
            switchWord(n, true);
        });
    }

    private void switchWord (SimpleHighLighter n, boolean move2next) {
        if (!res.isEmpty()) {
            try {
                n.remove(res.get(pos), res.get(pos) + patLength);
                n.add(res.get(pos), res.get(pos) + patLength);
                if (move2next) {
                    ++pos;
                } else {
                    --pos;
                }
                n.remove(res.get(pos), res.get(pos) + patLength);
                n.add(res.get(pos), res.get(pos) + patLength, new Color(212, 11, 234));
                display.setCaretPosition(res.get(pos));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Switch to next word...");
    }
}
