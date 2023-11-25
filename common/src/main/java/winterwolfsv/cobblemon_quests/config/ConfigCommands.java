package winterwolfsv.cobblemon_quests.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;
import static winterwolfsv.cobblemon_quests.CobblemonQuests.config;

public class ConfigCommands {
    // /cobblemonquestsconfig <setting> <value>
    // setting: the setting to change, a string
    // value: the value to change the setting to, a string, float or boolean
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("cobblemonquestsconfig")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("setting", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            DefaultConfig defaultConfig = config.getDefaultConfig();
                            List<String> settings = new ArrayList<>(defaultConfig.getConfigValues().keySet());
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
                                    if (configTypes.get(setting).equals("String")) {
                                        return CommandSource.suggestMatching(new String[]{"string"}, builder);
                                    } else if (configTypes.get(setting).equals("float") || configTypes.get(setting).equals("int") || configTypes.get(setting).equals("double")) {
                                        return CommandSource.suggestMatching(new String[]{"number"}, builder);
                                    } else if (configTypes.get(setting).equals("boolean")) {
                                        return CommandSource.suggestMatching(new String[]{"true", "false"}, builder);
                                    }
                                    return CommandSource.suggestMatching(new String[]{"string"}, builder);
                                })
                                .executes(context -> {
                                    String setting = StringArgumentType.getString(context, "setting");
                                    String value = StringArgumentType.getString(context, "value");
                                    Object newValue = null;

                                    DefaultConfig defaultConfig = config.getDefaultConfig();
                                    HashMap<String, Object> configTypes = defaultConfig.getConfigTypes();

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
                                    }

                                    if (newValue != null) {
                                        config.setConfigValue(setting, newValue);
                                    } else {
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
