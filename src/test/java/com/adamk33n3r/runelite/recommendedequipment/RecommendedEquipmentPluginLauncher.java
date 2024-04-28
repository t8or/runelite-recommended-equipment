package com.adamk33n3r.runelite.recommendedequipment;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RecommendedEquipmentPluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RecommendedEquipmentPlugin.class);
		RuneLite.main(args);
	}
}