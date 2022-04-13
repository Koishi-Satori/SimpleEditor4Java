package top.kkoishi.easy.swing;

import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.BadLocationException;
import java.awt.*;

public final class SimpleHighLighter extends BasicTextUI.BasicHighlighter {

    public static SimpleHighLighter getDefault () {
        if (instance == null) {
            instance = new SimpleHighLighter(new Color(9, 215, 252));
        }
        return instance;
    }

    private static SimpleHighLighter instance = new SimpleHighLighter(new Color(9, 215, 252));

    private Color renderColor;

    public SimpleHighLighter (Color renderColor) {
        this.renderColor = renderColor;
    }

    public Color getRenderColor () {
        return renderColor;
    }

    public void setRenderColor (Color renderColor) {
        this.renderColor = renderColor;
    }

    /**
     * Renders the highlights.
     *
     * @param g the graphics context
     */
    @Override
    public void paint (Graphics g) {
        super.paint(g);
    }

    public Object add (int p0, int p1) throws BadLocationException {
        return add(p0, p1, renderColor);
    }

    public Object add (int p0, int p1, Color renderColor) throws BadLocationException {
        return addHighlight(p0, p1, new DefaultHighlightPainter(renderColor));
    }

    public boolean remove (int p0, int p1) {
        for (final Highlight highlight : super.getHighlights()) {
            if (p0 == highlight.getStartOffset() && p1 == highlight.getEndOffset()) {
                super.removeHighlight(highlight);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a highlight to the view.  Returns a tag that can be used
     * to refer to the highlight.
     *
     * @param p0 the start offset of the range to highlight &gt;= 0
     * @param p1 the end offset of the range to highlight &gt;= p0
     * @param p  the painter to use to actually render the highlight
     * @return an object that can be used as a tag
     * to refer to the highlight
     * @throws BadLocationException if the specified location is invalid
     */
    @Override
    public Object addHighlight (int p0, int p1, HighlightPainter p) throws BadLocationException {
        return super.addHighlight(p0, p1, p);
    }
}
