package com.adamk33n3r.runelite.recommendedequipment;

import lombok.AllArgsConstructor;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Theme {
    public static final Border PRIMARY_BORDER = BorderFactory.createLineBorder(ColorScheme.TEXT_COLOR, 1);
    public static final Border SECONDARY_BORDER = BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR, 1);

    public static final Border NORMAL_PADDING = BorderFactory.createEmptyBorder(4, 8, 4, 8);
    public static final Border SMALL_PADDING = BorderFactory.createEmptyBorder(2, 4, 2, 4);
    public static final Border PANEL_PADDING = BorderFactory.createEmptyBorder(3, 5, 3, 5);

    @AllArgsConstructor
    public enum ButtonType {
        PRIMARY(ColorScheme.TEXT_COLOR, ColorScheme.DARK_GRAY_HOVER_COLOR, PRIMARY_BORDER),
        SECONDARY(ColorScheme.TEXT_COLOR, ColorScheme.DARK_GRAY_COLOR, SECONDARY_BORDER),
        HIGHLIGHT(ColorScheme.TEXT_COLOR, ColorScheme.DARKER_GRAY_HOVER_COLOR, PRIMARY_BORDER),
        ;

        private final Color foreground;
        private final Color background;
        private final Border border;
    }

    public enum ButtonSize {
        NORMAL,
        SMALL,
    }

    @AllArgsConstructor
    public enum PanelType {
        CLICKABLE(ColorScheme.DARKER_GRAY_HOVER_COLOR, BorderFactory.createCompoundBorder(SECONDARY_BORDER, PANEL_PADDING)),
        ;

        private final Color background;
        private final Border border;
    }

    public static void applyStyle(AbstractButton button, ButtonType type) {
        applyStyle(button, type, ButtonSize.NORMAL);
    }

    public static void applyStyle(AbstractButton button, ButtonType type, ButtonSize size) {
        button.setBackground(type.background);
        button.setBorder(BorderFactory.createCompoundBorder(type.border, size == ButtonSize.NORMAL ? NORMAL_PADDING : SMALL_PADDING));
    }

    public static void applyStyle(JPanel panel, PanelType type) {
        panel.setBackground(type.background);
        panel.setBorder(type.border);
    }
}
