package winterwolfsv.cobblemon_quests.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import winterwolfsv.cobblemon_quests.fabric.networking.CobblemonQuestsNetworkingServer;


public class CobblemonQuestsServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        CobblemonQuestsNetworkingServer.init();

    }
}
