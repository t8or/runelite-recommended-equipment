package com.adamk33n3r.runelite.recommendedequipment;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ActivitySlotTierDeserializer implements JsonDeserializer<ActivitySlotTier> {
    @Override
    public ActivitySlotTier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        ActivitySlotTier activitySlotTier = new ActivitySlotTier();
        jsonElement.getAsJsonObject().entrySet().forEach(entry -> {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            ActivityItem activityItem = new ActivityItem();
            activityItem.setName(key);
            activityItem.setItemIDs(context.deserialize(value, int[].class));
            activitySlotTier.getItems().add(activityItem);
        });
        return activitySlotTier;
    }
}
