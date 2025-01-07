package dev.lemonnik.melodeon;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundRegisterer{
    public static final SoundEvent ENTER_PLAINS = registerSoundEvent("enter.biome.plains");
    public static final SoundEvent AMBIENT_PLAINS = registerSoundEvent("ambient.biome.plains");
    public static final SoundEvent LEAVE_PLAINS = registerSoundEvent("leave.biome.plains");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(Melodeon.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        Melodeon.LOGGER.info("Registering Sounds for " + Melodeon.MOD_ID);
    }
}
