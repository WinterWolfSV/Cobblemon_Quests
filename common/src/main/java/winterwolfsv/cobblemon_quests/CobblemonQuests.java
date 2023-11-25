package winterwolfsv.cobblemon_quests;

import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;
import winterwolfsv.cobblemon_quests.config.Config;
import winterwolfsv.cobblemon_quests.events.FTBCobblemonEventHandler;
import winterwolfsv.cobblemon_quests.tasks.PokemonTaskTypes;

import java.nio.file.Path;
import java.util.logging.Logger;

public class CobblemonQuests
{
	public static final String MOD_ID = "cobblemon_quests";
	public static final String MOD_VERSION = "1.1.6";
	public static final Logger LOGGER = Logger.getLogger(MOD_ID);
	public static Path configPath;
	public static Config config;

	public static void init(Path configPath, boolean useConfig) {
		if (useConfig) {
			CobblemonQuests.configPath = configPath;
			CobblemonQuests.config = new Config(new CobblemonQuestsConfig());
		}
		new FTBCobblemonEventHandler().init();
		PokemonTaskTypes.init();
	}
}
