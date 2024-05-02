package com.adamk33n3r.runelite.recommendedequipment;

import com.formdev.flatlaf.FlatClientProperties;
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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class RecommendedEquipmentPanel extends PluginPanel {
    @Inject
    private RecEquipClient recEquipClient;
    @Inject
    private RecommendedEquipmentPlugin plugin;

    @Getter
    private final MultiplexingPluginPanel muxer = new MultiplexingPluginPanel(this) {
        @Override
        public void onAdd(PluginPanel p) {
            System.out.println("onAdd:"+p);
        }
        @Override
        public void onRemove(PluginPanel p) {
            System.out.println("onRemove:"+p);
        }
    };
    private final List<ActivityListItem> allActivityListItems = new ArrayList<>();
    private JPanel mainPanel;
    private IconTextField search;

    private static final Splitter SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();

    public RecommendedEquipmentPanel() {
        super(false);
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
    }

    public void rebuild() {
        this.removeAll();

        JPanel topPanel = new JPanel();
//        topPanel.setBorder(new EmptyBorder(10, 10, 0, 10));
        topPanel.setLayout(new BorderLayout(0, BORDER_OFFSET));
        this.add(topPanel, BorderLayout.NORTH);

        JPanel filterArea = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.add(filterArea, BorderLayout.CENTER);

        this.search = new IconTextField();
//        int i = 0;
//        for (Component comp : search.getComponents()) {
//            System.out.println(comp);
//            JComponent jcomp = (JComponent) comp;
//            ((JComponent) comp).putClientProperty(FlatClientProperties.STYLE_CLASS, "rounded15");
//            ((JComponent) comp).putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
//            if (i == 1) {
//                System.out.println(jcomp.getComponent(0));
//                JComponent jTextField = (JComponent) jcomp.getComponent(0);
////                jTextField.putClientProperty(FlatClientProperties.STYLE_CLASS, "rounded15");
//                jTextField.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
//                jTextField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
//
////                jcomp.getComponent(0).setBackground(ColorScheme.GRAND_EXCHANGE_ALCH);
//                jTextField.setOpaque(true);
//            }
//            i++;
//        }
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

        JPanel filterList = new JPanel();
        Util.addStyleClass(filterList, "rounded5");
        filterList.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        filterList.add(new JLabel("Filter list"));
        filterList.add(makeFilterButton("Slayer"));
        filterList.add(makeFilterButton("Minigame"));
        filterList.add(makeFilterButton("World"));
        filterArea.add(filterList);

//        JButton reloadButton = new JButton("Reload");
//        reloadButton.addActionListener((ev) -> {
//            Util.runWithLAF(this::rebuild);
//        });
//        topPanel.add(reloadButton, BorderLayout.SOUTH);

        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
        this.mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(this.mainPanel, BorderLayout.NORTH);
        JButton btn = new JButton("reload");
        btn.addActionListener((ev) -> {
            reloadList(true);
        });
        this.add(btn, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(northPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // For the horizontal rule border
        scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
        scrollPane.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 10, 0), new HorizontalRuleBorder(10)));
        this.add(scrollPane, BorderLayout.CENTER);

        this.reloadList(false);
    }

    private void reloadList(boolean forceDownload) {
        try {
            this.allActivityListItems.clear();
            List<Activity> activities = this.recEquipClient.downloadActivities(forceDownload);
            Util.runWithLAF(() -> {
                activities.stream()
                    .map((activity) -> new ActivityListItem(activity, this.plugin, this.muxer))
                    .forEach(this.allActivityListItems::add);
                this.onSearchBarChanged(this.search.getText());
                return null;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JToggleButton makeFilterButton(String label) {
        JToggleButton jToggleButton = new JToggleButton(label);
        jToggleButton.setPreferredSize(new Dimension(50, 20));
        jToggleButton.setFont(jToggleButton.getFont().deriveFont(12f));
        Util.addStyleClass(jToggleButton, "filter");
        return jToggleButton;
    }

    private void onSearchBarChanged(String text) {
        this.mainPanel.removeAll();
        this.allActivityListItems.stream()
            .filter(item -> Text.matchesSearchTerms(SPLITTER.split(text.toLowerCase()), Lists.newArrayList(SPLITTER.split(item.getActivity().getName().toLowerCase()))))
            .forEach(this.mainPanel::add);
        this.revalidate();
//        Util.runWithLAF(() -> {
//            this.mainPanel.add(new ActivityListItem(new Activity("Abyssal Sire", "Slayer", false)));
//            this.mainPanel.add(new ActivityListItem(new Activity("Alchemical Hydra", "Slayer", false)));
//            this.mainPanel.add(new ActivityListItem(new Activity("Jad", "Minigame", true)));
//            this.mainPanel.add(new ActivityListItem(new Activity("Zalcano", "Skilling", false)));
//        });
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
