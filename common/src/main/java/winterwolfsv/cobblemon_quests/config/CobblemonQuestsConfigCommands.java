package winterwolfsv.cobblemon_quests.config;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CobblemonQuestsConfigCommands {


    // /cobblemonquestsconfig blacklisted_pokemon <add/remove> <pokemon>
    // /cobblemonquestsconfig suppress_warnings <true/false>
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("cobblemonquestsconfig")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("suppress_warnings")
                        .then(argument("suppress_warnings", BoolArgumentType.bool())
                                .executes(context -> {
                                    if (Objects.equals(CobblemonQuestsConfig.suppressWarnings, BoolArgumentType.getBool(context, "suppress_warnings"))) {
                                        context.getSource().sendFeedback(() -> Text.of("Suppress Warnings is already set to: " + CobblemonQuestsConfig.suppressWarnings), true);
                                        return 0;
                                    }
                                    CobblemonQuestsConfig.suppressWarnings = BoolArgumentType.getBool(context, "suppress_warnings");
                                    CobblemonQuestsConfig.save();
                                    context.getSource().sendFeedback(() -> Text.of("Suppress Warnings set to: " + CobblemonQuestsConfig.suppressWarnings), true);
                                    return 1;
                                }))
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.of("Suppress Warnings is currently set to: " + CobblemonQuestsConfig.suppressWarnings), false);
                            return 1;
                        })
                )
                .then(literal("blacklisted_pokemon")
                        .then(argument("action", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    List<String> suggestions = List.of("add", "remove");
                                    return CommandSource.suggestMatching(suggestions, builder);
                                })
                                .then(argument("pokemon", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            List<String> suggestions = new ArrayList<>();
                                            if (StringArgumentType.getString(context, "action").equals("add")) {
                                                for (Species species : PokemonSpecies.INSTANCE.getSpecies()) {
                                                    suggestions.add(species.getName());
                                                }
                                            } else if (StringArgumentType.getString(context, "action").equals("remove")) {
                                                suggestions.addAll(CobblemonQuestsConfig.ignoredPokemon);
                                            }
                                            return CommandSource.suggestMatching(suggestions, builder);
                                        })
                                        .executes(context -> {
                                                    String action = StringArgumentType.getString(context, "action");
                                                    String pokemon = StringArgumentType.getString(context, "pokemon").toLowerCase();
                                                    if (action.equals("add")) {
                                                        if (CobblemonQuestsConfig.ignoredPokemon.contains(pokemon)) {
                                                            context.getSource().sendFeedback(() -> Text.of("Pokémon " + pokemon + " is already blacklisted."), true);
                                                            return 0;
                                                        }
                                                        CobblemonQuestsConfig.ignoredPokemon.add(pokemon);
                                                        CobblemonQuestsConfig.save();
                                                        context.getSource().sendFeedback(() -> Text.of("Pokémon " + pokemon + " has been blacklisted."), true);
                                                    } else if (action.equals("remove")) {
                                                        if (!CobblemonQuestsConfig.ignoredPokemon.contains(pokemon)) {
                                                            context.getSource().sendFeedback(() -> Text.of("Pokémon " + pokemon + " is not blacklisted."), true);
                                                            return 0;
                                                        }
                                                        CobblemonQuestsConfig.ignoredPokemon.remove(pokemon);
                                                        CobblemonQuestsConfig.save();
                                                        context.getSource().sendFeedback(() -> Text.of("Pokémon " + pokemon + " has been removed from the blacklist."), true);
                                                    }
                                                    return 1;
                                                }
                                        )))
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.of("Currently blacklisted Pokémon: " + CobblemonQuestsConfig.ignoredPokemon), false);
                            return 1;
                        })
                )

        );
    }
}