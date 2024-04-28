package com.adamk33n3r.runelite.recommendedequipment;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import lombok.SneakyThrows;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.laf.RuneLiteLAF;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecEquipLAF extends RuneLiteLAF {
    @SneakyThrows
    public RecEquipLAF() {
        Map<String, String> extras = new HashMap<>();
        // add all of ColorScheme as variables for the properties
        for (Field f : ColorScheme.class.getDeclaredFields())
        {
            if (Modifier.isStatic(f.getModifiers()) && Color.class == f.getType())
            {
                String name = f.getName();
                if (name.endsWith("_COLOR"))
                {
                    name = name.substring(0, name.length() - 6);
                }

                Color color = (Color) f.get(null);
                extras.put("@" + name, String.format("#%06x%02x", color.getRGB() & 0xFFFFFF, color.getRGB() >>> 24));
            }
        }
        setExtraDefaults(extras);
    }

    @Override
    protected List<Class<?>> getLafClassesForDefaultsLoading() {
        return List.of(FlatLaf.class, FlatDarkLaf.class, RuneLiteLAF.class, RecEquipLAF.class);
    }

    public static boolean setup() {
        return setup(new RecEquipLAF());
    }

    @Override
    public String getName() {
        return "RecEquipLAF";
    }
}
