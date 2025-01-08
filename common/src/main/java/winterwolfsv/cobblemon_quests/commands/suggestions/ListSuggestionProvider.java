package winterwolfsv.cobblemon_quests.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ListSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    private final List<String> suggestions;

    public <E> ListSuggestionProvider(List<E> suggestions) {
        this.suggestions = suggestions.stream().map(Object::toString).toList();
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(suggestions, builder);
    }
}
