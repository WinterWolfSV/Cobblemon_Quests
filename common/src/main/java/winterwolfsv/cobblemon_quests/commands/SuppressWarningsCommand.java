package winterwolfsv.cobblemon_quests.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;

import java.util.Objects;

public class SuppressWarningsCommand {
    public static CommandNode<CommandSourceStack> register() {
        return Commands.literal("suppress_warnings")
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
                .build();
    }
}