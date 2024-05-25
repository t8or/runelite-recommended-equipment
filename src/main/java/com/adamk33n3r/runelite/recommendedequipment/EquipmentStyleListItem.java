package com.adamk33n3r.runelite.recommendedequipment;

import lombok.Getter;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.Border;

public class EquipmentStyleListItem extends ClickableListItem {
    @Getter
    private final ActivityEquipmentStyle style;
    @Getter
    private boolean selected;
    private final RecommendedEquipmentPlugin plugin;

    private final Border selectedBorder;
    private final Border unselectedBorder;

    public EquipmentStyleListItem(Activity activity, ActivityEquipmentStyle style, ActivityPanel parent, RecommendedEquipmentPlugin plugin, ActivityManager activityManager) {
        super(style.getName(), null, style.isFavorite(), () -> {
            style.setFavorite(!style.isFavorite());
            activityManager.saveFavorite(activity);
        });
        this.plugin = plugin;
        this.style = style;

        this.setOnClick(() -> parent.selectLoadout(this));
        Border emptyBorder = BorderFactory.createEmptyBorder(3, 5, 3, 5);
        this.selectedBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ColorScheme.TEXT_COLOR, 1), emptyBorder);
        this.unselectedBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR, 1), emptyBorder);
        this.setBorder(this.unselectedBorder);
    }

    public void select() {
        this.selected = true;
        this.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.setBorder(this.selectedBorder);
        this.plugin.setActivityEquipmentStyle(this.style);
    }

    public void deselect(boolean setStyle) {
        this.selected = false;
        this.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
        this.setBorder(this.unselectedBorder);
        if (setStyle && this.plugin.getActivityEquipmentStyle() == this.style) {
            this.plugin.setActivityEquipmentStyle(null);
        }
    }
}
