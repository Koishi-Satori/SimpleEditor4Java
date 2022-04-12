package top.kkoishi.easy.swing;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author KKoishi_
 */
public final class MessageDisplayFrame extends Frame {

    public final static class HTMLDisplayFrame extends Frame {

        private final JTextPane pane = EditorComponent.getTextPane();

        private HTMLDisplayFrame (String title) {
            super(title);
            add(pane);
            setVisible(true);
        }
    }

    public static HTMLDisplayFrame showHtml (String html, String title) {
        final var f = new HTMLDisplayFrame(title);
        f.pane.setContentType("text/html");
        f.pane.setText(html);
        return f;
    }

    public static Document getPureTextDocument (String msg) {
        Document document = new DefaultStyledDocument();
        try {
            document.insertString(0, msg, new SimpleAttributeSet());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * Constructs a new instance of {@code Frame} that is
     * initially invisible.  The title of the {@code Frame}
     * is empty.
     *
     * @throws HeadlessException when
     *                           {@code GraphicsEnvironment.isHeadless()} returns {@code true}
     * @see GraphicsEnvironment#isHeadless()
     * @see Component#setSize
     * @see Component#setVisible(boolean)
     */
    public MessageDisplayFrame (String title, Document document) throws HeadlessException {
        super(title);
        final JTextPane display = EditorComponent.getTextPane();
        display.setAutoscrolls(false);
        display.setEditable(false);
        display.setDocument(document);
        setSize(500, 450);
        setResizable(false);
        add(new JScrollPane(display));
        addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             *
             * @param e e
             */
            @Override
            public void windowClosing (WindowEvent e) {
                display.setDocument(MessageDisplayFrame.getPureTextDocument(""));
                removeAll();
                dispose();
                System.runFinalization();
            }
        });
        setVisible(true);
    }
}
