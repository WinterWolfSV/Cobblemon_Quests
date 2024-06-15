package winterwolfsv.cobblemon_quests;

import dev.architectury.platform.Platform;
import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;
import winterwolfsv.cobblemon_quests.events.Cobblemon1_5EventHandler;
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

        //TODO Implement a better way to ensure that the minimum version of cobblemon for this feature is 1.5.1
        String cobblemonVersion = Platform.getMod("cobblemon").getVersion();
        if((cobblemonVersion.startsWith("1.5") || cobblemonVersion.startsWith("1.6") || cobblemonVersion.startsWith("1.7"))&&!cobblemonVersion.startsWith("1.5.0")){
            new Cobblemon1_5EventHandler().init();
        }
    }
}
