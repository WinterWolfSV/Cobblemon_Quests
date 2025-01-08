package winterwolfsv.cobblemon_quests;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;
import winterwolfsv.cobblemon_quests.commands.arguments.types.ActionListArgumentType;
import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;
import winterwolfsv.cobblemon_quests.events.CobblemonQuestsEventHandler;
import winterwolfsv.cobblemon_quests.logger.CobblemonQuestsLogger;
import winterwolfsv.cobblemon_quests.tasks.PokemonTaskTypes;

import java.nio.file.Path;

public class CobblemonQuests {
    public static final String MOD_ID = "cobblemon_quests";
    public static final CobblemonQuestsLogger LOGGER = new CobblemonQuestsLogger();
    public static Path configPath;
    public static CobblemonQuestsEventHandler eventHandler;

    public static void init(Path configPath, boolean useConfig) {
        if (useConfig) {
            CobblemonQuests.configPath = configPath;
            CobblemonQuestsConfig.init();
        }
        eventHandler = new CobblemonQuestsEventHandler().init();
        PokemonTaskTypes.init();

        ArgumentTypeRegistry.registerArgumentType(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "argument_list"),
                ActionListArgumentType.class,
                SingletonArgumentInfo.contextFree(ActionListArgumentType::actionList));
    }
}
