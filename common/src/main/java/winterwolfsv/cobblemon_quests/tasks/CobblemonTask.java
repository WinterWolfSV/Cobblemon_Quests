package winterwolfsv.cobblemon_quests.tasks;

import com.cobblemon.mod.common.CobblemonItemComponents;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.item.components.PokemonItemComponent;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.ftb.mods.ftblibrary.config.*;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector4f;
import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;
import static winterwolfsv.cobblemon_quests.tasks.TaskData.*;

public class CobblemonTask extends Task {
    public Icon pokeBallIcon = ItemIcon.getItemIcon(PokeBalls.INSTANCE.getPOKE_BALL().item());
    public long amount = 1L;
    public boolean shiny = false;
    public long timeMin = 0;
    public long timeMax = 24000;
    public ArrayList<String> actions = new ArrayList<>();
    public ArrayList<String> biomes = new ArrayList<>();
    public ArrayList<String> dimensions = new ArrayList<>();
    public ArrayList<String> forms = new ArrayList<>();
    public ArrayList<String> genders = new ArrayList<>();
    public ArrayList<String> pokeBallsUsed = new ArrayList<>();
    public ArrayList<String> pokemons = new ArrayList<>();
    public ArrayList<String> pokemonTypes = new ArrayList<>();
    public ArrayList<String> regions = new ArrayList<>();

    public CobblemonTask(long id, Quest quest) {
        super(id, quest);
    }

    @Override
    public TaskType getType() {
        return PokemonTaskTypes.COBBLEMON;
    }

    @Override
    public long getMaxProgress() {
        return amount;
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        nbt.putLong("amount", amount);
        nbt.putBoolean("shiny", shiny);
        nbt.putLong("time_min", timeMin);
        nbt.putLong("time_max", timeMax);
        nbt.putString("action", writeList(actions));
        nbt.putString("biome", writeList(biomes));
        nbt.putString("dimension", writeList(dimensions));
        nbt.putString("pokemon", writeList(pokemons));
        nbt.putString("form", writeList(forms));
        nbt.putString("gender", writeList(genders));
        nbt.putString("poke_ball_used", writeList(pokeBallsUsed));
        nbt.putString("pokemon_type", writeList(pokemonTypes));
        nbt.putString("region", writeList(regions));
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        amount = nbt.getLong("amount");
        shiny = nbt.getBoolean("shiny");
        timeMin = nbt.getLong("time_min");
        timeMax = nbt.getLong("time_max");
        actions = readList(nbt.getString("action"));
        biomes = readList(nbt.getString("biome"));
        dimensions = readList(nbt.getString("dimension"));
        pokemons = readList(nbt.getString("pokemon"));
        forms = readList(nbt.getString("form"));
        genders = readList(nbt.getString("gender"));
        pokeBallsUsed = readList(nbt.getString("poke_ball_used"));
        pokemonTypes = readList(nbt.getString("pokemon_type"));
        regions = readList(nbt.getString("region"));

        if (!forms.isEmpty()) {
            Map<String, String> formReplacements = Map.of(
                    "alola", "alolan",
                    "galar", "galarian",
                    "paldea", "paldean",
                    "hisui", "hisuian"
            );
            forms.replaceAll(form -> formReplacements.getOrDefault(form, form));
        }
        if (timeMin == timeMax && timeMin == 0) {
            timeMax = 24000;
        }
        if (nbt.contains("value")) {
            amount = nbt.getLong("value");
        }
        if (nbt.contains("entity")) {
            pokemons = readList(nbt.getString("entity"));
        }
        if (amount == 0) {
            amount = 1;
        }
        pokemons.remove("minecraft:");
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeLong(amount);
        buffer.writeBoolean(shiny);
        buffer.writeLong(timeMin);
        buffer.writeLong(timeMax);
        buffer.writeUtf(writeList(pokemons), Short.MAX_VALUE);
        buffer.writeUtf(writeList(actions), Short.MAX_VALUE);
        buffer.writeUtf(writeList(biomes), Short.MAX_VALUE);
        buffer.writeUtf(writeList(dimensions), Short.MAX_VALUE);
        buffer.writeUtf(writeList(forms), Short.MAX_VALUE);
        buffer.writeUtf(writeList(genders), Short.MAX_VALUE);
        buffer.writeUtf(writeList(pokeBallsUsed), Short.MAX_VALUE);
        buffer.writeUtf(writeList(pokemonTypes), Short.MAX_VALUE);
        buffer.writeUtf(writeList(regions), Short.MAX_VALUE);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        amount = buffer.readLong();
        shiny = buffer.readBoolean();
        timeMin = buffer.readLong();
        timeMax = buffer.readLong();
        pokemons = readList(buffer.readUtf(Short.MAX_VALUE));
        actions = readList(buffer.readUtf(Short.MAX_VALUE));
        biomes = readList(buffer.readUtf(Short.MAX_VALUE));
        dimensions = readList(buffer.readUtf(Short.MAX_VALUE));
        forms = readList(buffer.readUtf(Short.MAX_VALUE));
        genders = readList(buffer.readUtf(Short.MAX_VALUE));
        pokeBallsUsed = readList(buffer.readUtf(Short.MAX_VALUE));
        pokemonTypes = readList(buffer.readUtf(Short.MAX_VALUE));
        regions = readList(buffer.readUtf(Short.MAX_VALUE));
    }

    public String writeList(ArrayList<String> list) {
        list.removeIf(Objects::isNull);
        return String.join(",", list);
    }

    public ArrayList<String> readList(String s) {
        return Arrays.stream(s.split(",")).map(String::trim).filter(obj -> !obj.isEmpty() && !obj.contains("choice_any")).distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        // Asserts that the client is in a world, something that always should be true when the config is opened.
        assert Minecraft.getInstance().level != null;
        RegistryAccess registryManager = Minecraft.getInstance().level.registryAccess();
        addConfigList(config, "actions", actions, actionList, null, null);
        Function<String, String> pokemonNameProcessor = (name) -> name.split(":")[0] + ".species." + name.split(":")[1] + ".name";
        List<String> pokemonList = PokemonSpecies.INSTANCE.getSpecies().stream().map(species -> species.resourceIdentifier.toString()).sorted().toList();
        addConfigList(config, "pokemons", pokemons, pokemonList, this::getPokemonIcon, pokemonNameProcessor);
        config.addLong("amount", amount, v -> amount = v, 1L, 1L, Long.MAX_VALUE).setNameKey(MOD_ID + ".task.amount");
        config.addBool("shiny", shiny, v -> shiny = v, false).setNameKey(MOD_ID + ".task.shiny");
        Function<String, String> pokeBallNameProcessor = (name) -> "item." + name.replace(":", ".");
        List<String> pokeBallList = PokeBalls.INSTANCE.all().stream().map(pokeBall -> pokeBall.getName().toString()).sorted().toList();
        addConfigList(config, "pokeballs", pokeBallsUsed, pokeBallList, this::getIconFromIdentifier, pokeBallNameProcessor);
        addConfigList(config, "forms", forms, formList, null, null);
        addConfigList(config, "genders", genders, genderList, null, null);
        Function<String, String> pokemonTypeNameProcessor = (name) -> "cobblemon.type." + name;
        addConfigList(config, "pokemon_types", pokemonTypes, pokemonTypeList, null, pokemonTypeNameProcessor);
        addConfigList(config, "regions", regions, regionList, null, null);
        Function<String, String> biomeAndDimensionNameProcessor = (name) -> "(" + name.replace("_", " ").replace(":", ") ");
        List<String> biomeList = registryManager.registryOrThrow(Registries.BIOME).entrySet().stream().map(entry -> entry.getKey().location().toString()).toList();
        addConfigList(config, "biomes", biomes, biomeList, null, biomeAndDimensionNameProcessor);
        ArrayList<String> dimensionList = new ArrayList<>(registryManager.registryOrThrow(Registries.DIMENSION_TYPE).entrySet().stream().map(entry -> entry.getKey().location().toString()).toList());
        dimensionList.remove("minecraft:overworld_caves");
        addConfigList(config, "dimensions", dimensions, dimensionList, null, biomeAndDimensionNameProcessor);
        config.addLong("time_min", timeMin, v -> timeMin = v, 0L, 0L, 24000L).setNameKey(MOD_ID + ".task.time_min");
        config.addLong("time_max", timeMax, v -> timeMax = v, 24000L, 0L, 24000L).setNameKey(MOD_ID + ".task.time_max");
    }

    private void addConfigList(ConfigGroup config, String listName, List<String> listData, List<String> optionsList, Function<ResourceLocation, Icon> iconProcessor, Function<String, String> nameProcessor) {
        NameMap<String> nameMap = NameMap.of(optionsList.getFirst(), optionsList).id(s -> s).name(s -> Component.translatable(nameProcessor == null ? MOD_ID + "." + listName + "." + s : nameProcessor.apply(s))).icon(s -> iconProcessor == null ? pokeBallIcon : iconProcessor.apply(ResourceLocation.parse(s))).create();
        config.addList(listName, listData, new EnumConfig<>(nameMap), optionsList.getLast()).setNameKey(MOD_ID + ".task." + listName);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Component getAltTitle() {
        StringBuilder titleBuilder = new StringBuilder();
        for (String action : actions) {
            titleBuilder.append(Component.translatable("cobblemon_quests.actions." + action).getString()).append(" ");
        }
        titleBuilder.append(amount).append("x ");
        if (shiny) {
            titleBuilder.append(Component.translatable("cobblemon_quests.task.shiny").getString()).append(" ");
        }
        for (String gender : genders) {
            titleBuilder.append(Component.translatable("cobblemon_quests.genders." + gender).getString()).append(" ");
        }
        for (String form : forms) {
            titleBuilder.append(Component.translatable("cobblemon_quests.forms." + form).getString()).append(" ");
        }
        for (String region : regions) {
            titleBuilder.append(Component.translatable("cobblemon_quests.regions." + region).getString()).append(" ");
        }
        for (String pokemonType : pokemonTypes) {
            titleBuilder.append(Component.translatable("cobblemon.type." + pokemonType).getString()).append(" ");
        }
        if (pokemons.isEmpty()) {
            titleBuilder.append(Component.translatable("cobblemon_quests.task.pokemons").getString()).append(" ");
        } else {
            for (String pokemon : pokemons) {
                titleBuilder.append(Component.translatable("cobblemon.species." + pokemon.split(":")[1] + ".name").getString()).append(" ");
                if (pokemons.indexOf(pokemon) != pokemons.size() - 1) {
                    titleBuilder.append("or ");
                }
            }
        }
        for (String pokeballUsed : pokeBallsUsed) {
            if (pokeBallsUsed.indexOf(pokeballUsed) == 0) {
                titleBuilder.append("using a ");
            } else {
                titleBuilder.append("or ");
            }
            titleBuilder.append(Component.translatable("item." + pokeballUsed.replace(":", ".")).getString()).append(" ");
        }
        for (String dimension : dimensions) {
            titleBuilder.append("in ").append(dimension.split(":")[1].replace("_", " ")).append(" ");
        }
        for (String biome : biomes) {
            titleBuilder.append("in a ").append(biome.split(":")[1].replace("_", " ")).append(" biome ");
        }
        if (!(timeMin == 0 && timeMax == 24000)) {
            titleBuilder.append("between the time ").append(timeMin).append(" and ").append(timeMax);
        }

        return Component.literal(titleBuilder.toString().trim());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getAltIcon() {
        if (pokemons.isEmpty()) {
            return pokeBallIcon;
        }
        return getPokemonIcon(ResourceLocation.parse(pokemons.getFirst()));
    }


    public Icon getIconFromIdentifier(ResourceLocation ResourceLocation) {
        ItemStack itemStack = BuiltInRegistries.ITEM.get(ResourceLocation).getDefaultInstance();
        if (itemStack.isEmpty()) {
            return pokeBallIcon;
        } else {
            return ItemIcon.getItemIcon(itemStack);
        }

    }

    public Icon getPokemonIcon(ResourceLocation pokemon) {
        // TODO Figure out why the pokemon icon tint is so dark
        Item pokemonModelItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("cobblemon", "pokemon_model"));
        CompoundTag nbt = new CompoundTag();
        nbt.putString("species", pokemon.toString());
        ItemStack stack = new ItemStack(pokemonModelItem);
        PokemonItemComponent c = new PokemonItemComponent(pokemon, new HashSet<>(), new Vector4f(1, 1, 1, 1));
        stack.set(CobblemonItemComponents.INSTANCE.getPOKEMON_ITEM(), c);
        return ItemIcon.getItemIcon(stack);
        // Command to give player pokemon model:
        // give @s cobblemon:pokemon_model[cobblemon:pokemon_item={species:"cobblemon:<pokemon_name>",aspects:[]}]
    }

    public void increase(TeamData teamData, Pokemon pokemon, String executedAction, long progress, ServerPlayer player) {
        List<String> obtainingMethods = List.of("catch", "evolve-into", "trade_for", "obtain", "revive_fossil");
        if (CobblemonQuestsConfig.ignoredPokemon.contains(pokemon.getSpecies().toString().toLowerCase())) return;
        if (actions.contains(executedAction) || (actions.contains("obtain") && obtainingMethods.contains(executedAction))) {
            Level world = player.level();
            // Check region
            if (!regions.isEmpty()) {
                if (!regions.contains(pokemon.getSpecies().getLabels().toArray()[0].toString())) {
                    return;
                }
            }
            // Check the time of action
            if (!(timeMin == 0 && timeMax == 24000)) {
                long timeOfDay = world.getDayTime() % 24000;
                long actualMin = timeMin;
                long actualMax = timeMax;
                // Adjusts the time to account for the 24000 cycle
                if (timeMin > timeMax) {
                    actualMax = timeMax + 24000;
                    if (timeOfDay < timeMin) {
                        timeOfDay += 24000;
                    }
                }
                if (timeOfDay < actualMin || timeOfDay >= actualMax) {
                    return;
                }
            }
            if (!pokeBallsUsed.isEmpty()) {
                if (!pokeBallsUsed.contains(pokemon.getCaughtBall().getName().toString())) {
                    return;
                }
            }
            // Check dimension
            if (!dimensions.isEmpty()) {
                if (!dimensions.contains(world.dimension().location().toString())) {
                    return;
                }
            }
            // Check biome
            if (!biomes.isEmpty()) {
                if (!biomes.contains(world.getBiome(player.blockPosition()).unwrapKey().get().location().toString())) {
                    return;
                }
            }
            // Check gender
            if (!genders.isEmpty()) {
                if (!genders.contains(pokemon.getGender().toString().toLowerCase())) {
                    return;
                }
            }
            // Check form
            if (!forms.isEmpty()) {
                boolean flag = forms.contains(pokemon.getForm().getName().toLowerCase());
                for (String aspect : pokemon.getAspects()) {
                    if (forms.contains(aspect)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) return;
            }

            // Check type
            if (!pokemonTypes.isEmpty()) {
                boolean flag = false;
                for (ElementalType type : pokemon.getTypes()) {
                    if (pokemonTypes.contains(type.getName().toLowerCase())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) return;
            }

            // Check shiny
            if (!pokemon.getShiny() && shiny) return;
            boolean shouldAddProgress = pokemons.stream().anyMatch(p -> p.split(":").length > 1 && p.split(":")[1].equals(pokemon.getSpecies().toString())) || pokemons.isEmpty();

            if (shouldAddProgress) {
                if (executedAction.equals("level_up_to")) {
                    if (teamData.getProgress(this) < progress) {
                        teamData.setProgress(this, progress);
                    }
                    return;
                }
                teamData.addProgress(this, progress);
            }
        }
    }

    // data is a string that should match an entry in the chosen pokemon list. The data is entered (comma separated) in the form field.
    public void increaseWoPokemon(TeamData teamData, String data, String executedAction, long progress) {
        System.out.println(data + " " + executedAction + " " + progress + " " + forms);
        if (actions.contains(executedAction) && forms.contains(data) || forms.isEmpty()) {
            System.out.println("Adding progress");
            teamData.addProgress(this, progress);
        }
    }
}
