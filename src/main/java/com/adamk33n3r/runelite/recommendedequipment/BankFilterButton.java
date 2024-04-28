package com.adamk33n3r.runelite.recommendedequipment;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.bank.BankSearch;

import javax.inject.Inject;

public class BankFilterButton {
    @Getter @Setter
    private boolean tabActive = false;
    private final Client client;
    private final BankSearch bankSearch;
    private Widget parent;
    private Widget backgroundWidget;
    private Widget iconWidget;

    @Inject
    public BankFilterButton(Client client, BankSearch bankSearch) {
        this.client = client;
        // Used for resetting the search
        this.bankSearch = bankSearch;
    }

    public void init() {
        if (this.isHidden()) {
            return;
        }

        this.parent = this.client.getWidget(ComponentID.BANK_CONTAINER);
        assert this.parent != null;
        int REC_BUTTON_SIZE = 25;
        int REC_BUTTON_X = 408;
        int REC_BUTTON_Y = 5;

        this.backgroundWidget = this.createGraphic(this.parent, "rec-gear", SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL, REC_BUTTON_SIZE, REC_BUTTON_SIZE, REC_BUTTON_X, REC_BUTTON_Y);
        this.backgroundWidget.setAction(1, "View tab ");
        this.backgroundWidget.setOnOpListener((JavaScriptCallback) this::handleTagTab);

        this.iconWidget = this.createGraphic(this.parent, "", SpriteID.QUESTS_PAGE_ICON_GREEN_ACHIEVEMENT_DIARIES, REC_BUTTON_SIZE - 6, REC_BUTTON_SIZE - 6, REC_BUTTON_X + 3, REC_BUTTON_Y + 3);

        if (tabActive) {
            tabActive = false;
            activateTab();
        }

        System.out.println("BankTabInterface initialized");
    }

    public void destroy() {
        if (this.tabActive) {
            this.closeTab();
            this.bankSearch.reset(true);
        }

        this.parent = null;

        if (this.iconWidget != null) {
            iconWidget.setHidden(true);
        }

        if (this.backgroundWidget != null) {
            this.backgroundWidget.setHidden(true);
        }

        this.tabActive = false;
    }

    private void activateTab() {
        if (this.tabActive) {
            return;
        }

        this.backgroundWidget.setSpriteId(SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL_SELECTED);
        this.backgroundWidget.revalidate();
        this.tabActive = true;

        this.bankSearch.reset(true); // clear search dialog & re-layout bank for new tab
    }

    private void handleTagTab(ScriptEvent scriptEvent) {
        if (scriptEvent.getOp() == 2) {
            this.client.setVarbit(Varbits.CURRENT_BANK_TAB, 0);

            if (this.tabActive) {
                this.closeTab();
                this.bankSearch.reset(true);
            } else {
                this.activateTab();
            }

            this.client.playSoundEffect(SoundEffectID.UI_BOOP);
        }
    }


    public void handleClick(MenuOptionClicked event) {
        if (isHidden())
        {
            return;
        }
        String menuOption = event.getMenuOption();

        // If click a base tab, close
        boolean clickedTabTag = menuOption.startsWith("View tab") && !event.getMenuTarget().equals("rec-gear");
        boolean clickedOtherTab = menuOption.equals("View all items") || menuOption.startsWith("View tag tab");
        if (this.tabActive && (clickedTabTag || clickedOtherTab))
        {
            closeTab();
        }
    }

    public void closeTab() {
        this.tabActive = false;
        if (this.backgroundWidget != null)
        {
            this.backgroundWidget.setSpriteId(SpriteID.UNKNOWN_BUTTON_SQUARE_SMALL);
            this.backgroundWidget.revalidate();
        }
    }

    public void refreshTab() {
        if (!this.tabActive) {
            return;
        }

        client.setVarbit(Varbits.CURRENT_BANK_TAB, 0);

        bankSearch.reset(true); // clear search dialog & relayout bank for new tab.
    }

    private Widget createGraphic(Widget container, String name, int sprite, int width, int height, int x, int y) {
        Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
        widget.setOriginalWidth(width);
        widget.setOriginalHeight(height);
        widget.setOriginalX(x);
        widget.setOriginalY(y);
        widget.setSpriteId(sprite);
        widget.setOnOpListener(ScriptID.NULL);
        widget.setHasListener(true);
        widget.setName(name);
        widget.revalidate();
        return widget;
    }

    public boolean isHidden() {
        Widget widget = this.client.getWidget(ComponentID.BANK_CONTAINER);
        return widget == null || widget.isHidden();
    }
}
