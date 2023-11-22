package winterwolfsv.cobblemon_quests.fabric.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import winterwolfsv.cobblemon_quests.config.ConfigCommands;
import winterwolfsv.cobblemon_quests.config.DefaultConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;
import static winterwolfsv.cobblemon_quests.CobblemonQuests.config;

public class ConfigCommandsFabric {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                ConfigCommands.registerCommands(dispatcher));
    }
}

