package winterwolfsv.cobblemon_quests.fabric;

import net.fabricmc.api.ClientModInitializer;
import winterwolfsv.cobblemon_quests.fabric.networking.CobblemonQuestsNetworkingClient;


public class CobblemonQuestsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CobblemonQuestsNetworkingClient.init();

    }
}
