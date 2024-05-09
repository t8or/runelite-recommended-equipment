package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.laf.RuneLiteLAF;

import javax.swing.*;
import java.awt.*;

public class ActivityPanel extends PluginPanel {
    private final Activity activity;
    private final ActivityManager activityManager;
    private final MultiplexingPluginPanel muxer;
    private final RecommendedEquipmentPlugin plugin;

    public ActivityPanel(Activity activity, RecommendedEquipmentPlugin plugin, ActivityManager activityManager, MultiplexingPluginPanel muxer) {
        super(false);
        this.activity = activity;
        this.activityManager = activityManager;
        this.plugin = plugin;
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
        this.activity.getEquipmentStyles().stream()
            .map(style -> new EquipmentStyleListItem(this.activity, style, this.plugin, this.activityManager))
            .forEach(styles::add);
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
