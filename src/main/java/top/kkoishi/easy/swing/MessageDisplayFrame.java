package top.kkoishi.easy.swing;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author KKoishi_
 */
public final class MessageDisplayFrame extends Frame {

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
        display.setDocument(document.getDefaultRootElement().getDocument());
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
                removeAll();
                dispose();
            }
        });
        setVisible(true);
    }
}
