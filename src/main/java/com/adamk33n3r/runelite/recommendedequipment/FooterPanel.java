package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.util.LinkBrowser;

import javax.swing.*;
import java.awt.*;

public class FooterPanel extends JPanel {
    public FooterPanel() {
        super();
        this.setLayout(new GridLayout(0, 1, 8, 8));

        JButton howToUseButton = new JButton("How to use this plugin");
        Util.addStyleClass(howToUseButton, "rounded");
        howToUseButton.addActionListener(e -> LinkBrowser.browse("https://github.com/adamk33n3r/runelite-recommended-equipment/blob/master/README.md"));
        this.add(howToUseButton);
        JButton donateButton = new JButton("Donate");
        Util.addStyleClass(donateButton, "rounded");
        donateButton.addActionListener(e -> LinkBrowser.browse("https://donate.stripe.com/9AQcNxadm1pL7Hq9AA"));
        this.add(donateButton);
        JButton submitRequestButton = new JButton("Submit a request");
        Util.addStyleClass(submitRequestButton, "rounded");
        submitRequestButton.addActionListener(e -> LinkBrowser.browse("https://github.com/adamk33n3r/runelite-recommended-equipment/issues/new"));
        this.add(submitRequestButton);
    }
}
