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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import winterwolfsv.cobblemon_quests.CobblemonQuests;

import java.util.ArrayList;
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
    public String pokemon_type = "choice_any";
    public String gender = "choice_any";
    public String form = "choice_any";
    public String region = "choice_any";


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
        nbt.putString("pokemon_type", pokemon_type);
        nbt.putString("gender", gender);
        nbt.putString("form", form);
        nbt.putString("region", region);
    }

    @Override
    public void readData(NbtCompound nbt) {
        super.readData(nbt);
        pokemon = new Identifier(nbt.getString("entity"));
        action = nbt.getString("action");
        value = nbt.getLong("value");
        shiny = nbt.getBoolean("shiny");
        pokemon_type = nbt.getString("pokemon_type");
        gender = nbt.getString("gender");
        form = nbt.getString("form");
        region = nbt.getString("region");
    }

    @Override
    public void writeNetData(PacketByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeString(pokemon.toString(), Short.MAX_VALUE);
        buffer.writeString(action, Short.MAX_VALUE);
        buffer.writeVarLong(value);
        buffer.writeBoolean(shiny);
        buffer.writeString(pokemon_type, Short.MAX_VALUE);
        buffer.writeString(gender, Short.MAX_VALUE);
        buffer.writeString(form, Short.MAX_VALUE);
        buffer.writeString(region, Short.MAX_VALUE);
    }

    @Override
    public void readNetData(PacketByteBuf buffer) {
        super.readNetData(buffer);
        pokemon = new Identifier(buffer.readString(Short.MAX_VALUE));
        action = buffer.readString(Short.MAX_VALUE);
        value = Long.valueOf(buffer.readVarInt());
        shiny = Boolean.valueOf(buffer.readBoolean());
        pokemon_type = buffer.readString(Short.MAX_VALUE);
        gender = buffer.readString(Short.MAX_VALUE);
        form = buffer.readString(Short.MAX_VALUE);
        region = buffer.readString(Short.MAX_VALUE);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        config.addEnum("action", action, v -> action = String.valueOf(v), NameMap.of(action, Arrays.asList("catch", "defeat", "evolve", "kill", "level_up"))
                .nameKey(v -> "cobblemon.action." + v)
                .icon(v -> pokeball_icon)
                .create(), action);


        List<Identifier> pokemons = new java.util.ArrayList<>(PokemonSpecies.INSTANCE.getSpecies().stream().map(species -> species.resourceIdentifier).toList());
        pokemons.add(0, pokemonAnyChoice);

        config.addEnum("pokemon", pokemon, v -> pokemon = v, NameMap.of(pokemon, pokemons)
                .nameKey(v -> "cobblemon.species." + v.getPath() + ".name")
                .icon(v -> ItemIcon.getItemIcon(getPokemonItem(v.getPath())))
                .create(), pokemon);


        config.addLong("value", value, v -> value = v, 1L, 1L, Long.MAX_VALUE);
        config.addBool("shiny", shiny, v -> shiny = v, false);


        String[] genders = {"choice_any", "male", "female", "genderless"};
        config.addEnum("gender", gender, v -> gender = v, NameMap.of(gender, Arrays.asList(genders))
                .nameKey(v -> "cobblemon_quests.gender." + v)
                .icon(v -> pokeball_icon)
                .create(), gender);

        String[] forms = {"choice_any", "normal", "alola", "galar", "paldea", "hisui"};
        config.addEnum("form", form, v -> form = v, NameMap.of(form, Arrays.asList(forms))
                .nameKey(v -> "cobblemon_quests.form." + v)
                .icon(v -> pokeball_icon)
                .create(), form);

        // Generations: gen1: kanto, gen2: johto, gen3: hoenn, gen4: sinnoh, gen5: unova, gen6: kalos, gen7: alola, gen8: galar, gen9: paldea
        String[] regions = {"choice_any", "gen1", "gen2", "gen3", "gen4", "gen5", "gen6", "gen7", "gen8", "gen9"};
        config.addEnum("region", region, v -> region = v, NameMap.of(region, Arrays.asList(regions))
                .nameKey(v -> "cobblemon_quests.region." + v)
                .icon(v -> pokeball_icon)
                .create(), region);

        String[] pokemon_types = {"choice_any", "normal", "fire", "water", "grass", "electric", "ice", "fighting", "poison", "ground", "flying", "psychic", "bug", "rock", "ghost", "dragon", "dark", "steel", "fairy"};
        config.addEnum("pokemon_type", pokemon_type, v -> pokemon_type = v, NameMap.of(pokemon_type, Arrays.asList(pokemon_types))
                .nameKey(v -> "cobblemon.type." + v)
                .icon(v -> pokeball_icon)
                .create(), pokemon_type);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Text getAltTitle() {

        boolean displayGender = !(gender.equals("choice_any") || gender.isEmpty());
        boolean displayType = !(pokemon_type.equals("choice_any") || pokemon_type.isEmpty());
        boolean displayForm = !(form.equals("choice_any") || form.equals("normal") || form.isEmpty());
        boolean displayRegion = !(region.equals("choice_any") || region.isEmpty());

        Text actionText = Text.translatable("cobblemon.action." + action);
        Text shinyText = shiny ? Text.translatable("ftbquests.task.cobblemon_tasks.cobblemon_task.shiny") : Text.of("");
        Text genderText = displayGender ? Text.translatable("cobblemon_quests.gender." + gender) : Text.of("");
        Text formText = displayForm ? Text.translatable("cobblemon_quests.form." + form) : Text.of("");
        Text typeText = displayType ? Text.translatable("cobblemon.type." + pokemon_type) : Text.of("");
        Text regionText = displayRegion ? Text.translatable("cobblemon_quests.region." + region) : Text.of("");
        Text pokemonName = !Objects.equals(pokemon.getPath(), "choice_any") ? Text.translatable("cobblemon.species." + pokemon.getPath() + ".name") : Text.translatable("ftbquests.task.cobblemon_tasks.cobblemon_task.pokemon");

        return Text.of(actionText.getString() + " " + value + "x" + (shiny ? " " + shinyText.getString() : "") + (displayGender ? " " + genderText.getString() : "") + (displayForm ? " " + formText.getString() : "") + (displayRegion ? " " + regionText.getString() : "") + (displayType ? " " + typeText.getString() : "") + " " + pokemonName.getString());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getAltIcon() {
        return ItemIcon.getItemIcon(getPokemonItem(pokemon.getPath()));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onButtonClicked(Button button, boolean canClick) {
        System.out.println("Button clicked");
    }

    public ItemStack getPokemonItem(String pokemon_name) {

        if (pokemon_name.equals("choice_any")) {
            return PokeBalls.INSTANCE.getPOKE_BALL().item().getDefaultStack();
        }
        Item pokemon_model_item = Registries.ITEM.get(new Identifier("cobblemon", "pokemon_model"));

        NbtCompound nbt = new NbtCompound();
        nbt.putString("species", "cobblemon:" + pokemon_name.toLowerCase().trim());

        pokemon_model_item.getDefaultStack().setNbt(nbt);

        ItemStack stack = new ItemStack(pokemon_model_item);

        stack.setNbt(nbt);

        return stack;
    }

    public void CobblemonTaskIncrease(TeamData teamData, Pokemon p, String executedAction, long progress) {
        System.out.println(p.getSpecies().toString());
        if (Objects.equals(action, executedAction) || Objects.equals(action, "obtain")) {

            // Check region
            if (!(region.equals("choice_any") || region.isEmpty())) {
                if (!p.getSpecies().getLabels().toString().contains((region))) {
                    return;
                }
            }

            // Check gender
            if (!(gender.equals("choice_any") || gender.isEmpty())) {
                if (!p.getGender().toString().toLowerCase().equals(gender)) {
                    return;
                }
            }

            // Check form
            if (!(form.equals("choice_any") || form.isEmpty())) {
                if (!p.getForm().getName().toString().toLowerCase().equals(form)) {
                    return;
                }
            }

            // Check type
            if (!(pokemon_type.equals("choice_any") || pokemon_type.isEmpty())) {
                List<String> types = new ArrayList<>();
                p.getTypes().iterator().forEachRemaining(type -> types.add(type.getName().toLowerCase()));
                if (!types.contains(pokemon_type)) {
                    return;
                }
            }

            // Check shiny
            if (!p.getShiny() && shiny) return;

            if (pokemon.getPath().equals("choice_any")) {
                teamData.addProgress(this, progress);
            } else if (!teamData.isCompleted(this) && pokemon.getPath().equalsIgnoreCase(p.getSpecies().toString())) {
                teamData.addProgress(this, progress);
            }
        }
    }
}
