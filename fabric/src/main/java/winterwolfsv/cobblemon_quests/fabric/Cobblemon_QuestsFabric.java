package winterwolfsv.cobblemon_quests.fabric;

import winterwolfsv.cobblemon_quests.CobblemonQuests;
import net.fabricmc.api.ModInitializer;
import winterwolfsv.cobblemon_quests.config.Config;
import winterwolfsv.cobblemon_quests.fabric.config.ConfigCommands;

public class Cobblemon_QuestsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobblemonQuests.init();
        ConfigCommands.registerCommands();

    }
}