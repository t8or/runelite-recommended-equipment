package com.adamk33n3r.runelite.recommendedequipment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickablePanel extends JPanel {
    private final Color background;
    private final Color hover;
    private final Color pressed;

    public ClickablePanel(Runnable onClick, Color background, Color hover, Color pressed) {
        this.background = background;
        this.hover = hover;
        this.pressed = pressed;
        this.setBackground(background);
        this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                setBackground(hover);
            }
            public void mouseExited(MouseEvent evt) {
                setBackground(background);
            }
            public void mousePressed(MouseEvent evt) {
                setBackground(pressed);
            }
            public void mouseReleased(MouseEvent evt) {
                setBackground(hover);
                onClick.run();
            }
        });
    }
}
