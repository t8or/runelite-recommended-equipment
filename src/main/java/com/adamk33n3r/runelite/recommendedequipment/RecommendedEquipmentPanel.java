package com.adamk33n3r.runelite.recommendedequipment;

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
    private Set<String> selectedCategories = new HashSet<>();

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
        Util.addStyleClass(this.search, "rounded5");
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
        Util.addStyleClass(this.filterList, "rounded5");
        this.filterList.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.filterList.add(new JLabel("Filter list"));
        filterArea.add(this.filterList);

        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new GridLayout(0, 1, 0, 5));
        this.mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ScrollablePanel northPanel = new ScrollablePanel(new BorderLayout());
        northPanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        northPanel.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        northPanel.setScrollableBlockIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        northPanel.setScrollableUnitIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        northPanel.add(this.mainPanel, BorderLayout.NORTH);
        JButton btn = new JButton("Reload");
        Util.addStyleClass(btn, "rounded");
        btn.addActionListener((ev) -> {
            reloadList(true);
        });
        this.add(btn, BorderLayout.SOUTH);

        this.scrollPane = new JScrollPane(northPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 10));
        // For the horizontal rule border
        this.scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.scrollPane.setBorder(new HorizontalRuleBorder(10, HorizontalRuleBorder.BOTH));
        this.add(this.scrollPane, BorderLayout.CENTER);

        this.reloadList(false);
    }

    private void reloadList(boolean forceDownload) {
        try {
            this.allActivityListItems.clear();
            List<Activity> activities = this.activityManager.getActivities(forceDownload);
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
        if (label.equals("Favorite")) {
            Util.addStyleClass(jToggleButton, "filter favorite-filter");
        } else {
            Util.addStyleClass(jToggleButton, "filter");
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
        RecEquipLAF.setup();
        this.rebuild();
    }

    @Override
    public void onDeactivate() {
        RuneLiteLAF.setup();
    }
}
