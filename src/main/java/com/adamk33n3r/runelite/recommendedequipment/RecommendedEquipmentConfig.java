package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.util.HashSet;
import java.util.Set;

@ConfigGroup(RecommendedEquipmentConfig.CONFIG_GROUP)
public interface RecommendedEquipmentConfig extends Config {
    String CONFIG_GROUP = "recommended-equipment";

    @ConfigItem(
        keyName = "favorites",
        name = "Favorites",
        description = "The list of favorite activities.",
        hidden = true
    )
    default Set<String> favorites() {
        return new HashSet<>();
    }
}
