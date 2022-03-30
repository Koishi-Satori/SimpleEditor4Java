package top.kkoishi.easy.swing;

import java.awt.*;

public class CustomCursor extends Cursor {
    /**
     * Creates a new cursor object with the specified type.
     *
     * @param type the type of cursor
     * @throws IllegalArgumentException if the specified cursor type
     *                                  is invalid
     */
    public CustomCursor (int type) {
        super(type);
    }
}
