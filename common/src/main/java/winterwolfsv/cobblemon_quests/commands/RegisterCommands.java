package winterwolfsv.cobblemon_quests.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class RegisterCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        ConfigCommand.register(dispatcher);
    }
}
