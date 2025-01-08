package winterwolfsv.cobblemon_quests.tasks;

import com.cobblemon.mod.common.CobblemonItemComponents;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
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

public class CobblemonTask extends Task {
    public static final List<String> actionList = Arrays.asList("catch", "defeat", "evolve", "kill", "level_up", "level_up_to", "release", "trade_away", "trade_for", "obtain", "select_starter", "revive_fossil");
    public Icon pokeBallIcon = ItemIcon.getItemIcon(PokeBalls.INSTANCE.getPOKE_BALL().item());
    public long amount = 1L;
    public boolean shiny = false;
    public long timeMin = 0;
    public long timeMax = 24000;
    public ArrayList<String> actions = new ArrayList<>(List.of("catch"));
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
        nbt.putString("action", writeArrayList(actions));
        nbt.putString("biome", writeArrayList(biomes));
        nbt.putString("dimension", writeArrayList(dimensions));
        nbt.putString("pokemon", writeArrayList(pokemons));
        nbt.putString("form", writeArrayList(forms));
        nbt.putString("gender", writeArrayList(genders));
        nbt.putString("poke_ball_used", writeArrayList(pokeBallsUsed));
        nbt.putString("pokemon_type", writeArrayList(pokemonTypes));
        nbt.putString("region", writeArrayList(regions));
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        amount = nbt.getLong("amount");
        shiny = nbt.getBoolean("shiny");
        timeMin = nbt.getLong("time_min");
        timeMax = nbt.getLong("time_max");
        actions = readArrayList(nbt.getString("action"));
        biomes = readArrayList(nbt.getString("biome"));
        dimensions = readArrayList(nbt.getString("dimension"));
        pokemons = readArrayList(nbt.getString("pokemon"));
        forms = readArrayList(nbt.getString("form"));
        genders = readArrayList(nbt.getString("gender"));
        pokeBallsUsed = readArrayList(nbt.getString("poke_ball_used"));
        pokemonTypes = readArrayList(nbt.getString("pokemon_type"));
        regions = readArrayList(nbt.getString("region"));
        if (timeMin == timeMax && timeMin == 0) {
            timeMax = 24000;
        }
        if (nbt.contains("value")) {
            amount = nbt.getLong("value");
        }
        if (nbt.contains("entity")) {
            pokemons = readArrayList(nbt.getString("entity"));
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
        buffer.writeUtf(writeArrayList(pokemons), Short.MAX_VALUE);
        buffer.writeUtf(writeArrayList(actions), Short.MAX_VALUE);
        buffer.writeUtf(writeArrayList(biomes), Short.MAX_VALUE);
        buffer.writeUtf(writeArrayList(dimensions), Short.MAX_VALUE);
        buffer.writeUtf(writeArrayList(forms), Short.MAX_VALUE);
        buffer.writeUtf(writeArrayList(genders), Short.MAX_VALUE);
        buffer.writeUtf(writeArrayList(pokeBallsUsed), Short.MAX_VALUE);
        buffer.writeUtf(writeArrayList(pokemonTypes), Short.MAX_VALUE);
        buffer.writeUtf(writeArrayList(regions), Short.MAX_VALUE);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        amount = buffer.readLong();
        shiny = buffer.readBoolean();
        timeMin = buffer.readLong();
        timeMax = buffer.readLong();
        pokemons = readArrayList(buffer.readUtf(Short.MAX_VALUE));
        actions = readArrayList(buffer.readUtf(Short.MAX_VALUE));
        biomes = readArrayList(buffer.readUtf(Short.MAX_VALUE));
        dimensions = readArrayList(buffer.readUtf(Short.MAX_VALUE));
        forms = readArrayList(buffer.readUtf(Short.MAX_VALUE));
        genders = readArrayList(buffer.readUtf(Short.MAX_VALUE));
        pokeBallsUsed = readArrayList(buffer.readUtf(Short.MAX_VALUE));
        pokemonTypes = readArrayList(buffer.readUtf(Short.MAX_VALUE));
        regions = readArrayList(buffer.readUtf(Short.MAX_VALUE));
    }

    public String writeArrayList(ArrayList<String> list) {
        list.removeIf(Objects::isNull);
        list = new ArrayList<>(new LinkedHashSet<>(list));
        return String.join(",", list);
    }

    public ArrayList<String> readArrayList(String s) {
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
        List<String> pokemonList = new ArrayList<>(PokemonSpecies.INSTANCE.getSpecies().stream().map(species -> species.resourceIdentifier.toString()).toList());
        Collections.sort(pokemonList);
        addConfigList(config, "pokemons", pokemons, pokemonList, this::getPokemonIcon, pokemonNameProcessor);
        config.addLong("amount", amount, v -> amount = v, 1L, 1L, Long.MAX_VALUE).setNameKey(MOD_ID + ".task.amount");
        config.addBool("shiny", shiny, v -> shiny = v, false).setNameKey(MOD_ID + ".task.shiny");
        Function<String, String> pokeBallNameProcessor = (name) -> "item." + name.replace(":", ".");
        List<String> pokeBallList = new ArrayList<>(PokeBalls.INSTANCE.all().stream().map(pokeBall -> pokeBall.getName().toString()).toList());
        Collections.sort(pokeBallList);
        addConfigList(config, "pokeballs", pokeBallsUsed, pokeBallList, this::getIconFromIdentifier, pokeBallNameProcessor);
        List<String> formList = Arrays.asList("normal", "alola", "galar", "paldea", "hisui");
        addConfigList(config, "forms", forms, formList, null, null);
        List<String> genderList = Arrays.asList("male", "female", "genderless");
        addConfigList(config, "genders", genders, genderList, null, null);
        Function<String, String> pokemonTypeNameProcessor = (name) -> "cobblemon.type." + name;
        List<String> pokemonTypeList = Arrays.asList("normal", "fire", "water", "grass", "electric", "ice", "fighting", "poison", "ground", "flying", "psychic", "bug", "rock", "ghost", "dragon", "dark", "steel", "fairy");
        addConfigList(config, "pokemon_types", pokemonTypes, pokemonTypeList, null, pokemonTypeNameProcessor);
        List<String> regionList = Arrays.asList("gen1", "gen2", "gen3", "gen4", "gen5", "gen6", "gen7", "gen8", "gen9");
        addConfigList(config, "regions", regions, regionList, null, null);
        Function<String, String> biomeAndDimensionNameProcessor = (name) -> "(" + name.replace("_", " ").replace(":", ") ");
        List<String> biomesList = new ArrayList<>(registryManager.registryOrThrow(BuiltInRegistries.BIOME_SOURCE.key()).entrySet().stream().map(entry -> entry.getKey().toString()).toList());
        addConfigList(config, "biomes", biomes, biomesList, null, biomeAndDimensionNameProcessor);
        // Sorry to anyone with custom dimensions :/ Feel free to PR if you find a way to get dimensions dynamically on the client
        List<String> dimensionList = List.of("minecraft:overworld", "minecraft:the_end", "minecraft:the_nether");
        addConfigList(config, "dimensions", dimensions, dimensionList, null, biomeAndDimensionNameProcessor);
        config.addLong("time_min", timeMin, v -> timeMin = v, 0L, 0L, 24000L).setNameKey(MOD_ID + ".task.time_min");
        config.addLong("time_max", timeMax, v -> timeMax = v, 24000L, 0L, 24000L).setNameKey(MOD_ID + ".task.time_max");
    }

    private void addConfigList(ConfigGroup config, String listName, List<String> listData, List<String> optionsList, Function<ResourceLocation, Icon> iconProcessor, Function<String, String> nameProcessor) {
        NameMap<String> nameMap = NameMap.of(optionsList.get(0), optionsList).id(s -> s).name(s -> Component.translatable(nameProcessor == null ? MOD_ID + "." + listName + "." + s : nameProcessor.apply(s))).icon(s -> iconProcessor == null ? pokeBallIcon : iconProcessor.apply(ResourceLocation.parse(s))).create();
        config.addList(listName, listData, new EnumConfig<>(nameMap), optionsList.get(optionsList.size() - 1)).setNameKey(MOD_ID + ".task." + listName);
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
        return getPokemonIcon(ResourceLocation.parse(pokemons.get(0)));
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

    public void CobblemonTaskIncrease(TeamData teamData, Pokemon pokemon, String executedAction, long progress, ServerPlayer player) {
        String[] obtainingMethods = {"catch", "evolve", "trade_for", "obtain", "revive_fossil"};
        if (CobblemonQuestsConfig.ignoredPokemon.contains(pokemon.getSpecies().toString().toLowerCase())) return;
        if (actions.contains(executedAction) || (actions.contains("obtain") && Arrays.asList(obtainingMethods).contains(executedAction))) {
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
                if (!forms.contains(pokemon.getForm().getName().toLowerCase())) {
                    return;
                }
            }
            // Check type
            if (!pokemonTypes.isEmpty()) {
                List<String> types = new ArrayList<>();
                pokemon.getTypes().iterator().forEachRemaining(type -> {
                    String typeName = type.getName().toLowerCase();
                    if (pokemonTypes.contains(typeName)) types.add(typeName);
                });
                if (types.isEmpty()) {
                    return;
                }
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
}
