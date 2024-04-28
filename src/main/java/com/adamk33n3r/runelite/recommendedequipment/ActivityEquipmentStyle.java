package com.adamk33n3r.runelite.recommendedequipment;

import lombok.Data;

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
}
