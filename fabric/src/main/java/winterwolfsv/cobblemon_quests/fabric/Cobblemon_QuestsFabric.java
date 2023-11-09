package winterwolfsv.cobblemon_quests.fabric;

import winterwolfsv.cobblemon_quests.CobblemonQuests;
import net.fabricmc.api.ModInitializer;

public class Cobblemon_QuestsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobblemonQuests.init();

    }
}