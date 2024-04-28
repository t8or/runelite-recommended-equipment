package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.ScriptID;
import net.runelite.api.SpriteID;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BankTab {
    private static final int ITEMS_PER_ROW = 8;
    private static final int ITEM_VERTICAL_SPACING = 36;
    private static final int ITEM_HORIZONTAL_SPACING = 48;
    private static final int ITEM_ROW_START = 51;
    private static final int LINE_VERTICAL_SPACING = 5;
    private static final int LINE_HEIGHT = 2;
    private static final int TEXT_HEIGHT = 15;
    private static final int ITEM_HEIGHT = 32;
    private static final int ITEM_WIDTH = 36;
    private static final int EMPTY_BANK_SLOT_ID = 6512;

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private BankFilterButton bankFilterButton;

    private final List<Widget> addedWidgets = new ArrayList<>();

    public void startUp() {
    }

    public void shutDown() {
        this.clientThread.invokeLater(this.bankFilterButton::destroy);
        if (!this.addedWidgets.isEmpty()) {
            for (Widget addedWidget : this.addedWidgets) {
                addedWidget.setHidden(true);
            }
            this.addedWidgets.clear();
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == InterfaceID.BANK /* check if a recommended gear tab is enabled */) {
            bankFilterButton.init();
        }
    }
    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        bankFilterButton.handleClick(event);
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired event) {
        int scriptId = event.getScriptId();

        if (scriptId == ScriptID.BANKMAIN_FINISHBUILDING) {
            if (this.bankFilterButton.isTabActive()) {
                Widget bankTitle = this.client.getWidget(ComponentID.BANK_TITLE_BAR);
                if (bankTitle != null) {
                    bankTitle.setText("Recommended Gear");
                }
            }
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() != ScriptID.BANKMAIN_FINISHBUILDING) {
            return;
        }

        if (!this.bankFilterButton.isTabActive()) {
            return;
        }

        Widget itemContainer = this.client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
        if (itemContainer == null) {
            return;
        }

        if (!this.addedWidgets.isEmpty()) {
            for (Widget addedWidget : this.addedWidgets) {
                addedWidget.setHidden(true);
            }
            this.addedWidgets.clear();
        }

//        fakeToRealItem.clear();

        Widget[] containerChildren = itemContainer.getDynamicChildren();
        this.clientThread.invokeAtTickEnd(() -> {
//            List<BankTabItems> tabLayout = questHelper.getPluginBankTagItemsForSections(true);
//            if (tabLayout != null) {
//                sortBankTabItems(itemContainer, containerChildren, tabLayout);
//            }
            int totalSectionsHeight = 0;
            List<Integer> itemList = new ArrayList<>();
            for (Widget itemWidget : containerChildren) {
                // Hide tab dividers
                if (itemWidget.getSpriteId() == SpriteID.RESIZEABLE_MODE_SIDE_PANEL_BACKGROUND || itemWidget.getText().contains("Tab")) {
                    itemWidget.setHidden(true);
                } else if (!itemWidget.isHidden() && itemWidget.getItemId() !=- -1 && itemWidget.getItemId() != EMPTY_BANK_SLOT_ID) {
                    itemList.add(itemWidget.getItemId());
                    itemWidget.setHidden(true);
                }
            }
            this.addSectionHeader(itemContainer, "Recommended Gear", 0);
            Widget testItem = createText(itemContainer, "Test Item", Color.WHITE.getRGB(), 48, 15 - 3, 50, 50);
            this.addedWidgets.add(testItem);
            Widget testItemSprite = createGraphic(itemContainer, SpriteID.COMBAT_STYLE_SWORD_SLASH, ITEM_WIDTH, ITEM_HEIGHT, 50, 15);
            this.addedWidgets.add(testItemSprite);

            final Widget bankItemContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
            if (bankItemContainer == null) return;
            int itemContainerHeight = bankItemContainer.getHeight();

//            bankItemContainer.setScrollHeight(Math.max(totalSectionsHeight, itemContainerHeight));
        });
    }

    private int addSectionHeader(Widget itemContainer, String title, int totalSectionsHeight) {
        addedWidgets.add(createGraphic(itemContainer, SpriteID.RESIZEABLE_MODE_SIDE_PANEL_BACKGROUND, ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING, LINE_HEIGHT, ITEM_ROW_START, totalSectionsHeight));
        addedWidgets.add(createText(itemContainer, title, new Color(228, 216, 162).getRGB(), (ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING) + ITEM_ROW_START
            , TEXT_HEIGHT, ITEM_ROW_START, totalSectionsHeight + LINE_VERTICAL_SPACING));

        return totalSectionsHeight + LINE_VERTICAL_SPACING + TEXT_HEIGHT;
    }
    private Widget createText(Widget container, String text, int color, int width, int height, int x, int y)
    {
        Widget widget = container.createChild(-1, WidgetType.TEXT);
        widget.setOriginalWidth(width);
        widget.setOriginalHeight(height);
        widget.setOriginalX(x);
        widget.setOriginalY(y);

        widget.setText(text);
        widget.setFontId(FontID.PLAIN_11);
        widget.setTextColor(color);
        widget.setTextShadowed(true);

        widget.revalidate();

        return widget;
    }
    private Widget createGraphic(Widget container, int spriteId, int width, int height, int x, int y)
    {
        Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
        widget.setOriginalWidth(width);
        widget.setOriginalHeight(height);
        widget.setOriginalX(x);
        widget.setOriginalY(y);

        widget.setSpriteId(spriteId);

        widget.revalidate();

        return widget;
    }
}
