package com.adamk33n3r.runelite.recommendedequipment;

import lombok.Getter;

public class EquipmentStyleListItem extends ClickableListItem {
    @Getter
    private final ActivityEquipmentStyle style;
    @Getter
    private boolean selected;
    private final RecommendedEquipmentPlugin plugin;

    public EquipmentStyleListItem(Activity activity, ActivityEquipmentStyle style, ActivityPanel parent, RecommendedEquipmentPlugin plugin, ActivityManager activityManager) {
        super(style.getName(), null, style.isFavorite(), () -> {
            style.setFavorite(!style.isFavorite());
            activityManager.saveFavorite(activity);
        });
        this.plugin = plugin;
        this.style = style;

        this.setOnClick(() -> parent.selectLoadout(this));
    }

    public void select() {
        this.selected = true;
        Util.addStyleClass(this, "activity selected");
        this.plugin.setActivityEquipmentStyle(this.style);
    }

    public void deselect(boolean setStyle) {
        this.selected = false;
        Util.addStyleClass(this, "activity");
        if (setStyle && this.plugin.getActivityEquipmentStyle() == this.style) {
            this.plugin.setActivityEquipmentStyle(null);
        }
    }
}
