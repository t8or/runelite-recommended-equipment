package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.LinkBrowser;

import javax.swing.*;
import javax.swing.border.CompoundBorder;

public class FooterPanel extends JPanel {
    public FooterPanel() {
        super();
        this.setLayout(new StretchedStackedLayout(2));

        JPanel secondaryPanel = new JPanel(new StretchedStackedLayout(8));
        secondaryPanel.setBorder(new HorizontalRuleBorder(8, HorizontalRuleBorder.BOTTOM));

        JButton howToUseButton = new JButton("How to use this plugin");
        Theme.applyStyle(howToUseButton, Theme.ButtonType.SECONDARY);
        howToUseButton.addActionListener(e -> LinkBrowser.browse("https://github.com/adamk33n3r/runelite-recommended-equipment/blob/master/README.md"));
        secondaryPanel.add(howToUseButton);
        JButton donateButton = new JButton("Donate");
        Theme.applyStyle(donateButton, Theme.ButtonType.SECONDARY);
        donateButton.addActionListener(e -> LinkBrowser.browse("https://donate.stripe.com/9AQcNxadm1pL7Hq9AA"));
        secondaryPanel.add(donateButton);
        this.add(secondaryPanel);

        JButton submitRequestButton = new JButton("Submit a request");
        Theme.applyStyle(submitRequestButton, Theme.ButtonType.HIGHLIGHT);
        submitRequestButton.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
        submitRequestButton.addActionListener(e -> LinkBrowser.browse("https://github.com/adamk33n3r/runelite-recommended-equipment/issues/new"));
        this.add(submitRequestButton);
    }
}
