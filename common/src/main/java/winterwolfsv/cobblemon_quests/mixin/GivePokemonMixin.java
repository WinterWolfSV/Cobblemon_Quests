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

        if (matcher.find()) {
            String value = matcher.group(0);
            if (value.equals("true")) {
                player.sendMessage(Text.of("true"), false);
                try {
                    PokemonProperties pokemonProperties = PokemonPropertiesArgumentType.Companion.getPokemonProperties(context, "properties");
                    Pokemon pokemon = pokemonProperties.create();
                    CobblemonQuests.eventHandler.pokemonCatch(pokemon, player);
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }
            } else if(!value.equals("false")){
                player.sendMessage(Text.of("Command literal \"countascatch\" must be either true or false."), false);
            }
        }
    }
}
