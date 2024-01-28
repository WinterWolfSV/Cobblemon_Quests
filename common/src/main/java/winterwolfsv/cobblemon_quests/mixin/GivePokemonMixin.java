package winterwolfsv.cobblemon_quests.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.command.GivePokemon;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import winterwolfsv.cobblemon_quests.CobblemonQuests;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(GivePokemon.class)
public abstract class GivePokemonMixin {


//    @ModifyVariable(method = "register", at = @At("STORE"), ordinal = 0, remap = false)
//    private LiteralCommandNode selfCommand(LiteralCommandNode node) {
//        node.addChild(new LiteralCommandNode("countAsCatch", node.getCommand(), node.getRequirement(), node.getRedirect(), node.getRedirectModifier(), node.isFork()));
//        return node;
//    }

    @Inject(method = "execute", at = @At("HEAD"))
    private void execute(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, CallbackInfoReturnable<Integer> cir) {
        String input = context.getInput().toLowerCase(Locale.ROOT);
        Pattern pattern = Pattern.compile("(?<=countascatch=)\\w*");
        Matcher matcher = pattern.matcher(input);
        PokemonProperties pokemonProperties;
        Pokemon pokemon = null;
        try {
            pokemonProperties = PokemonPropertiesArgumentType.Companion.getPokemonProperties(context, "properties");
            pokemon = pokemonProperties.create();
        } catch (Exception e) {
            CobblemonQuests.LOGGER.severe(() -> "Failed to create pokemon from properties. " + e.getMessage());

        }
        if (matcher.find()) {
            String value = matcher.group(0);
            if (value.equals("true")) {
                CobblemonQuests.eventHandler.pokemonCatch(pokemon, player);
            } else if (value.equals("false")) {
                CobblemonQuests.eventHandler.pokemonObtain(pokemon, player);
            } else {
                player.sendMessage(Text.of("Command literal \"countascatch\" must be either true or false."), false);
            }
        } else {
            CobblemonQuests.eventHandler.pokemonObtain(pokemon, player);
        }
    }
}
