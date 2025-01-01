package winterwolfsv.cobblemon_quests.fabric.config;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import winterwolfsv.cobblemon_quests.commands.RegisterCommands;

public class ConfigCommandsFabric {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                RegisterCommands.register(dispatcher));
    }
}

