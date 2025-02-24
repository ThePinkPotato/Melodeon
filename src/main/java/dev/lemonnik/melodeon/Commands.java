package dev.lemonnik.melodeon;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(CommandManager.literal("melodeon-reload")
            .executes(context -> {
                ResourcePackGenerator.generateResourcePack();
                SoundRegisterer.registerSounds();
                return Command.SINGLE_SUCCESS;
            })
        );
    }
}
