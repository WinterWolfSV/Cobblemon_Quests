package winterwolfsv.cobblemon_quests.fabric.config;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import winterwolfsv.cobblemon_quests.config.ConfigCommands;

public class ConfigCommandsFabric {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                ConfigCommands.registerCommands(dispatcher));
//        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
//                CustomGivePokemonCommand.INSTANCE.register(dispatcher));
//    }
    }
}

