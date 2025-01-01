package winterwolfsv.cobblemon_quests.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ListSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    private final List<?> suggestions;

    public <E> ListSuggestionProvider(List<E> suggestions) {
        this.suggestions = suggestions;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String remaining = builder.getRemainingLowerCase();
        for (Object suggestion : suggestions) {
            if (suggestion.toString().toLowerCase().startsWith(remaining)) {
                builder.suggest(suggestion.toString());
            }
        }
        return builder.buildFuture();
    }
}
