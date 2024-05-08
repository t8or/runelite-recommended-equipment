package com.adamk33n3r.runelite.recommendedequipment;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Activity {
    private String name;
    private String category;
    private boolean favorite;

    @SerializedName("styles")
    private List<ActivityEquipmentStyle> equipmentStyles = new ArrayList<>();

    public Activity(String activityName, String category, boolean favorite) {
        this.name = activityName;
        this.category = category;
        this.favorite = favorite;
    }
}
