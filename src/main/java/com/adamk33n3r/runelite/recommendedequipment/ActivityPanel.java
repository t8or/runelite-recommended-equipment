package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.laf.RuneLiteLAF;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ActivityPanel extends PluginPanel {
    private final Activity activity;
    private final MultiplexingPluginPanel muxer;

    public ActivityPanel(Activity activity, MultiplexingPluginPanel muxer) {
        super(false);
        this.activity = activity;
        this.muxer = muxer;

        this.setLayout(new BorderLayout());
    }

    public void rebuild() {
        this.removeAll();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(0, BORDER_OFFSET));
        this.add(topPanel, BorderLayout.NORTH);
        topPanel.add(new JLabel(this.activity.getName()), BorderLayout.CENTER);
        JButton back = new JButton("Back");
        back.addActionListener(e -> this.muxer.popState());
        topPanel.add(back, BorderLayout.NORTH);

        ScrollablePanel styles = new ScrollablePanel(new StretchedStackedLayout(3));
        styles.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        styles.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        styles.setScrollableBlockIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        styles.setScrollableUnitIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        JScrollPane scrollPane = new JScrollPane(styles, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane, BorderLayout.CENTER);
        this.activity.getEquipmentStyles().forEach(style -> {
            JPanel stylePanel = new JPanel();
            Util.addStyleClass(stylePanel, "activity");
            stylePanel.setLayout(new GridLayout(0, 1));
            stylePanel.add(new JLabel(style.getName()), BorderLayout.WEST);
            style.getWeapon().forEach(equipment -> {
                JPanel equipmentPanel = new JPanel(new GridLayout(1, 0));
                equipment.getItems().forEach(item -> {
                    equipmentPanel.add(new JLabel(item.getName()));
                });
                stylePanel.add(equipmentPanel, BorderLayout.CENTER);
            });
            style.getAmmo().forEach(equipment -> {
                JPanel equipmentPanel = new JPanel(new GridLayout(1, 0));
                equipment.getItems().forEach(item -> {
                    equipmentPanel.add(new JLabel(item.getName()));
                });
                stylePanel.add(equipmentPanel, BorderLayout.CENTER);
            });
            styles.add(stylePanel);
        });

//        JPanel title = new PluginTitle();
//        topPanel.add(title, BorderLayout.NORTH);
    }

    @Override
    public void onActivate() {
        RecEquipLAF.setup();
        this.rebuild();
    }

    @Override
    public void onDeactivate() {
        RuneLiteLAF.setup();
    }
}
