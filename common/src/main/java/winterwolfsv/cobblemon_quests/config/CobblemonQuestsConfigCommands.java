package winterwolfsv.cobblemon_quests.config;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

public class CobblemonQuestsConfigCommands {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cobblemonquestsconfig")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("suppress_warnings")
                        .then(Commands.argument("suppress_warnings", BoolArgumentType.bool())
                                .executes(context -> {
                                    if (Objects.equals(CobblemonQuestsConfig.suppressWarnings, BoolArgumentType.getBool(context, "suppress_warnings"))) {
                                        context.getSource().sendSystemMessage(Component.literal("Suppress Warnings is already set to: " + CobblemonQuestsConfig.suppressWarnings));
                                        return 0;
                                    }
                                    CobblemonQuestsConfig.suppressWarnings = BoolArgumentType.getBool(context, "suppress_warnings");
                                    CobblemonQuestsConfig.save();
                                    context.getSource().sendSystemMessage(Component.literal("Suppress Warnings set to: " + CobblemonQuestsConfig.suppressWarnings));
                                    return 1;
                                }))
                        .executes(context -> {
                            context.getSource().sendSystemMessage(Component.literal("Suppress Warnings is currently set to: " + CobblemonQuestsConfig.suppressWarnings));
                            return 1;
                        })
                )
                .then(Commands.literal("blacklisted_pokemon")
                        .then(Commands.argument("action", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    List<String> suggestions = List.of("add", "remove");
                                    String input = builder.getRemainingLowerCase();
                                    suggestions.stream()
                                            .filter(suggestion -> suggestion.startsWith(input))
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("pokemon", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            String input = builder.getRemainingLowerCase();
                                            if (StringArgumentType.getString(context, "action").equals("add")) {
                                                PokemonSpecies.INSTANCE.getSpecies().stream()
                                                        .filter(p -> p.getName().toLowerCase().startsWith(input))
                                                        .forEach(p -> builder.suggest(p.getName()));
                                            } else if (StringArgumentType.getString(context, "action").equals("remove")) {
                                                CobblemonQuestsConfig.ignoredPokemon.stream()
                                                        .filter(p -> p.toLowerCase().startsWith(input))
                                                        .forEach(builder::suggest);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> {
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
                                                }
                                        )))
                        .executes(context -> {
                            context.getSource().sendSystemMessage(Component.literal("Currently blacklisted Pokémon: " + CobblemonQuestsConfig.ignoredPokemon));
                            return 1;
                        })
                )
        );
    }
}