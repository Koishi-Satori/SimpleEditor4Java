package top.kkoishi.easy.swing;

import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JVMStateDisplay extends JPanel {
    DynamicLabel memory = new DynamicLabel() {
        @Override
        protected void initProc () {
            super.initProc();
            System.gc();
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked (MouseEvent e) {
                    System.gc();
                }
            });
        }

        @Override
        String flush () {
            return "Heap Memory State:" + Runtime.getRuntime().totalMemory() / (1 << 20) + " of "
                    + Runtime.getRuntime().maxMemory() / (1 << 20) + "  |  Allocated by JVM:" + Runtime.getRuntime().freeMemory() / (1 << 10) + "KiB";
        }

        @Override
        public void run () {
            setText(flush());
        }
    };

    /**
     * Creates a new <code>JVMStateDisplay</code> with a double buffer
     * and a flow layout.
     */
    public JVMStateDisplay () {
        add(memory);
    }

    public DynamicLabel get () {
        return memory;
    }
}
