package dev.lemonnik.melodeon;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.io.File;

public class SoundRegisterer {
    public static void registerSounds() {
        File currentRunDir = new File(System.getProperty("user.dir"));
        File soundsDir = new File(currentRunDir, "resourcepacks/Melodeon/assets/melodeon/sounds/");

        if (!soundsDir.exists()) {
            if (soundsDir.mkdirs()) {
                Melodeon.LOGGER.info("Created sounds directory: " + soundsDir.getAbsolutePath());
            } else {
                Melodeon.LOGGER.error("Failed to create sounds directory: " + soundsDir.getAbsolutePath());
                return;
            }
        }

        File[] soundFiles = soundsDir.listFiles();
        if (soundFiles == null || soundFiles.length == 0) {
            Melodeon.LOGGER.warn("No sound files found in: " + soundsDir.getAbsolutePath());
            return;
        }

        for (File soundFile : soundFiles) {
            if (soundFile.isFile() && (soundFile.getName().endsWith(".ogg"))) {
                String soundName = soundFile.getName().substring(0, soundFile.getName().lastIndexOf('.'));
                registerSoundEvent(soundName);
            }
        }

        Melodeon.LOGGER.info("Registered all sounds from " + soundsDir.getAbsolutePath());
    }

    private static void registerSoundEvent(String name) {
        Identifier id = new Identifier(Melodeon.MOD_ID, name);
        SoundEvent soundEvent = SoundEvent.of(id);
        Registry.register(Registries.SOUND_EVENT, id, soundEvent);

        Melodeon.LOGGER.info("Registered sound: " + id);
    }
}
