package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ClickableListItem extends ClickablePanel {
    protected static final ImageIcon ON_STAR;
    protected static final ImageIcon OFF_STAR;

    static {
        BufferedImage onStar = ImageUtil.loadImageResource(ConfigPlugin.class, "star_on.png");
        ON_STAR = new ImageIcon(onStar);

        BufferedImage offStar = ImageUtil.luminanceScale(
            ImageUtil.grayscaleImage(onStar),
            0.77f
        );
        OFF_STAR = new ImageIcon(offStar);
    }

    private final String text;
    private final String subtext;
    private final boolean isFavorite;
    private final Runnable onFavorite;

    public ClickableListItem(String text, String subtext, boolean isFavorite, Runnable onFavorite) {
        super(ColorScheme.DARKER_GRAY_HOVER_COLOR, ColorScheme.MEDIUM_GRAY_COLOR, ColorScheme.DARK_GRAY_COLOR);
        this.text = text;
        this.subtext = subtext;
        this.isFavorite = isFavorite;
        this.onFavorite = onFavorite;
        this.rebuild();
        this.setBorder(new EmptyBorder(3, 5, 3, 5));
        Theme.applyStyle(this, Theme.PanelType.CLICKABLE);
    }

    public void rebuild() {
        this.removeAll();
        JLabel name = new JLabel(this.text);
        name.setHorizontalAlignment(SwingConstants.LEFT);
        name.setAlignmentX(LEFT_ALIGNMENT);

        JLabel category = new JLabel(this.subtext);
        if (this.subtext == null) {
            category.setText("\u00A0");
        }
        JToggleButton favoriteBtn = new JToggleButton(OFF_STAR);
        favoriteBtn.setSelected(this.isFavorite);
        favoriteBtn.setSelectedIcon(ON_STAR);
        SwingUtil.removeButtonDecorations(favoriteBtn);
        SwingUtil.addModalTooltip(favoriteBtn, "Unfavorite", "Favorite");
        favoriteBtn.setPreferredSize(new Dimension(21, 21));
        favoriteBtn.addActionListener(e -> this.onFavorite.run());

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(name, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(category)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(favoriteBtn, 21, 21, 21))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(name, 16, 16, 16)
                        .addComponent(favoriteBtn, 16, 16, 16)
                        .addComponent(category))
                    .addContainerGap())
        );
    }
}
