package winterwolfsv.cobblemon_quests.forge.networking;

import dev.architectury.event.events.client.ClientPlayerEvent;
import io.netty.channel.ChannelHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import winterwolfsv.cobblemon_quests.forge.networking.CobblemonQuestsNetworkingServerForge;


public class CobblemonQuestsNetworkingClientForge {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new Identifier(CobblemonQuests.MOD_ID, "main"),
            () -> "1.0",
            s -> true,
            s -> true
    );

    @SubscribeEvent
    public void onClientPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        System.out.println("Logging in client player" + event.getPlayer().getUuidAsString());
    }


}
