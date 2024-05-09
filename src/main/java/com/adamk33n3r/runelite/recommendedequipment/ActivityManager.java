package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Singleton
public class ActivityManager {
    @Inject
    private ConfigManager configManager;
    @Inject
    private RecommendedEquipmentConfig config;
    @Inject
    private RecEquipClient client;

    public List<Activity> getActivities(boolean forceDownload) throws IOException {
        List<Activity> activities = this.client.downloadActivities(forceDownload);
        activities.forEach(activity -> {
            if (this.config.favorites().contains(activity.getName())) {
                activity.setFavorite(true);
            }
        });
        return activities;
    }

    public void saveFavorite(Activity activity) {
        Set<String> favorites = this.config.favorites();
        if (activity.isFavorite()) {
            favorites.add(activity.getName());
        } else {
            favorites.remove(activity.getName());
        }
        this.configManager.setConfiguration(RecommendedEquipmentConfig.CONFIG_GROUP, "favorites", favorites);
    }
}
