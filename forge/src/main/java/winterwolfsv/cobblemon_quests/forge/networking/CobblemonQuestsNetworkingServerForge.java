package winterwolfsv.cobblemon_quests.forge.networking;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.extensions.IForgePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import winterwolfsv.cobblemon_quests.CobblemonQuests;

@Mod.EventBusSubscriber(modid = CobblemonQuests.MOD_ID)
public class CobblemonQuestsNetworkingServerForge {

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getEntity();
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;


    }
}
