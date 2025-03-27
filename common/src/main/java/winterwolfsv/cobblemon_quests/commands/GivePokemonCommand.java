package winterwolfsv.cobblemon_quests.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import winterwolfsv.cobblemon_quests.commands.arguments.types.ActionListArgumentType;

import java.util.List;

public class GivePokemonCommand {
    public static CommandNode<CommandSourceStack> register() {
        return Commands.literal("givepokemon")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("should_give", BoolArgumentType.bool())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .then(Commands.argument("action", ActionListArgumentType.actionList())
                                                .then(Commands.argument("properties", PokemonPropertiesArgumentType.Companion.properties())
                                                        .executes(GivePokemonCommand::execute))))))
                .build();
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<String> actionList = ActionListArgumentType.getActionList(context, "action");
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        boolean givePokemon = BoolArgumentType.getBool(context, "should_give");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PokemonProperties pokemonProperties = PokemonPropertiesArgumentType.Companion.getPokemonProperties(context, "properties");
        Pokemon pokemon = pokemonProperties.create();
        for (String action : actionList) {
            CobblemonQuests.eventHandler.processTasksForTeam(pokemon, action, amount, player);
        }
        if (givePokemon) {
            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            party.add(pokemon);
        }
        context.getSource().sendSuccess(() -> Component.literal("Successfully ran command givepokemon for player " + player.getName().getString() + " with actions " + actionList + " for pokemon " + pokemon.getSpecies().getName()), true);
        return 1;
    }
}