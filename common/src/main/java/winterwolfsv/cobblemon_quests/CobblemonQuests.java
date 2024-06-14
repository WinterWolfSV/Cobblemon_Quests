package winterwolfsv.cobblemon_quests;

import dev.architectury.platform.Platform;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;
import winterwolfsv.cobblemon_quests.events.Cobblemon1_5EventHandler;
import winterwolfsv.cobblemon_quests.events.FTBCobblemonEventHandler;
import winterwolfsv.cobblemon_quests.logger.CobblemonQuestsLogger;
import winterwolfsv.cobblemon_quests.tasks.PokemonTaskTypes;

import java.nio.file.Path;
import java.util.Arrays;

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
        try {
            Version cobblemonVersion = SemanticVersion.parse(Platform.getMod("cobblemon").getVersion().split("[+]")[0]);
            Version minVersion = SemanticVersion.parse("1.5.1");
            if(cobblemonVersion.compareTo(minVersion) >= 0){
                LOGGER.info("Initializing Cobblemon 1.5 event handler");
                new Cobblemon1_5EventHandler().init();
            }
        } catch (VersionParsingException e) {
            LOGGER.warning(Arrays.toString(e.getStackTrace()));
        }
    }
}
