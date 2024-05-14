package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.util.LinkBrowser;

import javax.swing.*;

public class FooterPanel extends JPanel {
    public FooterPanel() {
        super();
        this.setLayout(new StretchedStackedLayout(2));

        JPanel secondaryPanel = new JPanel(new StretchedStackedLayout(8));
        secondaryPanel.setBorder(new HorizontalRuleBorder(8, HorizontalRuleBorder.BOTTOM));

        JButton howToUseButton = new JButton("How to use this plugin");
        Util.addStyleClass(howToUseButton, "rounded secondary");
        howToUseButton.addActionListener(e -> LinkBrowser.browse("https://github.com/adamk33n3r/runelite-recommended-equipment/blob/master/README.md"));
        secondaryPanel.add(howToUseButton);
        JButton donateButton = new JButton("Donate");
        Util.addStyleClass(donateButton, "rounded secondary");
        donateButton.addActionListener(e -> LinkBrowser.browse("https://donate.stripe.com/9AQcNxadm1pL7Hq9AA"));
        secondaryPanel.add(donateButton);
        this.add(secondaryPanel);

        JButton submitRequestButton = new JButton("Submit a request");
        Util.addStyleClass(submitRequestButton, "rounded primary");
        submitRequestButton.addActionListener(e -> LinkBrowser.browse("https://github.com/adamk33n3r/runelite-recommended-equipment/issues/new"));
        this.add(submitRequestButton);
    }
}
