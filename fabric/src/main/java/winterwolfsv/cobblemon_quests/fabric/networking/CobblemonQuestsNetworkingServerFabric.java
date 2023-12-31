package winterwolfsv.cobblemon_quests.fabric.networking;

import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import winterwolfsv.cobblemon_quests.CobblemonQuests;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.config;

public class CobblemonQuestsNetworkingServerFabric {

    @Environment(net.fabricmc.api.EnvType.SERVER)
    public static void init() {
        ServerPlayConnectionEvents.JOIN.register(CobblemonQuestsNetworkingServerFabric::onServerPlayerJoin);
    }

    private static void onServerPlayerJoin(ServerPlayNetworkHandler handler, PacketSender packetSender, MinecraftServer server) {
        if (!config.getConfigBool("doVersionVerification")) return;
        CompletableFuture<String> joinMessageFuture = new CompletableFuture<>();

        Identifier joinMessageIdentifier = new Identifier("cobblemon_quests:join");
        ServerPlayNetworking.registerReceiver(handler, joinMessageIdentifier, (server1, player, handler1, buf, responseSender) -> {
            String bufferString = buf.readString();
            joinMessageFuture.complete(bufferString);
        });

        try {
            String bufferString = joinMessageFuture.get((long) config.getConfigFloat("versionVerificationTimeoutMillis"), TimeUnit.MILLISECONDS);

            if (bufferString.equals(CobblemonQuests.MOD_VERSION)) {
            } else {
                CobblemonQuests.LOGGER.log(Level.INFO, "Player " + handler.player.getName().getString() + " failed to join the server because the mod Cobblemon Quests is outdated. The player has version " + bufferString + " installed and the server requires version " + CobblemonQuests.MOD_VERSION + ".");


                Text disconnectText = Text.literal("This server requires Cobblemon Quests version §4§l" +
                        CobblemonQuests.MOD_VERSION + "§f to be installed. " + "Only the wrong version §4§l" +
                        bufferString + "§f is installed. " + "\n\n§nhttps://modrinth.com/mod/cobblemon-quests" +
                        "\n\nhttps://www.curseforge.com/minecraft/mc-mods/cobblemon-quests");
                DisconnectS2CPacket disconnectPacket = new DisconnectS2CPacket(disconnectText);
                handler.sendPacket(disconnectPacket);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            CobblemonQuests.LOGGER.log(Level.INFO, "Player " + handler.player.getName().getString() + " failed to join the server because the mod Cobblemon Quests is missing or outdated.");
            DisconnectS2CPacket disconnectPacket = new DisconnectS2CPacket(Text.of("This server requires Cobblemon Quests version §4§l" + CobblemonQuests.MOD_VERSION + "§f to be installed.\n\n§nhttps://modrinth.com/mod/cobblemon-quests\n\nhttps://www.curseforge.com/minecraft/mc-mods/cobblemon-quests"));
            handler.sendPacket(disconnectPacket);
        } finally {
            ServerPlayNetworking.unregisterReceiver(handler, joinMessageIdentifier);
        }
    }
}
