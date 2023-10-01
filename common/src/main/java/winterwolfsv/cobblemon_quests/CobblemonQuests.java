package winterwolfsv.cobblemon_quests;

import winterwolfsv.cobblemon_quests.events.FTBCobblemonEventHandler;
import winterwolfsv.cobblemon_quests.tasks.PokemonTaskTypes;

public class CobblemonQuests
{
	public static final String MOD_ID = "cobblemon_quests";

	public static void init() {
		new FTBCobblemonEventHandler().init();
		PokemonTaskTypes.init();
	}
}
