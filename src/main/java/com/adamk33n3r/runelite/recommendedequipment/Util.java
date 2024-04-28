package com.adamk33n3r.runelite.recommendedequipment;

import com.formdev.flatlaf.FlatClientProperties;
import net.runelite.client.ui.laf.RuneLiteLAF;

import java.awt.Color;
import java.util.function.Supplier;

import javax.swing.JComponent;

public class Util {
    public static void setColor(JComponent component, Color color) {
//        component.getUI().getPropertyPrefix()
    }

//    public static void runWithLAF(Runnable runnable) {
//        RecEquipLAF.setup();
//        runnable.run();
//        RuneLiteLAF.setup();
//    }

    public static <T> T runWithLAF(Supplier<T> callable) {
        RecEquipLAF.setup();
        T ret = callable.get();
        RuneLiteLAF.setup();
        return ret;
    }

    public static void addStyleClass(JComponent component, String styleClass) {
        component.putClientProperty(FlatClientProperties.STYLE_CLASS, styleClass);
    }
}
