package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.laf.RuneLiteLAF;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Singleton
public class PanelWrapper extends PluginPanel {
    private final RecommendedEquipmentPanel recommendedEquipmentPanel;
    @Inject
    public PanelWrapper(RecommendedEquipmentPanel recommendedEquipmentPanel, PluginTitle pluginTitle) {
        super(false);
        this.recommendedEquipmentPanel = recommendedEquipmentPanel;
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add(pluginTitle, BorderLayout.NORTH);
        pluginTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        this.add(this.recommendedEquipmentPanel.getMuxer(), BorderLayout.CENTER);
        FooterPanel footerPanel = new FooterPanel();
        this.add(footerPanel, BorderLayout.SOUTH);
    }

    @Override
    public void onActivate() {
        RecEquipLAF.setup();
        this.recommendedEquipmentPanel.getMuxer().onActivate();
    }

    @Override
    public void onDeactivate() {
        RuneLiteLAF.setup();
        this.recommendedEquipmentPanel.getMuxer().onDeactivate();
    }
}
