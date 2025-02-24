package dev.lemonnik.melodeon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.client.sound.PositionedSoundInstance.record;

public class MelodeonClient implements ClientModInitializer {
    public static final String MOD_ID = "melodeon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private RegistryKey<Biome> currentBiomeKey;

    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register(Commands::register);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.player != null) {
                RegistryEntry<Biome> biomeEntry = client.world.getBiome(client.player.getBlockPos());
                RegistryKey<Biome> newBiomeKey = biomeEntry.getKey().orElse(null);
                String biomeModName = newBiomeKey.getValue().getNamespace();

                if (!newBiomeKey.equals(currentBiomeKey)) {
                    if (currentBiomeKey != null) {
                        playBiomeSound("leave", currentBiomeKey, biomeModName);
                    }
                    playBiomeSound("enter", newBiomeKey, biomeModName);
                    currentBiomeKey = newBiomeKey;
                } else {
                    playBiomeSound("ambient", newBiomeKey, biomeModName);
                }
            }
        });
    }

    private void playBiomeSound(String type, RegistryKey<Biome> biomeKey, String modName) {
        String biomeName = biomeKey.getValue().getPath();
        Identifier soundId = new Identifier(Melodeon.MOD_ID, type + "." + modName + "." + biomeName);
        SoundEvent soundEvent = MinecraftClient.getInstance().world.getRegistryManager()
                .get(RegistryKeys.SOUND_EVENT).get(soundId);

        if (soundEvent != null) {
            Vec3d playerPos = MinecraftClient.getInstance().player.getPos();
            MinecraftClient.getInstance().getSoundManager().play(record(
                    soundEvent, playerPos
            ));
        }
    }
}
