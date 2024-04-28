package com.adamk33n3r.runelite.recommendedequipment;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ActivityEquipmentStyleDeserializer implements JsonDeserializer<ActivityEquipmentStyle> {
    @Override
    public ActivityEquipmentStyle deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
//        ActivityEquipmentStyle activityEquipmentStyle = new ActivityEquipmentStyle();
//        jsonElement.getAsJsonObject().entrySet().forEach(entry -> {
//            String key = entry.getKey();
//            JsonElement value = entry.getValue();
//            ActivitySlotTier activitySlotTier = context.deserialize(value, ActivitySlotTier.class);
//            activitySlotTier.setName(key);
//            activity.getEquipmentStyles().add(activityEquipmentStyle);
//        });
//        return activity;
        return null;
    }
}
