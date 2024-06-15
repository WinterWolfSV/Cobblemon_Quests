package winterwolfsv.cobblemon_quests;

import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;
import winterwolfsv.cobblemon_quests.events.FTBCobblemonEventHandler;
import winterwolfsv.cobblemon_quests.logger.CobblemonQuestsLogger;
import winterwolfsv.cobblemon_quests.tasks.PokemonTaskTypes;

import java.nio.file.Path;

public class CobblemonQuests {
    public static final String MOD_ID = "cobblemon_quests";
    public static final CobblemonQuestsLogger LOGGER = new CobblemonQuestsLogger();
    public static Path configPath;
    public static FTBCobblemonEventHandler eventHandler;

    public static void init(Path configPath, boolean useConfig) {
        if (useConfig) {
            CobblemonQuests.configPath = configPath;
            CobblemonQuestsConfig.init();
        }
        eventHandler = new FTBCobblemonEventHandler().init();
        PokemonTaskTypes.init();
    }
}
