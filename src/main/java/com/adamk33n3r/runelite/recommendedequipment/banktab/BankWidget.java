package com.adamk33n3r.runelite.recommendedequipment.banktab;

import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;

public class BankWidget {
    public Widget itemWidget;

    public BankWidget(Widget itemWidget) {
        this.itemWidget = itemWidget;
    }

    public boolean isPointOverWidget(Point point)
    {
        return itemWidget.contains(point);
    }

    public int getItemID() {
        return itemWidget.getItemId();
    }

    public int getItemQuantity() {
        return itemWidget.getItemQuantity();
    }

    public void swap(BankWidget otherWidget) {
        int otherXItem = otherWidget.itemWidget.getOriginalX();
        int otherYItem = otherWidget.itemWidget.getOriginalY();

        otherWidget.swapPosition(otherWidget.itemWidget, itemWidget);
        swapPosition(itemWidget, otherXItem, otherYItem);
    }

    private void swapPosition(Widget thisWidget, Widget otherWidget) {
        thisWidget.setOriginalX(otherWidget.getOriginalX());
        thisWidget.setOriginalY(otherWidget.getOriginalY());
        thisWidget.revalidate();
    }

    private void swapPosition(Widget thisWidget, int x, int y) {
        thisWidget.setOriginalX(x);
        thisWidget.setOriginalY(y);
        thisWidget.revalidate();
    }
}

