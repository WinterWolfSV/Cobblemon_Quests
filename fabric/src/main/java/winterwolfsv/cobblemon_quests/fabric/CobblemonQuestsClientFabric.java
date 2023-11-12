package winterwolfsv.cobblemon_quests.fabric;

import net.fabricmc.api.ClientModInitializer;
import winterwolfsv.cobblemon_quests.fabric.networking.CobblemonQuestsNetworkingClientFabric;


public class CobblemonQuestsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CobblemonQuestsNetworkingClientFabric.init();

    }
}
