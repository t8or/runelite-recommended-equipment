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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(background);
            }
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                setBackground(pressed);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                setBackground(hover);
            }
        });
    }
}
