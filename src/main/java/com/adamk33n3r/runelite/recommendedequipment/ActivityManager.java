package com.adamk33n3r.runelite.recommendedequipment;

import com.google.gson.Gson;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class ActivityManager {
    private final ConfigManager configManager;
    private final RecommendedEquipmentConfig config;
    private final RecEquipClient client;

    @Inject
    public ActivityManager(ConfigManager configManager, RecommendedEquipmentConfig config, RecEquipClient client, Gson gson) {
        this.configManager = configManager;
        this.config = config;
        this.client = client;
    }

    public List<Activity> getActivities(boolean forceDownload) throws IOException {
        List<Activity> activities = this.client.downloadActivities(forceDownload);
        activities.forEach(activity -> {
            this.config.favorites().stream()
                .filter(f -> f.getActivity().equals(activity.getName()))
                .findFirst()
                .ifPresent(favorite -> {
                    activity.setFavorite(favorite.isFavorite());
                    activity.getEquipmentStyles().forEach(style -> {
                        if (favorite.getLoadouts().contains(style.getName())) {
                            style.setFavorite(true);
                        }
                    });
                });
        });
        return activities;
    }

    public void saveFavorite(Activity activity) {
        Set<Favorite> favorites = this.config.favorites();
        // Remove if there are no favorites
        if (!activity.isFavorite() && activity.getEquipmentStyles().stream().noneMatch(ActivityEquipmentStyle::isFavorite)) {
            favorites.removeIf(f -> f.getActivity().equals(activity.getName()));
        } else {
            Favorite favorite = favorites.stream()
                .filter(f -> f.getActivity().equals(activity.getName()))
                .findFirst()
                .orElse(new Favorite(activity.getName()));
            favorite.setFavorite(activity.isFavorite());
            favorite.setLoadouts(activity.getEquipmentStyles().stream()
                .filter(ActivityEquipmentStyle::isFavorite)
                .map(ActivityEquipmentStyle::getName)
                .collect(Collectors.toList()));
            favorites.add(favorite);
        }
        this.config.favorites(favorites);
    }
}
