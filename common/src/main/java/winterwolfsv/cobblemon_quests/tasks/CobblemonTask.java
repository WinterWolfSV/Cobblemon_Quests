package winterwolfsv.cobblemon_quests.tasks;

import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import winterwolfsv.cobblemon_quests.CobblemonQuests;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CobblemonTask extends Task {
    Identifier pokemonAnyChoice = new Identifier(CobblemonQuests.MOD_ID, "choice_any");
    public Identifier pokemon = pokemonAnyChoice;

    public long value = 1L;
    public Icon pokeball_icon = ItemIcon.getItemIcon(PokeBalls.INSTANCE.getPOKE_BALL().item());
    public String action = "catch";
    public boolean shiny = false;

    public CobblemonTask(long id, Quest quest) {
        super(id, quest);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.COBBLEMON;
    }

    @Override
    public long getMaxProgress() {
        return value;
    }

    @Override
    public void writeData(NbtCompound nbt) {
        super.writeData(nbt);
        nbt.putString("action", action);
        nbt.putString("entity", pokemon.toString());
        nbt.putLong("value", value);
        nbt.putBoolean("shiny", shiny);
    }

    @Override
    public void readData(NbtCompound nbt) {
        super.readData(nbt);
        pokemon = new Identifier(nbt.getString("entity"));
        action = nbt.getString("action");
        value = nbt.getLong("value");
        shiny = nbt.getBoolean("shiny");
    }

    @Override
    public void writeNetData(PacketByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeString(pokemon.toString(), Short.MAX_VALUE);
        buffer.writeString(action, Short.MAX_VALUE);
        buffer.writeVarLong(value);
        buffer.writeBoolean(shiny);
    }

    @Override
    public void readNetData(PacketByteBuf buffer) {
        super.readNetData(buffer);
        pokemon = new Identifier(buffer.readString(Short.MAX_VALUE));
        action = buffer.readString(Short.MAX_VALUE);
        value = Long.valueOf(buffer.readVarInt());
        shiny = Boolean.valueOf(buffer.readBoolean());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        config.addEnum("action", action, v -> action = String.valueOf(v), NameMap.of(action, Arrays.asList("kill", "defeat", "catch"))
                .nameKey(v -> "cobblemon.action." + v)
                .icon(v -> pokeball_icon)
                .create(), action);


        List<Identifier> pokemons = new java.util.ArrayList<>(PokemonSpecies.INSTANCE.getSpecies().stream().map(species -> species.resourceIdentifier).toList());
        pokemons.add(0, pokemonAnyChoice);

        config.addEnum("pokemon", pokemon, v -> pokemon = v, NameMap.of(pokemon, pokemons)
                .nameKey(v -> "cobblemon.species." + v.getPath() + ".name")
                .icon(v -> pokeball_icon)
                .create(), pokemon);

        config.addLong("value", value, v -> value = v, 1L, 1L, Long.MAX_VALUE);
        config.addBool("shiny", shiny, v -> shiny = v, false);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Text getAltTitle() {
        String pokemonName = Text.translatable("cobblemon.species." + pokemon.getPath() + ".name").getString();
        if (Objects.equals(pokemon.getPath(), "choice_any")) {
            pokemonName = "Pokémon";
        }
        return Text.translatable(CobblemonQuests.MOD_ID + ".task." + action + ".title" + (shiny ? ".shiny" : ""), formatMaxProgress(), pokemonName);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getAltIcon() {
        return pokeball_icon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onButtonClicked(Button button, boolean canClick) {
    }

    public void CobblemonTaskIncrease(TeamData teamData, Pokemon p, String executedAction) {
        if (Objects.equals(action, executedAction)) {
            if (!p.getShiny() && shiny) return;
            if (pokemon.getPath().equals("choice_any")) {
                teamData.addProgress(this, 1L);
            } else if (!teamData.isCompleted(this) && pokemon.getPath().equalsIgnoreCase(p.getSpecies().toString())) {
                teamData.addProgress(this, 1L);
            }
        }
    }
}
