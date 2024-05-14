package com.adamk33n3r.runelite.recommendedequipment;

import com.google.common.base.Charsets;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.laf.RuneLiteLAF;
import net.runelite.client.util.LinkBrowser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class ActivityPanel extends PluginPanel {
    private final Activity activity;
    private final ActivityManager activityManager;
    private final MultiplexingPluginPanel muxer;
    private final RecommendedEquipmentPlugin plugin;

    private JPanel styles;
    private JScrollPane scrollPane;
    private Accordion selectedLoadout;

    public ActivityPanel(Activity activity, RecommendedEquipmentPlugin plugin, ActivityManager activityManager, MultiplexingPluginPanel muxer) {
        super(false);
        this.activity = activity;
        this.activityManager = activityManager;
        this.plugin = plugin;
        this.muxer = muxer;

        this.setLayout(new BorderLayout());
    }

    public void deselectAll(EquipmentStyleListItem except, boolean setActiveToNull) {
        Arrays.stream(this.styles.getComponents())
            .filter(c -> c instanceof EquipmentStyleListItem)
            .map(c -> (EquipmentStyleListItem) c)
            .filter(item -> item != except)
            .forEach(item -> item.deselect(setActiveToNull));
    }

    public void rebuild() {
        this.removeAll();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(0, 10));
        this.add(topPanel, BorderLayout.NORTH);
        JButton directWikiLink = new JButton("Direct wiki link", Icons.LINK);
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

        this.styles = new JPanel(new StretchedStackedLayout(5));

        ActivityEquipmentStyle selectedStyle = this.plugin.getActivityEquipmentStyle();
        this.activity.getEquipmentStyles().stream()
            .map(style -> new EquipmentStyleListItem(this.activity, style, this, this.plugin, this.activityManager))
            .forEach(item -> {
                this.styles.add(item);
                if (selectedStyle != null && item.getStyle() == selectedStyle) {
                    item.select();
                }
            });

        this.selectedLoadout = new Accordion();
        this.selectedLoadout.setBorder(new EmptyBorder(5, 0, 0, 0));

        ScrollablePanel wrapper = new ScrollablePanel(new StretchedStackedLayout(5));
        wrapper.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        wrapper.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        wrapper.setScrollableBlockIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        wrapper.setScrollableUnitIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        wrapper.add(this.styles);
        wrapper.add(this.selectedLoadout);

        this.scrollPane = new JScrollPane(wrapper, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // For the scrollbar gap
        this.scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
        ClickableListPanel chooseLoadout = new ClickableListPanel("Choose loadout", this.scrollPane);
        chooseLoadout.setBorder(new HorizontalRuleBorder(10, HorizontalRuleBorder.BOTH));
        this.add(chooseLoadout, BorderLayout.CENTER);
        this.rebuildSelectedLoadout();
    }

    public void selectLoadout(EquipmentStyleListItem item) {
        if (item.isSelected()) {
            item.deselect(true);
        } else {
            this.deselectAll(item, false);
            item.select();
        }
        this.rebuildSelectedLoadout();
    }

    private void rebuildSelectedLoadout() {
        this.selectedLoadout.removeAll();
        if (this.plugin.getActivityEquipmentStyle() != null) {
            this.selectedLoadout.removeAll();
            this.plugin.getActivityEquipmentStyle().getSlots().forEach((slot) -> {
                if (slot.getValue().isEmpty()) {
                    return;
                }
                JPanel section = this.makeSlotSection(slot.getValue());
                this.selectedLoadout.addSection(slot.getKey(), section);
            });
        }

        this.selectedLoadout.revalidate();
        this.selectedLoadout.repaint();

        SwingUtilities.invokeLater(() -> {
            if (this.scrollPane.getVerticalScrollBar().isVisible()) {
                this.scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 5));
            } else {
                this.scrollPane.setViewportBorder(null);
            }
        });
    }

    private JPanel makeSlotSection(List<ActivitySlotTier> slotTiers) {
        JPanel content = new JPanel();
        content.setLayout(new WrapLayout(WrapLayout.LEFT));
        slotTiers.forEach(slotTier -> {
            slotTier.getItems().forEach((item) -> {
                JButton tierItem = new JButton(item.getName());
                tierItem.addActionListener((ev) -> {
                    LinkBrowser.browse("https://oldschool.runescape.wiki/?title=Special%3ASearch&go=Go&search=" + URLEncoder.encode(item.getName(), Charsets.UTF_8));
                });
                Util.addStyleClass(tierItem, "med");
                content.add(tierItem);
            });
        });
        return content;
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
