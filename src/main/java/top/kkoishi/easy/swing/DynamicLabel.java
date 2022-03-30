package top.kkoishi.easy.swing;

import javax.swing.*;

/**
 * @author KKoishi_
 */
public abstract class DynamicLabel extends JLabel implements Runnable {
    /**
     * Creates a <code>JLabel</code> instance with
     * no image and with an empty string for the title.
     * The label is centered vertically
     * in its display area.
     * The label's contents, once set, will be displayed on the leading edge
     * of the label's display area.
     */
    public DynamicLabel () {
        initProc();
    }

    /**
     * Flush the text.
     *
     * @return text
     */
    abstract String flush ();

    protected void initProc () {
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
        setText(flush());
    }
}
