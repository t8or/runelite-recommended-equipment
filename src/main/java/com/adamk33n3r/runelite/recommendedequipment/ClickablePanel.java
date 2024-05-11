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

    private boolean isEntered;
    private boolean isPressed;

    public ClickablePanel(Color background, Color hover, Color pressed) {
        this.background = background;
        this.hover = hover;
        this.pressed = pressed;
        super.setBackground(this.background);
        this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (!isPressed) {
                    ClickablePanel.super.setBackground(ClickablePanel.this.hover);
                }
                isEntered = true;
            }
            public void mouseExited(MouseEvent evt) {
                if (!isPressed) {
                    ClickablePanel.super.setBackground(ClickablePanel.this.background);
                }
                isEntered = false;
            }
            public void mousePressed(MouseEvent evt) {
                ClickablePanel.super.setBackground(ClickablePanel.this.pressed);
                isPressed = true;
            }
            public void mouseReleased(MouseEvent evt) {
                if (isEntered) {
                    ClickablePanel.super.setBackground(ClickablePanel.this.hover);
                } else {
                    ClickablePanel.super.setBackground(ClickablePanel.this.background);
                }
                isPressed = false;
                if (isEntered && ClickablePanel.this.onClick != null) {
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
