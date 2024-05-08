package com.adamk33n3r.runelite.recommendedequipment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.metal.MetalButtonUI;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatButtonUI;

import lombok.Getter;
import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

public class ActivityListItem extends ClickablePanel {
    private static final ImageIcon ON_STAR;
	private static final ImageIcon OFF_STAR;

    @Getter
    private final Activity activity;

    private final RecommendedEquipmentPlugin plugin;

    static {
		BufferedImage onStar = ImageUtil.loadImageResource(ConfigPlugin.class, "star_on.png");
		ON_STAR = new ImageIcon(onStar);

		BufferedImage offStar = ImageUtil.luminanceScale(
			ImageUtil.grayscaleImage(onStar),
			0.77f
		);
		OFF_STAR = new ImageIcon(offStar);
	}

    public ActivityListItem(Activity activity, RecommendedEquipmentPlugin plugin, MultiplexingPluginPanel muxer) {
        super(() -> {
            System.out.println("Clicked: " + activity.getName());
            muxer.pushState(new ActivityPanel(activity, plugin, muxer));
        }, ColorScheme.DARKER_GRAY_HOVER_COLOR, ColorScheme.MEDIUM_GRAY_COLOR, ColorScheme.DARK_GRAY_COLOR);
        this.activity = activity;
        this.plugin = plugin;
        Util.addStyleClass(this, "activity");
        this.setToolTipText(activity.getName());
        this.rebuild();
    }

    public void rebuild() {
        this.removeAll();
        JLabel name = new JLabel(this.activity.getName());
        name.setHorizontalAlignment(SwingConstants.LEFT);
        name.setAlignmentX(LEFT_ALIGNMENT);

        JLabel category = new JLabel(this.activity.getCategory());
        if (this.activity.getCategory() == null) {
            category.setText("\u00A0");
        }
        JToggleButton favoriteBtn = new JToggleButton(OFF_STAR);
        favoriteBtn.setSelected(this.activity.isFavorite());
		favoriteBtn.setSelectedIcon(ON_STAR);
		SwingUtil.removeButtonDecorations(favoriteBtn);
		SwingUtil.addModalTooltip(favoriteBtn, "Unfavorite", "Favorite");
		favoriteBtn.setPreferredSize(new Dimension(21, 21));
		favoriteBtn.addActionListener(e -> {
            this.activity.setFavorite(!this.activity.isFavorite());
            this.plugin.saveFavorites(this.activity);
            System.out.println("Pin button clicked");
		});

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
