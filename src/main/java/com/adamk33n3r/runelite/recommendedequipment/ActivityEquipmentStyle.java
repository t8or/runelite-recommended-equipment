package com.adamk33n3r.runelite.recommendedequipment;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Data
public class ActivityEquipmentStyle {
    private String name;
    private List<ActivitySlotTier> head;
    private List<ActivitySlotTier> neck;
    private List<ActivitySlotTier> cape;
    private List<ActivitySlotTier> body;
    private List<ActivitySlotTier> legs;
    private List<ActivitySlotTier> weapon;
    private List<ActivitySlotTier> shield;
    private List<ActivitySlotTier> ammo;
    /*
    1st tier = rada 4
    2nd tier = god blessing OR rada 3
    "ammo": [
      {
        "Rada's blessing 4": [
          "22947"
        ]
      },
      {
        "God blessing": [
          "20220",
          "20223",
          "20226",
          "20229",
          "20232",
          "20235"
        ],
        "Rada's blessing 3": [
          "22945"
        ]
      }
    ],
     */
    private List<ActivitySlotTier> hands;
    private List<ActivitySlotTier> feet;
    /*
    "feet": [
      {
        "Pegasian boots": [
          "13237"
        ]
      },
      {
        "Blessed boots": [
          "19921",
          "19930",
          "19924",
          "19927",
          "19933",
          "19936"
        ]
      }
    ],
     */
    private List<ActivitySlotTier> ring;
    private List<ActivitySlotTier> special;

    private transient boolean favorite;

    public List<Pair<String, List<ActivitySlotTier>>> getSlots() {
        // TODO: should probably be a scraper data change
        return List.of(
            Pair.of("Head", head),
            Pair.of("Neck", neck),
            Pair.of("Cape", cape),
            Pair.of("Body", body),
            Pair.of("Legs", legs),
            Pair.of("Weapon", weapon),
            Pair.of("Shield", shield),
            Pair.of("Ammo", ammo),
            Pair.of("Hands", hands),
            Pair.of("Feet", feet),
            Pair.of("Ring", ring),
            Pair.of("Special", special)
        );
    }
}
