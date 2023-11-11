package winterwolfsv.cobblemon_quests.config;

public class CobblemonQuestsConfig implements DefaultConfig {
    // All _only final_ fields will be added to the config file. These fields must be primitive types or Strings.
    final boolean doVersionVerification = true;
    final int versionVerificationTimeoutMillis = 1000;
    final String testString = "Hello World!";
}
