package com.adamk33n3r.runelite.recommendedequipment;

import lombok.Data;

import java.util.List;

@Data
public class ActivityItem {
    private String name;
    private List<Integer> itemIDs;
}

