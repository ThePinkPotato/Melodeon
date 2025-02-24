package dev.lemonnik.melodeon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MelodeonClient implements ClientModInitializer {
    public static final String MOD_ID = "melodeon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final int CHECK_INTERVAL = 0;
    private int tickCounter = 0;
    private Identifier lastBiomeId = null;
    private SoundInstance currentAmbientSound;

    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register(Commands::register);
        ClientTickEvents.END_CLIENT_TICK.register(this::handleBiomeMusic);
    }

    private void handleBiomeMusic(MinecraftClient client) {
        if (client.world == null || client.player == null) return;

        if (tickCounter++ >= CHECK_INTERVAL) {
            tickCounter = 0;
            RegistryEntry<Biome> biomeEntry = client.world.getBiome(client.player.getBlockPos());
            Identifier currentBiomeId = biomeEntry.getKey().map(RegistryKey::getValue).orElse(null);

            if (currentBiomeId != null && !currentBiomeId.equals(lastBiomeId)) {
                handleBiomeChange(client, currentBiomeId);
            }
        }
    }

    private void handleBiomeChange(MinecraftClient client, Identifier newBiomeId) {
        if (lastBiomeId != null) {
            playLeaveSound(client, lastBiomeId);
            stopCurrentAmbient(client);
        }

        playEnterSound(client, newBiomeId);
        playAmbientSound(client, newBiomeId);

        lastBiomeId = newBiomeId;
    }

    private void playLeaveSound(MinecraftClient client, Identifier biomeId) {
        Identifier soundId = new Identifier(MOD_ID, "leave." + biomeId.getNamespace() + "." + biomeId.getPath());
        SoundEvent soundEvent = getSoundEvent(client, soundId);
        if (soundEvent != null) {
            Vec3d pos = client.player.getPos();
            SoundInstance sound = new PositionedSoundInstance(
                    soundEvent.getId(),
                    SoundCategory.AMBIENT,
                    1.0F,
                    1.0F,
                    SoundInstance.createRandom(),
                    false,
                    0,
                    SoundInstance.AttenuationType.LINEAR,
                    pos.x,
                    pos.y,
                    pos.z,
                    false
            );
            client.getSoundManager().play(sound);
        }
    }

    private void playEnterSound(MinecraftClient client, Identifier biomeId) {
        Identifier soundId = new Identifier(MOD_ID, "enter." + biomeId.getNamespace() + "." + biomeId.getPath());
        SoundEvent soundEvent = getSoundEvent(client, soundId);
        if (soundEvent != null) {
            Vec3d pos = client.player.getPos();
            SoundInstance sound = new PositionedSoundInstance(
                    soundEvent.getId(),
                    SoundCategory.AMBIENT,
                    1.0F,
                    1.0F,
                    SoundInstance.createRandom(),
                    false,
                    0,
                    SoundInstance.AttenuationType.LINEAR,
                    pos.x,
                    pos.y,
                    pos.z,
                    false
            );
            client.getSoundManager().play(sound);
        }
    }

    private void playAmbientSound(MinecraftClient client, Identifier biomeId) {
        Identifier soundId = new Identifier(MOD_ID, "ambient." + biomeId.getNamespace() + "." + biomeId.getPath());
        SoundEvent soundEvent = getSoundEvent(client, soundId);
        if (soundEvent != null) {
            currentAmbientSound = new PositionedSoundInstance(
                    soundEvent.getId(),
                    SoundCategory.AMBIENT,
                    1.0F,
                    1.0F,
                    SoundInstance.createRandom(),
                    true,
                    0,
                    SoundInstance.AttenuationType.NONE,
                    0.0,
                    0.0,
                    0.0,
                    true
            );
            client.getSoundManager().play(currentAmbientSound);
        }
    }

    private SoundEvent getSoundEvent(MinecraftClient client, Identifier soundId) {
        return client.world.getRegistryManager()
                .get(RegistryKeys.SOUND_EVENT)
                .get(soundId);
    }

    private void stopCurrentAmbient(MinecraftClient client) {
        if (currentAmbientSound != null) {
            client.getSoundManager().stop(currentAmbientSound);
            currentAmbientSound = null;
        }
    }
}