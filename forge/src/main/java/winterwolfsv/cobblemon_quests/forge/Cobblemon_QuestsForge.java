package winterwolfsv.cobblemon_quests.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import winterwolfsv.cobblemon_quests.CobblemonQuests;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;


@Mod(MOD_ID)
public class Cobblemon_QuestsForge {

    public Cobblemon_QuestsForge() {
        EventBuses.registerModEventBus(MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CobblemonQuests.init(FMLPaths.CONFIGDIR.get().resolve(MOD_ID).resolve(MOD_ID + "_config.json"), true);
    }
}