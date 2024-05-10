package com.adamk33n3r.runelite.recommendedequipment;

import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickablePanel extends JPanel {
    private Color background;
    @Setter
    private Color hover;
    @Setter
    private Color pressed;

    @Setter
    private Runnable onClick;

    public ClickablePanel(Color background, Color hover, Color pressed) {
        this.background = background;
        this.hover = hover;
        this.pressed = pressed;
        super.setBackground(this.background);
        this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                ClickablePanel.super.setBackground(ClickablePanel.this.hover);
            }
            public void mouseExited(MouseEvent evt) {
                ClickablePanel.super.setBackground(ClickablePanel.this.background);
            }
            public void mousePressed(MouseEvent evt) {
                ClickablePanel.super.setBackground(ClickablePanel.this.pressed);
            }
            public void mouseReleased(MouseEvent evt) {
                ClickablePanel.super.setBackground(ClickablePanel.this.hover);
                if (ClickablePanel.this.onClick != null) {
                    ClickablePanel.this.onClick.run();
                }
            }
        });
    }

    public void setBackground(Color bg) {
        super.setBackground(bg);
        this.background = bg;
    }
}
