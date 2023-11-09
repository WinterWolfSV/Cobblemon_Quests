package winterwolfsv.cobblemon_quests.fabric.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import winterwolfsv.cobblemon_quests.CobblemonQuests;

public class CobblemonQuestsNetworkingClient {
    @Environment(net.fabricmc.api.EnvType.CLIENT)
    public static void init() {
        ClientPlayConnectionEvents.JOIN.register(CobblemonQuestsNetworkingClient::onClientPlayerJoin);
    }

    private static void onClientPlayerJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient minecraftClient) {
        System.out.println("Client player joined");
        ClientPlayNetworking.send(new Identifier("cobblemon_quests:join"), new PacketByteBuf(Unpooled.buffer()).writeString(CobblemonQuests.MOD_VERSION));
    }

}
