package winterwolfsv.cobblemon_quests.config;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;
import static winterwolfsv.cobblemon_quests.CobblemonQuests.config;

public class ConfigCommands {
    // WTF? Mixture of dynamic, static and just plain bad. Trying to add features to this is a nightmare.
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("cobblemonquestsconfig")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("setting", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            DefaultConfig defaultConfig = config.getDefaultConfig();
                            List<String> settings = new ArrayList<>(defaultConfig.getConfigValues().keySet());
                            settings.add("removeBlackListedPokemon");
                            return CommandSource.suggestMatching(settings, builder);
                        })
                        .executes(context -> {
                            String setting = StringArgumentType.getString(context, "setting");
                            String value = config.getConfigString(setting);
                            context.getSource().sendMessage(Text.of("§2" + setting + "§a is set to: §2" + value));
                            return 1;
                        })
                        .then(CommandManager.argument("value", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    String setting = StringArgumentType.getString(context, "setting");
                                    DefaultConfig defaultConfig = config.getDefaultConfig();
                                    HashMap<String, Object> configTypes = defaultConfig.getConfigTypes();
                                    configTypes.put("removeBlackListedPokemon", "List");
                                    if(!configTypes.containsKey(setting)) {
                                        return CommandSource.suggestMatching(new String[]{}, builder);
                                    }
                                    if (configTypes.get(setting).equals("String")) {
                                        return CommandSource.suggestMatching(new String[]{"string"}, builder);
                                    } else if (configTypes.get(setting).equals("float") || configTypes.get(setting).equals("int") || configTypes.get(setting).equals("double")) {
                                        return CommandSource.suggestMatching(new String[]{"number"}, builder);
                                    } else if (configTypes.get(setting).equals("boolean")) {
                                        return CommandSource.suggestMatching(new String[]{"true", "false"}, builder);
                                    } else if (Objects.equals(setting, "blackListedPokemon")) {
                                        List<Identifier> pokemons = new ArrayList<>(PokemonSpecies.INSTANCE.getSpecies().stream().map(species -> species.resourceIdentifier).toList());
                                        List<String> pokemonStrings = pokemons.stream().map(identifier -> "\"" + identifier.toString() + "\"").toList();
                                        return CommandSource.suggestMatching(pokemonStrings, builder);
                                    } else if (Objects.equals(setting, "removeBlackListedPokemon")) {
                                        List<String> pokemonStrings = config.getConfigList("blackListedPokemon").stream().map(identifier -> "\"" + identifier + "\"").toList();
                                        return CommandSource.suggestMatching(pokemonStrings, builder);
                                    }
                                    return CommandSource.suggestMatching(new String[]{"string"}, builder);
                                })
                                .executes(context -> {
                                    String setting = StringArgumentType.getString(context, "setting");
                                    String value = StringArgumentType.getString(context, "value");
                                    Object newValue = null;

                                    DefaultConfig defaultConfig = config.getDefaultConfig();
                                    HashMap<String, Object> configTypes = defaultConfig.getConfigTypes();
                                    configTypes.put("removeBlackListedPokemon", "List");


                                    if ((configTypes.get(setting).equals("float") || configTypes.get(setting).equals("int") || configTypes.get(setting).equals("double"))) {
                                        try {
                                            newValue = Float.parseFloat(value);
                                        } catch (NumberFormatException ignore) {
                                        }
                                    } else if (configTypes.get(setting).equals("boolean")) {
                                        if ((value.equals("true") || value.equals("false"))) {
                                            newValue = Boolean.parseBoolean(value);
                                        }
                                    } else if (configTypes.get(setting).equals("String")) {
                                        newValue = value;
                                    } else if (setting.equals("blackListedPokemon")) {
                                        config.addConfigValueStringToList(setting, value);
                                    } else if (setting.equals("removeBlackListedPokemon")) {
                                        config.removeConfigValueFromList("blackListedPokemon", value);
                                    }
                                    if (setting.equals("blackListedPokemon")) {
                                        context.getSource().sendMessage(Text.of("§aAdded §2" + value + "§a to §2" + setting));
                                        return 1;
                                    } else if (setting.equals("removeBlackListedPokemon")) {
                                        context.getSource().sendMessage(Text.of("§aRemoved §2" + value + "§a from §2" + setting));
                                        return 1;
                                    } else if (newValue != null) {
                                        config.setConfigValue(setting, newValue);
                                    } else if (!configTypes.get(setting).equals("List")) {
                                        context.getSource().sendMessage(Text.of("§cInvalid value for setting §4" + setting + "§c. Expected value is: §4" + configTypes.get(setting) + "§c."));
                                        return 1;
                                    }

                                    context.getSource().sendMessage(Text.of("§aSet §2" + setting + "§a to §2" + newValue));
                                    return 1;
                                })
                        )
                )
        );
    }
}
