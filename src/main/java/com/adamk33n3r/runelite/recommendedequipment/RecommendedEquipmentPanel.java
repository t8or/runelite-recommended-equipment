package com.adamk33n3r.runelite.recommendedequipment;

import com.formdev.flatlaf.ui.FlatToggleButtonUI;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.laf.RuneLiteLAF;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Singleton
public class RecommendedEquipmentPanel extends PluginPanel {
    @Inject
    private ActivityManager activityManager;
    @Inject
    private RecommendedEquipmentPlugin plugin;

    @Getter
    private final MultiplexingPluginPanel muxer = new MultiplexingPluginPanel(this);

    private static final EmptyBorder VIEWPORT_BORDER = new EmptyBorder(0, 0, 0, 5);
    private final List<ActivityListItem> allActivityListItems = new ArrayList<>();
    private JPanel mainPanel;
    private IconTextField search;
    private JScrollPane scrollPane;

    private static final Splitter SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();
    private JPanel filterList;
    private final Set<String> selectedCategories = new HashSet<>();
    private List<Activity> activities;

    public RecommendedEquipmentPanel() {
        super(false);
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
    }

    public void rebuild() {
        this.removeAll();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(0, BORDER_OFFSET));
        this.add(topPanel, BorderLayout.NORTH);

        JPanel filterArea = new JPanel(new DynamicGridLayout(2, 1, 5, 5));
        topPanel.add(filterArea, BorderLayout.CENTER);

        this.search = new IconTextField();
        this.search.setIcon(IconTextField.Icon.SEARCH);
        this.search.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        this.search.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.search.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onSearchBarChanged(search.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onSearchBarChanged(search.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onSearchBarChanged(search.getText());
            }
        });
//        CATEGORY_TAGS.forEach(searchBar.getSuggestionListModel()::addElement);
        filterArea.add(this.search);

        this.filterList = new JPanel();
        this.filterList.setLayout(new WrapLayout(FlowLayout.LEFT, 2, 2));
        this.filterList.setBorder(new EmptyBorder(4, 4, 4, 4));
        this.filterList.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.filterList.add(new JLabel("Filter list", Icons.FUNNEL, SwingConstants.LEFT));
        filterArea.add(this.filterList);

        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new StretchedStackedLayout(5));
        this.mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ScrollablePanel scrollablePanel = new ScrollablePanel(new BorderLayout());
        scrollablePanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        scrollablePanel.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        scrollablePanel.setScrollableBlockIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        scrollablePanel.setScrollableUnitIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        scrollablePanel.add(this.mainPanel, BorderLayout.NORTH);

        this.scrollPane = new JScrollPane(scrollablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // For the scrollbar gap
        this.scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);

        ClickableListPanel activityList = new ClickableListPanel("Choose activity", this.scrollPane);
        activityList.setBorder(new HorizontalRuleBorder(10, HorizontalRuleBorder.BOTH));
        this.add(activityList, BorderLayout.CENTER);

        this.reloadList(true);
    }

    private void reloadList(boolean fetch) {
        this.reloadList(fetch, false);
    }

    private void reloadList(boolean fetch, boolean forceDownload) {
        try {
            this.allActivityListItems.clear();
            if (fetch) {
                this.activities = this.activityManager.getActivities(forceDownload);
            }
            this.filterList.removeAll();
            this.filterList.add(new JLabel("Filter list", Icons.FUNNEL, SwingConstants.LEFT));
            this.filterList.add(this.makeFilterButton("Favorite"));
            activities.stream().map(Activity::getCategory).distinct().map(this::makeFilterButton).forEach(this.filterList::add);
            activities.stream()
                .sorted(Comparator.comparing(Activity::getName))
                .filter((activity) -> {
                    return this.selectedCategories.isEmpty() || Arrays.stream(this.filterList.getComponents())
                        .filter(c -> c instanceof JToggleButton)
                        .map(c -> (JToggleButton) c)
                        .anyMatch(c -> c.isSelected() && (c.getText().equals("Favorite") ? activity.isFavorite() : c.getText().equals(activity.getCategory())));
                })
                .map((activity) -> new ActivityListItem(activity, this.plugin, this.activityManager, this.muxer))
                .forEach(this.allActivityListItems::add);
            this.onSearchBarChanged(this.search.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JToggleButton makeFilterButton(String label) {
        JToggleButton jToggleButton = new JToggleButton(label);
        jToggleButton.setFont(jToggleButton.getFont().deriveFont(12f));
        jToggleButton.setSelected(this.selectedCategories.contains(label));
        jToggleButton.addActionListener((ev) -> {
            if (jToggleButton.isSelected()) {
                this.selectedCategories.add(label);
            } else {
                this.selectedCategories.remove(label);
            }
            this.reloadList(false);
        });
        jToggleButton.putClientProperty("JButton.buttonType", "toolBarButton");
//        jToggleButton.setBorder(BorderFactory.createLineBorder(ColorScheme.TEXT_COLOR, 1));
        Theme.applyStyle(jToggleButton, Theme.ButtonType.SECONDARY, Theme.ButtonSize.SMALL);
        if (label.equals("Favorite")) {
            jToggleButton.setForeground(ColorScheme.BRAND_ORANGE);
        }
        return jToggleButton;
    }

    private void onSearchBarChanged(String text) {
        this.mainPanel.removeAll();
        this.allActivityListItems.stream()
            .filter(item -> Text.matchesSearchTerms(SPLITTER.split(text.toLowerCase()), Lists.newArrayList(SPLITTER.split(item.getActivity().getName().toLowerCase()))))
            .forEach(this.mainPanel::add);
        this.revalidate();
        SwingUtilities.invokeLater(() -> {
            if (this.scrollPane.getVerticalScrollBar().isVisible()) {
                this.scrollPane.setViewportBorder(VIEWPORT_BORDER);
            } else {
                this.scrollPane.setViewportBorder(null);
            }
        });
    }

    @Override
    public void onActivate() {
        this.rebuild();
    }

    @Override
    public void onDeactivate() {
    }
}
