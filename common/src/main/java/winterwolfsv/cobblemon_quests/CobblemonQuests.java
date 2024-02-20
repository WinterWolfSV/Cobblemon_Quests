package winterwolfsv.cobblemon_quests;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import winterwolfsv.cobblemon_quests.events.FTBCobblemonEventHandler;
import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;
import winterwolfsv.cobblemon_quests.tasks.PokemonTaskTypes;

import java.nio.file.Path;
import java.util.logging.Logger;

public class CobblemonQuests {
    public static final String MOD_ID = "cobblemon_quests";
    public static final String MOD_VERSION = "1.1.8";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);
    public static Path configPath;
    public static FTBCobblemonEventHandler eventHandler;

    public static void init(Path configPath, boolean useConfig) {
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "poke_ball_icon"), new Item(new Item.Settings()));
        if (useConfig) {
            CobblemonQuests.configPath = configPath;
            CobblemonQuestsConfig.init();
        }
        eventHandler = new FTBCobblemonEventHandler().init();
        PokemonTaskTypes.init();
    }
}
