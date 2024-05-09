package com.adamk33n3r.runelite.recommendedequipment;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Favorite {
    @EqualsAndHashCode.Include
    public final String activity;
    public boolean favorite;
    public List<String> loadouts = new ArrayList<>();
}
