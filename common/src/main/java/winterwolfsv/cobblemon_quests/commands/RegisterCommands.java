package winterwolfsv.cobblemon_quests.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class RegisterCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("cobblemonquests")
                        .requires(source -> source.hasPermission(2))
                        .then(SuppressWarningsCommand.register())
                        .then(BlacklistPokemonCommand.register())
                        .then(GivePokemonCommand.register())
        );
    }
}