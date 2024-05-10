package com.adamk33n3r.runelite.recommendedequipment;

import com.adamk33n3r.runelite.recommendedequipment.banktab.BankTab;

import com.google.inject.Binder;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import com.google.inject.name.Names;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.IndexedSprite;
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
	name = "Recommended Equipment"
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
	private ConfigManager configManager;

	@Inject
	private RecommendedEquipmentConfig config;
	@Inject
	@Getter
	private BankTab bankTab;

	@Getter
	private ActivityEquipmentStyle activityEquipmentStyle;

	@Override
	public void configure(Binder binder) {
		Properties properties = RecommendedEquipmentProperties.getProperties();
		Names.bindProperties(binder, properties);
//		binder.bind(WatchdogMuxer.class).toProvider(() -> this.panel.getMuxer());
	}

	@Override
	protected void startUp() throws Exception {
		this.eventBus.register(this.bankTab);
		this.bankTab.startUp();
		PanelWrapper panel = Util.runWithLAF(() -> this.injector.getInstance(PanelWrapper.class));
		NavigationButton navButton = NavigationButton.builder()
			.tooltip("Recommended Equipment")
			.icon((BufferedImage) Icons.ICON.getImage())
			.priority(1)
			.panel(panel)
			.build();
		this.clientToolbar.addNavigation(navButton);

		this.client.getSpriteOverrides().put(ICON_SPRITE_ID, ImageUtil.getImageSpritePixels(Icons.ICON_IMG, this.client));
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.eventBus.unregister(this.bankTab);
		this.bankTab.shutDown();
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
