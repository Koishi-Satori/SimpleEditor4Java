package top.kkoishi.swing;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serial;

public final class IconButton extends JButton {
    public static ImageIcon getIcon (File f) {
        return new ImageIcon(f.getAbsolutePath());
    }

    @Serial
    private static final long serialVersionUID = -1145141919810L;
    private ImageIcon normalIcon, iconEnable, iconDisable;
    private String tip;

    /**
     * Creates a button with no set text or icon.
     */
    public IconButton (ImageIcon normalIcon, ImageIcon iconEnable, ImageIcon iconDisable, String tip) {
        setIcon(normalIcon);
        this.normalIcon = normalIcon;
        this.iconEnable = iconEnable;
        this.iconDisable = iconDisable;
        this.tip = tip;
        init();
    }

    private void init () {
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setDisabledSelectedIcon(normalIcon);
        this.setContentAreaFilled(false);
        this.setFocusable(true);
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setRolloverIcon(iconEnable);
        this.setPressedIcon(iconEnable);
        this.setDisabledIcon(iconDisable);

        if (!"".equals(tip)) {
            this.setToolTipText(tip);
        }
    }
}
