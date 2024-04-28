package com.adamk33n3r.runelite.recommendedequipment;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PluginTitle extends JPanel {
    @Inject
    public PluginTitle(@Named("recequip.pluginVersion") String pluginVersion) {
        this.setLayout(new BorderLayout());
        JLabel icon = new JLabel(Icons.ICON);
        icon.setBorder(new EmptyBorder(0, 0, 4, 2));
        this.add(icon, BorderLayout.WEST);
        JLabel titleLabel = new JLabel("Recommended Equipment");
        this.add(titleLabel, BorderLayout.CENTER);
        JLabel version = new JLabel("v" + pluginVersion);
        version.setFont(version.getFont().deriveFont(Font.BOLD, 10f));
        version.setBorder(new EmptyBorder(5, 0, 0, 0));
        this.add(version, BorderLayout.EAST);
    }
}