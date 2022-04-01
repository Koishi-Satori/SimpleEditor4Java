package top.kkoishi.easy.swing;

import javax.swing.*;
import java.awt.*;

public final class FontCellRender extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent (JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof final Font font) {
            final var comp = super.getListCellRendererComponent(list, font.getName(), index, isSelected, cellHasFocus);
            comp.setFont(font.deriveFont((float) 12));
            return comp;
        } else {
            throw new ClassCastException();
        }
    }
}
