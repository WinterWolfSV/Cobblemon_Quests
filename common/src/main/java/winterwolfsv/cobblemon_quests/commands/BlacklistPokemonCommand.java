package winterwolfsv.cobblemon_quests.commands;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import winterwolfsv.cobblemon_quests.commands.suggestions.ListSuggestionProvider;
import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;

import java.util.List;

public class BlacklistPokemonCommand {
    public static CommandNode<CommandSourceStack> register() {
        return Commands.literal("blacklisted_pokemon").then(Commands.argument("action", StringArgumentType.string()).suggests(new ListSuggestionProvider(List.of("add", "remove"))).then(Commands.argument("pokemon", StringArgumentType.string()).suggests((context, builder) -> {
            if (StringArgumentType.getString(context, "action").equals("add")) {
                return new ListSuggestionProvider(PokemonSpecies.INSTANCE.getSpecies().stream().map(Species::getName).toList()).getSuggestions(context, builder);
            } else if (StringArgumentType.getString(context, "action").equals("remove")) {
                return new ListSuggestionProvider(CobblemonQuestsConfig.ignoredPokemon).getSuggestions(context, builder);
            }
            return builder.buildFuture();
        }).executes(context -> {
            String action = StringArgumentType.getString(context, "action");
            String pokemon = StringArgumentType.getString(context, "pokemon").toLowerCase();
            if (action.equals("add")) {
                if (CobblemonQuestsConfig.ignoredPokemon.contains(pokemon)) {
                    context.getSource().sendSystemMessage(Component.literal("Pokémon " + pokemon + " is already blacklisted."));
                    return 0;
                }
                CobblemonQuestsConfig.ignoredPokemon.add(pokemon);
                CobblemonQuestsConfig.save();
                context.getSource().sendSystemMessage(Component.literal("Pokémon " + pokemon + " has been blacklisted."));
            } else if (action.equals("remove")) {
                if (!CobblemonQuestsConfig.ignoredPokemon.contains(pokemon)) {
                    context.getSource().sendSystemMessage(Component.literal("Pokémon " + pokemon + " is not blacklisted."));
                    return 0;
                }
                CobblemonQuestsConfig.ignoredPokemon.remove(pokemon);
                CobblemonQuestsConfig.save();
                context.getSource().sendSystemMessage(Component.literal("Pokémon " + pokemon + " has been removed from the blacklist."));
            }
            return 1;
        }))).executes(context -> {
            context.getSource().sendSystemMessage(Component.literal("Currently blacklisted Pokémon: " + CobblemonQuestsConfig.ignoredPokemon));
            return 1;
        }).build();
    }
}