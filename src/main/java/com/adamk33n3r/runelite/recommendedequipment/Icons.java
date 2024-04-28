package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.util.ImageUtil;

import javax.swing.*;

public final class Icons {
    public static final ImageIcon ICON = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "icon.png"));
    public static final ImageIcon FILTER = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_filter.png"));

    private Icons() {
        throw new AssertionError();
    }
}
