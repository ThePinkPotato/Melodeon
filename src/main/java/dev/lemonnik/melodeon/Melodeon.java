package dev.lemonnik.melodeon;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Melodeon implements ModInitializer {
	public static final String MOD_ID = "melodeon";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Starting Melodeon!");
		ResourcePackGenerator.generateResourcePack();
		SoundRegisterer.registerSounds();
	}
}