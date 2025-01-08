package winterwolfsv.cobblemon_quests.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import winterwolfsv.cobblemon_quests.tasks.CobblemonTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ActionListArgumentType implements ArgumentType<List<String>> {

    private final List<String> values = CobblemonTask.actionList;
    private final char delimiter = ',';

    public static ActionListArgumentType actionList() {
        return new ActionListArgumentType();
    }

    public static List<String> getActionList(final CommandContext<?> context, final String name) {
        // Yes, this is a safe cast
        return context.getArgument(name, List.class);
    }

    @Override
    public List<String> parse(StringReader reader) throws CommandSyntaxException {
        List<String> result = new ArrayList<>();
        reader.expect('{');
        while (reader.canRead() && reader.peek() != '}') {
            reader.skipWhitespace();
            int start = reader.getCursor();
            while (reader.canRead() && reader.peek() != delimiter && reader.peek() != '}') {
                reader.skip();
            }
            String value = reader.getString().substring(start, reader.getCursor()).trim();
            if (!value.isEmpty()) {
                result.add(value.toLowerCase());
            }
            if (reader.canRead() && reader.peek() == delimiter) {
                reader.skip();
            }
        }
        reader.expect('}');
        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (!builder.getRemaining().startsWith("{")) {
            builder.suggest("{");
            return builder.buildFuture();
        }
        for (String value : values) {
            String suggestion = getSuggestion(value, builder.getRemaining());
            if (suggestion != null) {
                builder.suggest(suggestion);
            }
        }
        return builder.buildFuture();
    }

    private String getSuggestion(String value, String current) {
        List<String> currentList = new ArrayList<>(Arrays.stream((current).split("[" + delimiter + "{]")).map(String::strip).map(String::toLowerCase).toList());
        if (currentList.isEmpty()) {
            currentList.add("");
        }
        if (value.toLowerCase().startsWith(currentList.getLast())) {
            return current + value.substring(currentList.getLast().length());
        }
        return null;
    }
}