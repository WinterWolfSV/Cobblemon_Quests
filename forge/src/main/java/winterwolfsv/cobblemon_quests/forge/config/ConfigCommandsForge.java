package winterwolfsv.cobblemon_quests.forge.config;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import winterwolfsv.cobblemon_quests.config.ConfigCommands;

@Mod.EventBusSubscriber(modid = CobblemonQuests.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ConfigCommandsForge {
    public ConfigCommandsForge() {
    }
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ConfigCommands.registerCommands(event.getDispatcher());
    }
}
