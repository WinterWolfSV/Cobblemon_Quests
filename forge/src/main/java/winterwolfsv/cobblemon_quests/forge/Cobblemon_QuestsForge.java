package winterwolfsv.cobblemon_quests.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import winterwolfsv.cobblemon_quests.config.ConfigCommands;
import winterwolfsv.cobblemon_quests.forge.config.ConfigCommandsForge;
import winterwolfsv.cobblemon_quests.forge.networking.Testing;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;

@Mod(CobblemonQuests.MOD_ID)
public class Cobblemon_QuestsForge {
    public Cobblemon_QuestsForge() {
        EventBuses.registerModEventBus(CobblemonQuests.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CobblemonQuests.init(FMLPaths.CONFIGDIR.get().resolve(MOD_ID).resolve(MOD_ID + "_config.json"));

        if (FMLEnvironment.dist.isClient()) {
            Testing.init();
        }
    }

}