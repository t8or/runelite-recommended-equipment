package com.adamk33n3r.runelite.recommendedequipment;

import javax.swing.border.AbstractBorder;

import net.runelite.client.ui.ColorScheme;

import java.awt.*;

public class RoundedBorder extends AbstractBorder {
    private int radius;
    private int topInset;
    private int leftInset;
    private int bottomInset;
    private int rightInset;

    RoundedBorder(int radius) {
        this(radius, radius/2, radius/2, radius/2, radius/2);
    }

    RoundedBorder(int radius, int topInset, int leftInset, int bottomInset, int rightInset) {
        this.radius = radius;
        this.topInset = topInset;
        this.leftInset = leftInset;
        this.bottomInset = bottomInset;
        this.rightInset = rightInset;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        int strokeWidth = 2;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(strokeWidth));
        g.setColor(ColorScheme.MEDIUM_GRAY_COLOR);
        g.drawRoundRect(x, y, width-strokeWidth, height-strokeWidth, this.radius, this.radius);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(this.topInset, this.leftInset, this.bottomInset, this.rightInset);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = this.radius/2;
        return insets;
    }
}
