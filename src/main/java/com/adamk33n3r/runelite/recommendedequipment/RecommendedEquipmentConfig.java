package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ConfigGroup(RecommendedEquipmentConfig.CONFIG_GROUP)
public interface RecommendedEquipmentConfig extends Config {
    String CONFIG_GROUP = "recommended-equipment";

    @ConfigItem(
        keyName = "favorites",
        name = "Favorites",
        description = "The serialized list of favorite activities and equipment styles.",
        hidden = true
    )
    default Set<Favorite> favorites() {
        return new HashSet<>();
    }

    // Setter
    @ConfigItem(
        keyName = "favorites",
        name = "Favorites",
        description = "The serialized list of favorite activities and equipment styles.",
        hidden = true
    )
    void favorites(Set<Favorite> favorites);
}
