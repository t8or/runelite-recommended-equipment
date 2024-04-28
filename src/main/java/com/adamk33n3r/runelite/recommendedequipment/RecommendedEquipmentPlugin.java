package com.adamk33n3r.runelite.recommendedequipment;

import com.google.inject.Binder;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import com.google.inject.name.Names;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.devtools.DevToolsPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.Properties;

@Slf4j
@PluginDescriptor(
	name = "Recommended Equipment"
)
public class RecommendedEquipmentPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private EventBus eventBus;
	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private RecommendedEquipmentConfig config;
	@Inject
	private BankTab bankTab;

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
		// We inject it here so that it gets the right LAF
//		RecommendedEquipmentPanel panel = Util.runWithLAF(() -> this.injector.getInstance(RecommendedEquipmentPanel.class));
		PanelWrapper panel = Util.runWithLAF(() -> this.injector.getInstance(PanelWrapper.class));
		NavigationButton navButton = NavigationButton.builder()
			.tooltip("Recommended Equipment")
			.icon((BufferedImage) Icons.ICON.getImage())
			.priority(1)
			.panel(panel)
			.build();
		this.clientToolbar.addNavigation(navButton);
		// TODO: Remove before release
		SwingUtilities.invokeLater(() -> {
			this.clientToolbar.openPanel(navButton);
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.eventBus.unregister(this.bankTab);
		this.bankTab.shutDown();
	}

	@Provides
	RecommendedEquipmentConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RecommendedEquipmentConfig.class);
	}
}
