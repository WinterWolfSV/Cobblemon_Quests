package winterwolfsv.cobblemon_quests.fabric;

import net.fabricmc.loader.api.FabricLoader;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import net.fabricmc.api.ModInitializer;
import winterwolfsv.cobblemon_quests.fabric.config.ConfigCommandsFabric;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;

public class Cobblemon_QuestsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobblemonQuests.init(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve(MOD_ID + "_config.yml"),true);
        ConfigCommandsFabric.registerCommands();
    }
}