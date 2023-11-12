package winterwolfsv.cobblemon_quests.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import winterwolfsv.cobblemon_quests.fabric.networking.CobblemonQuestsNetworkingServerFabric;


public class CobblemonQuestsServerFabric implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        CobblemonQuestsNetworkingServerFabric.init();

    }
}
