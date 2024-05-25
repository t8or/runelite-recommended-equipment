package com.adamk33n3r.runelite.recommendedequipment;

import com.adamk33n3r.runelite.recommendedequipment.banktab.BankTab;

import com.google.inject.Binder;
import com.google.inject.Provides;
import javax.inject.Inject;

import com.google.inject.name.Names;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.*;

@Slf4j
@PluginDescriptor(
	name = "Recommended Equipment",
	description = "Recommended Equipment is a plugin that will suggest the best gear for the boss you are fighting or activity you are participating in.",
	tags = { "equipment", "gear", "boss", "pvm", "pvp", "activity" }
)
public class RecommendedEquipmentPlugin extends Plugin {
	public static final int ICON_SPRITE_ID = 0x74386F72;

	@Inject
	private Client client;
	@Inject
	private EventBus eventBus;
	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	@Getter
	private BankTab bankTab;

	@Getter
	private ActivityEquipmentStyle activityEquipmentStyle;

	private NavigationButton navButton;

	@Override
	public void configure(Binder binder) {
		Properties properties = RecommendedEquipmentProperties.getProperties();
		Names.bindProperties(binder, properties);
	}

	@Override
	protected void startUp() throws Exception {
		this.eventBus.register(this.bankTab);
		this.bankTab.startUp();
		PanelWrapper panel = this.injector.getInstance(PanelWrapper.class);
		this.navButton = NavigationButton.builder()
			.tooltip("Recommended Equipment")
			.icon((BufferedImage) Icons.ICON.getImage())
			.priority(5)
			.panel(panel)
			.build();
		this.clientToolbar.addNavigation(this.navButton);

		this.client.getSpriteOverrides().put(ICON_SPRITE_ID, ImageUtil.getImageSpritePixels(Icons.ICON_IMG, this.client));
	}

	@Override
	protected void shutDown() throws Exception {
		this.eventBus.unregister(this.bankTab);
		this.bankTab.shutDown();
		this.clientToolbar.removeNavigation(this.navButton);
		this.client.getSpriteOverrides().remove(ICON_SPRITE_ID);
	}

	public void setActivityEquipmentStyle(ActivityEquipmentStyle activityEquipmentStyle) {
		this.activityEquipmentStyle = activityEquipmentStyle;

		if (this.activityEquipmentStyle != null) {
			if (!this.bankTab.isInitialized()) {
				this.bankTab.startUp();
			}
			if (this.bankTab.isActive()) {
				this.bankTab.resetTab();
			}
		} else {
			this.bankTab.shutDown();
		}
	}

	@Provides
	RecommendedEquipmentConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RecommendedEquipmentConfig.class);
	}
}
