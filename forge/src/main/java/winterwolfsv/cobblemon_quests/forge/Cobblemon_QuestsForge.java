package winterwolfsv.cobblemon_quests.forge;

import dev.architectury.platform.forge.EventBuses;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CobblemonQuests.MOD_ID)
public class Cobblemon_QuestsForge {
    public Cobblemon_QuestsForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(CobblemonQuests.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CobblemonQuests.init();
    }
}