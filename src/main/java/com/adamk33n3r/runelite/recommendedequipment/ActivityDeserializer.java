package com.adamk33n3r.runelite.recommendedequipment;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ActivityDeserializer implements JsonDeserializer<Activity> {
    @Override
    public Activity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        Activity activity = new Activity();
        jsonElement.getAsJsonObject().entrySet().forEach(entry -> {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            ActivityEquipmentStyle activityEquipmentStyle = context.deserialize(value, ActivityEquipmentStyle.class);
            activityEquipmentStyle.setName(key);
            activity.getEquipmentStyles().add(activityEquipmentStyle);
        });
        return activity;
    }
}
