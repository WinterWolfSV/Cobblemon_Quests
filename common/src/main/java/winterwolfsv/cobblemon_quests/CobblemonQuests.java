package winterwolfsv.cobblemon_quests;

import winterwolfsv.cobblemon_quests.events.FTBCobblemonEventHandler;
import winterwolfsv.cobblemon_quests.tasks.PokemonTaskTypes;

import java.util.logging.Logger;

public class CobblemonQuests
{
	public static final String MOD_ID = "cobblemon_quests";
	public static final String MOD_VERSION = "1.1.6";
	public static final Logger LOGGER = Logger.getLogger(MOD_ID);

	public static void init() {
		new FTBCobblemonEventHandler().init();
		PokemonTaskTypes.init();
	}
}
