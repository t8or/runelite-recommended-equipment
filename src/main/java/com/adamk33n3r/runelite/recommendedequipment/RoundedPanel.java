package com.adamk33n3r.runelite.recommendedequipment;

import javax.swing.JPanel;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private Color backgroundColor;
    private int cornerRadius = 15;

    public RoundedPanel(int radius, Color bgColor) {
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.setBorder(new RoundedBorder(radius, 5, 10, 5, 10));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draws the rounded panel with borders.
        graphics.setColor(backgroundColor);
        graphics.fillRoundRect(0, 0, width-1, height-1, this.cornerRadius, this.cornerRadius); //paint background
        // graphics.setColor(Color.RED);
        // graphics.drawRoundRect(0, 0, width-1, height-1, this.cornerRadius, this.cornerRadius); //paint border
    }
}
