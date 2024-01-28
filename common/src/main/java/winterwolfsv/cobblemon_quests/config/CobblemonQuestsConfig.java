package winterwolfsv.cobblemon_quests.config;

import winterwolfsv.cobblemon_quests.CobblemonQuests;

import java.util.ArrayList;
import java.util.List;

public class CobblemonQuestsConfig implements DefaultConfig {
    // All _only final_ fields will be added to the config file. These fields must be primitive types or Strings.
    final boolean doVersionVerification = false;
    final int versionVerificationTimeoutMillis = 1000;
    final String configVersion = CobblemonQuests.MOD_VERSION;
    final List<String> blackListedPokemon = new ArrayList<>();
    // TODO Fix the error when using a string in the config
}
