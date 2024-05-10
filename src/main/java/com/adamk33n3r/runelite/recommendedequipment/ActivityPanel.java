package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.laf.RuneLiteLAF;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

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
        topPanel.setLayout(new BorderLayout(0, 10));
        this.add(topPanel, BorderLayout.NORTH);
        JButton directWikiLink = new JButton("Direct Wiki Link", Icons.LINK);
        directWikiLink.addActionListener(e -> LinkBrowser.browse(this.activity.getUrl()));
        directWikiLink.setHorizontalTextPosition(SwingConstants.LEFT);
        Util.addStyleClass(directWikiLink, "mini");
        JPanel wikiWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wikiWrapper.add(directWikiLink);
        topPanel.add(wikiWrapper, BorderLayout.SOUTH);
        JButton back = new JButton(this.activity.getName(), Icons.CHEVRON_LEFT);
        back.setHorizontalAlignment(SwingConstants.LEFT);
        Util.addStyleClass(back, "rounded dark");
        back.addActionListener(e -> {
            this.plugin.setActivityEquipmentStyle(null);
            this.muxer.popState();
        });
        topPanel.add(back, BorderLayout.CENTER);

        ScrollablePanel styles = new ScrollablePanel(new StretchedStackedLayout(5));
        styles.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        styles.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        styles.setScrollableBlockIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        styles.setScrollableUnitIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        JScrollPane scrollPane = new JScrollPane(styles, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel wrapper = new JPanel(new BorderLayout(5, 5));
        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(new JLabel("Choose Loadout"), BorderLayout.NORTH);
        wrapper.setBorder(new HorizontalRuleBorder(10, HorizontalRuleBorder.BOTH));
        this.add(wrapper, BorderLayout.CENTER);

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
