package winterwolfsv.cobblemon_quests.logger;

import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;

public class CobblemonQuestsLogger {

    private static final Logger logger = Logger.getLogger(MOD_ID);

    public void log(Level level, String message) {
        logger.log(level, message);
    }
    public void info(String message) {
        logger.info(message);
    }

    public void warning(String message) {
        if (!CobblemonQuestsConfig.suppressWarnings) {
            logger.warning(message);
        }
    }
}
