package com.mmyzd.llor;

import java.io.File;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigManager {
	
	public Configuration file;
	
	public Property useSkyLight;
	public Property overlayType;
	public Property chunkRadius;
	public Property pollingInterval;
	
	public ConfigManager(File configDir) {
		file = new Configuration(new File(configDir, "LLOverlayReloaded.cfg"));
		reload();
	}
	
	@SubscribeEvent
	public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(LightLevelOverlayReloaded.MODID)) update();
	}
	
	void reload() {
		file.load();
		useSkyLight = file.get("general", "useSkyLight", false);
		useSkyLight.setComment("If set to true, the sunlight/moonlight will be counted in light level. (default: false)");
		useSkyLight.set(useSkyLight.getBoolean(false));
		overlayType = file.get("general", "overlayType", 0);
		overlayType.setComment("0 - normal, 1 - only shows dangerous (red) area, 2 - only shows safe (green) area. (default: 0)");
		overlayType.set(Math.min(Math.max(overlayType.getInt(0), 0), 2));
		chunkRadius = file.get("general", "chunkRadius", 3);
		chunkRadius.setComment("The distance (in chunks) of rendering radius. (default: 3)");
		chunkRadius.set(Math.min(Math.max(chunkRadius.getInt(3), 1), 9));
		pollingInterval = file.get("general", "pollingInterval", 200);
		pollingInterval.setComment("The update interval (in milliseconds) of light level. Farther chunks update less frequently. (default: 200)");
		pollingInterval.set(Math.min(Math.max(pollingInterval.getInt(200), 10), 60000));
		file.save();
	}
	
	public void update() {
		file.save();
	}
	
}
