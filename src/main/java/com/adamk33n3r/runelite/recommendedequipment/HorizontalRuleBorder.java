package com.adamk33n3r.runelite.recommendedequipment;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class HorizontalRuleBorder extends EtchedBorder {
    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int BOTH = 2;

    private final int size;
    private final int sides;
    private final Border outsideBorder;

    public HorizontalRuleBorder(int size) {
        this(size, BOTH);
    }

    public HorizontalRuleBorder(int size, int sides) {
        super();
        this.size = size;
        this.sides = sides;
        this.outsideBorder = new EmptyBorder(this.sides != BOTTOM ? size : 0, 0, this.sides != TOP ? size : 0, 0);
    }

    /**
     * Reinitialize the insets parameter with this Border's current Insets.
     *
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        Insets outerInsets = this.outsideBorder.getBorderInsets(c);
        insets.set(this.sides != BOTTOM ? this.size + outerInsets.top : 0, 0, this.sides != TOP ? this.size + outerInsets.bottom : 0, 0);
        return insets;
    }

    /**
     * Paints the border for the specified component with the
     * specified position and size.
     *
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Insets  nextInsets;
        int px, py, pw, ph;

        px = x;
        py = y;
        pw = width;
        ph = height;

        outsideBorder.paintBorder(c, g, px, py, pw, ph);

        nextInsets = outsideBorder.getBorderInsets(c);
        px += nextInsets.left;
        pw = pw - nextInsets.right - nextInsets.left;

        // Draw top
        if (sides != BOTTOM) {
            py += nextInsets.top;
            g.translate(px, py);
            draw(c, g, pw);
            g.translate(-px, -py);
        }

        // Draw bottom
        if (sides != TOP) {
            py += height - nextInsets.bottom - nextInsets.top;
            g.translate(px, py);
            draw(c, g, pw);
            g.translate(-px, -py);
        }
    }

    private void draw(Component c, Graphics g, int pw) {
        g.setColor(etchType == LOWERED? getShadowColor(c) : getHighlightColor(c));
        g.drawLine(0, 0, pw - 2, 0);

        g.setColor(etchType == LOWERED? getHighlightColor(c) : getShadowColor(c));
        g.drawLine(1, 1, pw -3, 1);
    }
}
