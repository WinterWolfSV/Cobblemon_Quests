package winterwolfsv.cobblemon_quests.neoforge;


import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import winterwolfsv.cobblemon_quests.neoforge.config.ConfigCommandsForge;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;

@Mod(MOD_ID)
public class Cobblemon_QuestsNeoForge {
    public Cobblemon_QuestsNeoForge() {
        NeoForge.EVENT_BUS.register(ConfigCommandsForge.class);
        CobblemonQuests.init(FMLPaths.CONFIGDIR.get().resolve(MOD_ID).resolve(MOD_ID+".config"),true);
    }
}