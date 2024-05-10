package com.adamk33n3r.runelite.recommendedequipment;

import lombok.Getter;
import net.runelite.client.ui.MultiplexingPluginPanel;

@Getter
public class ActivityListItem extends ClickableListItem {
    private final Activity activity;

    public ActivityListItem(Activity activity, RecommendedEquipmentPlugin plugin, ActivityManager activityManager, MultiplexingPluginPanel muxer) {
        super(activity.getName(),
            activity.getCategory(),
            activity.isFavorite(),
            () -> {
                activity.setFavorite(!activity.isFavorite());
                activityManager.saveFavorite(activity);
            }
        );
        this.activity = activity;
        this.setToolTipText(activity.getName());
        this.setOnClick(() -> muxer.pushState(new ActivityPanel(activity, plugin, activityManager, muxer)));
    }
}
