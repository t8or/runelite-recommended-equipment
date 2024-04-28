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
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

public class ActivityListItem extends ClickablePanel {
    private static final ImageIcon ON_STAR;
	private static final ImageIcon OFF_STAR;

    @Getter
    private final Activity activity;

    static {
		BufferedImage onStar = ImageUtil.loadImageResource(ConfigPlugin.class, "star_on.png");
		ON_STAR = new ImageIcon(onStar);

		BufferedImage offStar = ImageUtil.luminanceScale(
			ImageUtil.grayscaleImage(onStar),
			0.77f
		);
		OFF_STAR = new ImageIcon(offStar);
	}

    public ActivityListItem(Activity activity, MultiplexingPluginPanel muxer) {
        super(() -> {
            System.out.println("Clicked: " + activity.getName());
            muxer.pushState(new ActivityPanel(activity, muxer));
        }, ColorScheme.DARKER_GRAY_HOVER_COLOR, ColorScheme.MEDIUM_GRAY_COLOR, ColorScheme.DARK_GRAY_COLOR);
        Util.addStyleClass(this, "activity");
        this.activity = activity;
        this.setLayout(new BorderLayout(5, 5));
        this.rebuild();
    }

    public void rebuild() {
        this.removeAll();
        for (MouseAdapter listener : this.getListeners(MouseAdapter.class)) {
            this.removeMouseListener(listener);
        }
        JPanel left = new JPanel();
        left.setOpaque(false);
        // left.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
        JLabel label = new JLabel(this.activity.getName());
        left.add(label);
        this.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new DynamicGridLayout(1, 0));
        right.setOpaque(false);
        // right.setBackground(ColorScheme.BRAND_ORANGE);
        JLabel category = new JLabel(this.activity.getCategory());
        if (this.activity.getCategory() == null) {
            category.setText("\u00A0");
        }
//        category.setHorizontalAlignment(SwingConstants.RIGHT);
        // category.setBackground(ColorScheme.GRAND_EXCHANGE_ALCH);
        // category.setOpaque(true);
        // UIManager.put("Button.toolbar.pressedBackground", Color.PINK);
//        JButton test = new JButton("Test");
        // test.getUI().uninstallUI(test);
        // test.getUI().installUI(test);
        // UIManager.put("Button.toolbar.pressedBackground", prevColor);
//        test.putClientProperty("JButton.buttonType", "borderless");
        // FlatButtonUI buttonUI = FlatButtonUI.createUI(test);
        // test.setUI(buttonUI);
        // test.setUI(new BasicButtonUI() {
        //     @Override
        //     public void paintButtonPressed(Graphics g, AbstractButton b) {
        //         paintText(g, b, b.getBounds(), b.getText());
        //         g.setColor(Color.RED.brighter());
        //         g.fillRect(0, 0, b.getSize().width, b.getSize().height);
        //     }
        // });
        right.add(category);

//        Color prevColor = (Color)UIManager.get("ToggleButton.toolbar.pressBackground");
//        UIManager.put("ToggleButton.toolbar.pressedBackground", ColorScheme.DARKER_GRAY_HOVER_COLOR);
        JToggleButton favoriteBtn = new JToggleButton(OFF_STAR);
        favoriteBtn.setSelected(this.activity.isFavorite());
        // see if i can make my own style sheet so that im not modifying the defaults? I'm not sure how UIManager works. It didn't seem to change the buttons in the config panel.
        // favoriteBtn.putClientProperty("JButton.buttonType", "borderless");
		favoriteBtn.setSelectedIcon(ON_STAR);
		SwingUtil.removeButtonDecorations(favoriteBtn);
		SwingUtil.addModalTooltip(favoriteBtn, "Unfavorite", "Favorite");
		favoriteBtn.setPreferredSize(new Dimension(21, 0));
		favoriteBtn.addActionListener(e -> {
			// pluginListPanel.savePinnedPlugins();
			// pluginListPanel.refresh();
            System.out.println("Pin button clicked");
		});
        // pinButton.setBackground(ColorScheme.GRAND_EXCHANGE_ALCH);
//        UIManager.put("ToggleButton.toolbar.pressedBackground", prevColor);

        right.add(favoriteBtn);
        right.setAlignmentX(RIGHT_ALIGNMENT);
        this.add(right, BorderLayout.EAST);
    }
}
