package top.kkoishi.easy.swing;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * @author KKoishi_
 */
public final class TaskDisplay extends JFrame implements Runnable {

     Consumer<Object> action;

    public final JProgressBar bar = new JProgressBar(0, 114);

    private int cur = 0;

    public boolean dispose = false;

    /**
     * Creates a new, initially invisible <code>Frame</code> with the
     * specified title.
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @param title the title for the frame
     * @throws HeadlessException if GraphicsEnvironment.isHeadless()
     *                           returns true.
     * @see GraphicsEnvironment#isHeadless
     * @see Component#setSize
     * @see Component#setVisible
     * @see JComponent#getDefaultLocale
     */
    public TaskDisplay (String title, Consumer<Object> action) throws HeadlessException {
        super(title);
        this.action = action;
        setVisible(true);
        while (dispose) {
            bar.setValue(cur);
            ++cur;
            if (cur == 114) {
                cur = 0;
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        bar.removeAll();
        removeAll();
        dispose();
    }

    public static void setDispose (TaskDisplay instance, boolean dispose) {
        instance.dispose = dispose;
    }

    public void setAction (Consumer<Object> action) {
        this.action = action;
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
        action.accept(null);
    }
}
