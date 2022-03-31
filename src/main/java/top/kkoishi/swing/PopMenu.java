package top.kkoishi.swing;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author KKoishi_
 */
public final class PopMenu extends JPopupMenu {

    private List<String> words = new ArrayList<>();

    private int pos = 0;

    boolean onAction = false;

    private Component invoker = null;

    public Consumer<Object> action;

    private Deque<MouseAdapter> tasks = new LinkedList<>();

    private String word = null;

    /**
     * Constructs a <code>JPopupMenu</code> with the specified title.
     *
     * @param label the string that a UI may use to display as a title
     *              for the popup menu.
     */
    public PopMenu (String label) {
        super(label);
        addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             *
             * @param e event
             */
            @Override
            public void keyTyped (KeyEvent e) {
                super.keyTyped(e);
                final int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ESCAPE) {
                    focusParent(invoker);
                    onAction = false;
                    return;
                }
                if (keyCode == KeyEvent.VK_TAB) {
                    action.accept(null);
                    focusParent(invoker);
                    onAction = false;
                } else {
                    if (keyCode == KeyEvent.VK_UP) {
                        if (checkRange()) {
                            cancel();
                            --pos;
                            render();
                        }
                    } else if (keyCode == KeyEvent.VK_DOWN) {
                        if (checkRange()) {
                            cancel();
                            ++pos;
                            render();
                        }
                    } else {
                        focusParent(invoker);
                        System.out.println("Redirect to parent");
                    }
                }
            }
        });
    }

    public void setWords (List<String> words) {
        this.removeAll();
        this.words.clear();
        this.words = new ArrayList<>();
        this.words.addAll(words);
    }

    @Override
    public void show (Component invoker, int x, int y) {
        System.out.println("Initializing Tip Menu");
        this.invoker = invoker;
        for (String word : words) {
            final JMenuItem i = add(word);
            i.addActionListener(l -> {
                System.out.println("Select comp:" + i.getText());
                this.word = word;
                action.accept(null);
            });
        }
        onAction = true;
        super.show(invoker, x, y);
    }

    public String getSelect () {
        System.out.println("Redirecting output word.");
        if (words.isEmpty()) {
            return null;
        } else {
            return word;
        }
    }

    private void cancel () {
        this.getSubElements()[pos].getComponent().setBackground(Color.WHITE);
    }

    private void render () {
        this.getSubElements()[pos].getComponent().setBackground(Color.GRAY);
    }

    private boolean checkRange () {
        return pos > 0 && pos < words.size() - 1;
    }

    public void focusParent (Component invoker) {
        this.setFocusable(false);
        invoker.setFocusable(true);
    }

    /**
     * Sets the visibility of the popup menu.
     *
     * @param b true to make the popup visible, or false to
     *          hide it
     */
    @Override
    public void setVisible (boolean b) {
        super.setVisible(b);
        if (!b) {
            onAction = false;
        }
    }
}
