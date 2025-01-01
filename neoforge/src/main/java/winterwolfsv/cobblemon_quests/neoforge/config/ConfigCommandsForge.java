package winterwolfsv.cobblemon_quests.neoforge.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import winterwolfsv.cobblemon_quests.commands.RegisterCommands;

public class ConfigCommandsForge {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        RegisterCommands.register(event.getDispatcher());
    }
}