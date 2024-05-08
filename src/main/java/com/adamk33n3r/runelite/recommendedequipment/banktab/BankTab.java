package com.adamk33n3r.runelite.recommendedequipment.banktab;

import com.adamk33n3r.runelite.recommendedequipment.*;

import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import java.awt.*;
import java.awt.Point;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
    private static final int ITEM_HORIZONTAL_GAP = ITEM_HORIZONTAL_SPACING - ITEM_WIDTH;
    private static final int EMPTY_BANK_SLOT_ID = 6512;

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private BankFilterButton bankFilterButton;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private RecommendedEquipmentPlugin recommendedEquipmentPlugin;

    private final List<Widget> addedWidgets = new ArrayList<>();
    private final Map<Widget, ActivityItem> widgetItems = new HashMap<>();
    private final Map<BankWidget, BankWidget> fakeToRealItem = new HashMap<>();

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

    public boolean isActive() {
        return this.bankFilterButton.isTabActive();
    }

    public void resetTab() {
        this.clientThread.invokeLater(this.bankFilterButton::refreshTab);
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == InterfaceID.BANK && this.recommendedEquipmentPlugin.getActivityEquipmentStyle() != null) {
            this.bankFilterButton.init();
        }
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        if (!bankFilterButton.isTabActive() || bankFilterButton.isHidden()) return;

        net.runelite.api.Point mousePoint = client.getMouseCanvasPosition();
        if (fakeToRealItem.isEmpty()) {
            return;
        }

        for (BankWidget bankWidget : fakeToRealItem.keySet())
        {
            if (bankWidget.isPointOverWidget(mousePoint))
            {
                bankWidget.swap(fakeToRealItem.get(bankWidget));
                return;
            }
        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event) {
        String eventName = event.getEventName();

        int[] intStack = client.getIntStack();
        int intStackSize = client.getIntStackSize();

        if ("getSearchingTagTab".equals(eventName)) {
            intStack[intStackSize - 1] = this.bankFilterButton.isTabActive() ? 1 : 0;
            if (this.bankFilterButton.isTabActive())
            {
                // As we're on the recommended equipment tab, we don't need to check again for tab tags
                // Change the name of the event so as to not proc another check
                event.setEventName("getSearchingRecEquipTab");
            }
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
                    bankTitle.setText(this.recommendedEquipmentPlugin.getActivityEquipmentStyle().getName());
                }
            }
        } else if (scriptId == ScriptID.BANKMAIN_SEARCH_TOGGLE) {
            this.bankFilterButton.handleSearch();
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
//        int SEARCHBOX_LOADED = 750;
//        if (event.getScriptId() == SEARCHBOX_LOADED)
//        {
//            geButtonWidget.init();
//        }
        if (event.getScriptId() == ScriptID.BANKMAIN_SEARCHING) {
            // The return value of bankmain_searching is on the stack. If we have a tag tab active
            // make it return true to put the bank in a searching state.
            if (bankFilterButton.isTabActive()) {
                client.getIntStack()[client.getIntStackSize() - 1] = 1; // true
            }
            if (!addedWidgets.isEmpty()) {
                for (Widget addedWidget : addedWidgets) {
                    addedWidget.setHidden(true);
                }
                addedWidgets.clear();
            }
            fakeToRealItem.clear();

            return;
        }

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

        fakeToRealItem.clear();

        Widget[] containerChildren = itemContainer.getDynamicChildren();
        this.clientThread.invokeAtTickEnd(() -> {
            ActivityEquipmentStyle equipmentStyle = recommendedEquipmentPlugin.getActivityEquipmentStyle();
            if (equipmentStyle != null) {
                sortBankTabItems(itemContainer, containerChildren, equipmentStyle);
            }
        });
    }

    private void sortBankTabItems(Widget itemContainer, Widget[] containerChildren, ActivityEquipmentStyle newLayout)
    {
        int totalSectionsHeight = 0;

        List<Integer> itemList = new ArrayList<>();
        for (Widget itemWidget : containerChildren)
        {
            if (itemWidget.getSpriteId() == SpriteID.RESIZEABLE_MODE_SIDE_PANEL_BACKGROUND
                || itemWidget.getText().contains("Tab"))
            {
                itemWidget.setHidden(true);
            }
            else if (!itemWidget.isHidden() &&
                itemWidget.getItemId() != -1 &&
                !itemList.contains(itemWidget.getItemId()) &&
                itemWidget.getItemId() != EMPTY_BANK_SLOT_ID)
            {
                itemList.add(itemWidget.getItemId());
            }
        }

        List<String> bankItemTexts = new ArrayList<>();
        HashMap<Integer, BankWidget> itemIDsAdded = new HashMap<>();

        for (Pair<String, List<ActivitySlotTier>> bankTabItems : newLayout.getSlots())
        {
            totalSectionsHeight = addSlotTabSection(itemContainer, bankTabItems.getKey(), bankTabItems.getValue(), itemList, totalSectionsHeight, bankItemTexts, itemIDsAdded);
        }

        // We add item texts after all items are added so they always overlay
//        for (BankText bankText : bankItemTexts)
//        {
//            Widget realItemQuantityText = createText(itemContainer,
//                bankText.text,
//                Color.WHITE.getRGB(),
//                ITEM_HORIZONTAL_SPACING,
//                TEXT_HEIGHT - 3,
//                bankText.x,
//                bankText.y);
//
//            addedWidgets.add(realItemQuantityText);
//
//            if (bankText.spriteID != -1)
//            {
//                Widget realItemInInventorySprite = createIcon(itemContainer,
//                    bankText.spriteID,
//                    10,
//                    10,
//                    bankText.spriteX,
//                    bankText.spriteY
//                );
//                addedWidgets.add(realItemInInventorySprite);
//            }
//
//        }

        totalSectionsHeight = addGeneralSection(itemContainer, itemList, totalSectionsHeight);

        final Widget bankItemContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
        if (bankItemContainer == null) return;
        int itemContainerHeight = bankItemContainer.getHeight();

        bankItemContainer.setScrollHeight(Math.max(totalSectionsHeight, itemContainerHeight));

        final int itemContainerScroll = bankItemContainer.getScrollY();
        clientThread.invokeLater(() ->
            client.runScript(ScriptID.UPDATE_SCROLLBAR,
                ComponentID.BANK_SCROLLBAR,
                ComponentID.BANK_ITEM_CONTAINER,
                itemContainerScroll));
    }

    private int addGeneralSection(Widget itemContainer, List<Integer> items, int totalSectionsHeight) {
        int totalItemsAdded = 0;

        if (items.isEmpty())
        {
            return totalSectionsHeight;
        }

        for (Integer itemID : items)
        {
            for (Widget widget : itemContainer.getDynamicChildren())
            {
                if (!widget.isHidden() && widget.getOpacity() != 150 && widget.getItemId() == itemID)
                {
                    if (totalItemsAdded == 0)
                    {
                        totalSectionsHeight = addSectionHeader(itemContainer, "General", totalSectionsHeight);
                    }

                    placeItem(widget, totalItemsAdded, totalSectionsHeight);
                    totalItemsAdded++;
                }
            }
        }
        int newHeight = totalSectionsHeight + (totalItemsAdded / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;
        newHeight = totalItemsAdded % ITEMS_PER_ROW != 0 ? newHeight + ITEM_VERTICAL_SPACING : newHeight;

        return newHeight;
    }

    private int addSectionHeader(Widget itemContainer, String title, int totalSectionsHeight) {
        addedWidgets.add(createGraphic(itemContainer, SpriteID.RESIZEABLE_MODE_SIDE_PANEL_BACKGROUND, ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING, LINE_HEIGHT, ITEM_ROW_START, totalSectionsHeight));
        addedWidgets.add(createText(itemContainer, title, new Color(228, 216, 162).getRGB(), (ITEMS_PER_ROW * ITEM_HORIZONTAL_SPACING) + ITEM_ROW_START
            , TEXT_HEIGHT, ITEM_ROW_START, totalSectionsHeight + LINE_VERTICAL_SPACING));

        return totalSectionsHeight + LINE_VERTICAL_SPACING + TEXT_HEIGHT;
    }

    private int addTierDivider(Widget itemContainer, int totalItemsAdded, int totalSectionsHeight) {
        int adjXOffset = ((totalItemsAdded) % (ITEMS_PER_ROW+1)) * ITEM_HORIZONTAL_SPACING + ITEM_ROW_START - ITEM_HORIZONTAL_GAP;
        int adjYOffset = totalSectionsHeight + ((totalItemsAdded - 1) / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;
        addedWidgets.add(createGraphic(itemContainer, SpriteID.RESIZEABLE_MODE_SIDE_PANEL_BACKGROUND, 2, ITEM_VERTICAL_SPACING - 6, adjXOffset, adjYOffset));
        return totalSectionsHeight;
    }

    private int addSlotTabSection(Widget itemContainer,
                                  String slotName,
                                  List<ActivitySlotTier> items,
                                  List<Integer> ownedItemIDs,
                                  int totalSectionsHeight,
                                  List<String> bankItemTexts,
                                  Map<Integer, BankWidget> itemIDsAdded) {
        int newHeight = totalSectionsHeight;

         if (items.isEmpty()) {
             return newHeight;
         }

         // Presume there'll be some content as we have fake items now
         newHeight = addSectionHeader(itemContainer, slotName, newHeight);
         newHeight = createPartialSection(itemContainer, items, ownedItemIDs, newHeight, itemIDsAdded);

         return newHeight;
    }

    private int createPartialSection(Widget itemContainer,
                                     List<ActivitySlotTier> items,
                                     List<Integer> ownedItemIDs,
                                     int totalSectionsHeight,
                                     Map<Integer, BankWidget> itemIDsAdded) {
        int totalItemsAdded = 0;
        // Iterate over each tier
        int i = 0;
        for (ActivitySlotTier slotTier : items) {
            boolean foundItem = false;

            // Iterate over each item in the tier
            for (ActivityItem item : slotTier.getItems()) {
                // If owns one of the items
                if (!Collections.disjoint(ownedItemIDs, item.getItemIDs())) {
                    // Loop through bank to find item
                    for (Widget widget : itemContainer.getDynamicChildren()) {
                        if (!widget.isHidden() && widget.getOpacity() != 150 && (item.getItemIDs().contains(widget.getItemId()))) {
                            foundItem = true;

                            Point point = placeItem(widget, totalItemsAdded, totalSectionsHeight);
                            widget.setItemQuantityMode(1);

                            totalItemsAdded++;
                            ownedItemIDs.removeAll(Collections.singletonList(widget.getItemId()));
                            itemIDsAdded.put(widget.getItemId(), new BankWidget(widget));

                            break;
                        }
                    }
                }

                if (!foundItem) {
                    // calculate correct item position as if this was a normal tab
                    int adjXOffset = (totalItemsAdded % ITEMS_PER_ROW) * ITEM_HORIZONTAL_SPACING + ITEM_ROW_START;
                    int adjYOffset = totalSectionsHeight + (totalItemsAdded / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;

                    Widget fakeItemWidget;
                    // Have list of all real items + text. Do check to see if any of those items
                    // Match the ItemIDs
                    if (Collections.disjoint(itemIDsAdded.keySet(), item.getItemIDs()))
                    {
                        fakeItemWidget = createMissingItem(itemContainer, item, adjXOffset, adjYOffset);
                        ownedItemIDs.removeAll(item.getItemIDs());
                    }
                    else
                    {
                        List<Integer> result = item.getItemIDs().stream()
                            .distinct()
                            .filter(itemIDsAdded.keySet()::contains)
                            .collect(Collectors.toList());

                        BankWidget realItemWidget = itemIDsAdded.get((result.get(0)));

                        fakeItemWidget = createDuplicateItem(itemContainer, item,
                            realItemWidget.getItemQuantity(), adjXOffset, adjYOffset);

                        fakeToRealItem.put(new BankWidget(fakeItemWidget), realItemWidget);
                    }

//                    if (bankTabItem.getQuantity() > 0)
//                    {
//                        makeBankText(fakeItemWidget.getItemQuantity(), bankTabItem.getQuantity(), adjXOffset, adjYOffset, bankTabItem.getItemRequirement(), bankItemTexts);
//                    }

                    widgetItems.put(fakeItemWidget, item);
                    addedWidgets.add(fakeItemWidget);

                    totalItemsAdded++;
                }
            }

            if (i < items.size() - 1) {
                addTierDivider(itemContainer, totalItemsAdded, totalSectionsHeight);
            }


            i++;
        }

        int newHeight = totalSectionsHeight + (totalItemsAdded / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;
        newHeight = totalItemsAdded % ITEMS_PER_ROW == 0 ? newHeight : newHeight + ITEM_VERTICAL_SPACING;
        return newHeight;
    }

    private Point placeItem(Widget widget, int totalItemsAdded, int totalSectionsHeight) {
        int adjYOffset = totalSectionsHeight + (totalItemsAdded / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;
        int adjXOffset = (totalItemsAdded % ITEMS_PER_ROW) * ITEM_HORIZONTAL_SPACING + ITEM_ROW_START;

        if (widget.getOriginalY() != adjYOffset) {
            widget.setOriginalY(adjYOffset);
            widget.revalidate();
        }

        if (widget.getOriginalX() != adjXOffset) {
            widget.setOriginalX(adjXOffset);
            widget.revalidate();
        }

        return new Point(adjXOffset, adjYOffset);
    }

    private Widget createMissingItem(Widget container, ActivityItem activityItem, int x, int y)
    {
        Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
        widget.setItemQuantityMode(1); // quantity of 1 still shows number
        widget.setOriginalWidth(ITEM_WIDTH);
        widget.setOriginalHeight(ITEM_HEIGHT);
        widget.setOriginalX(x);
        widget.setOriginalY(y);

        List<Integer> itemIDs = activityItem.getItemIDs();
//        if (activityItem.getItemRequirement().getDisplayItemId() != null)
//        {
//            itemIDs = Collections.singletonList(activityItem.getItemRequirement().getDisplayItemId());
//        }

        if (itemIDs.isEmpty())
        {
            itemIDs.add(ItemID.CAKE_OF_GUIDANCE);
        }

        widget.setItemId(itemIDs.get(0));
        widget.setName("<col=ff9040>" + activityItem.getName() + "</col>");
//        if (activityItem.getDetails() != null) {
            widget.setText("Test tooltip: Missing");
//        }
        widget.setItemQuantity(0);
        widget.setOpacity(150);
        widget.setOnOpListener(ScriptID.NULL);
        widget.setHasListener(true);

        addTabActions(widget);

        widget.revalidate();

        return widget;
    }

    private Widget createDuplicateItem(Widget container, ActivityItem activityItem, int quantity, int x, int y) {
        Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
        widget.setItemQuantityMode(1); // quantity of 1 still shows number
        widget.setOriginalWidth(ITEM_WIDTH);
        widget.setOriginalHeight(ITEM_HEIGHT);
        widget.setOriginalX(x);
        widget.setOriginalY(y);
        widget.setBorderType(1);

        List<Integer> itemIDs = activityItem.getItemIDs();
//        if (activityItem.getDisplayID() != null) {
//            itemIDs = Collections.singletonList(activityItem.getDisplayID());
//        }

        widget.setItemId(itemIDs.get(0));
        widget.setName("<col=ff9040>" + activityItem.getName() + "</col>");
//        if (activityItem.getDetails() != null) {
            widget.setText("This is a test tooltip: Duplicate");
//        }
        widget.setItemQuantity(quantity);
        widget.setOnOpListener(ScriptID.NULL);
        widget.setHasListener(true);

        widget.revalidate();

        return widget;
    }

    private void addTabActions(Widget w) {
        w.setAction(1, "Details");

        w.setOnOpListener((JavaScriptCallback) this::handleFakeItemClick);
    }

    private void handleFakeItemClick(ScriptEvent event) {
        Widget widget = event.getSource();
        if (widget.getItemId() != -1)
        {
            String name = widget.getName();
//            ActivityItem item = widgetItems.get(widget);

//            String quantity = QuantityFormatter.formatNumber(item.getQuantity()) + " x ";
//            if (item.getQuantity() == -1)
//            {
//                quantity = "some ";
//            }
            final ChatMessageBuilder message = new ChatMessageBuilder()
                .append("You need ")
                .append(ChatColorType.HIGHLIGHT)
                .append("a ")
                .append(Text.removeTags(name))
                .append(".");

            if (!widget.getText().isEmpty())
            {
                message.append(ChatColorType.NORMAL)
                    .append(" " + widget.getText() + ".");
            }

            this.chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.ITEM_EXAMINE)
                .runeLiteFormattedMessage(message.build())
                .build());
        }
    }

    private Widget createText(Widget container, String text, int color, int width, int height, int x, int y) {
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

    private Widget createGraphic(Widget container, int spriteId, int width, int height, int x, int y) {
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
