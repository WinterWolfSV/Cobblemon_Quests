package winterwolfsv.cobblemon_quests.tasks;

import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import winterwolfsv.cobblemon_quests.config.CobblemonQuestsConfig;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;

public class CobblemonTask extends Task {
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
    public void writeData(NbtCompound nbt) {
        super.writeData(nbt);
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
    public void readData(NbtCompound nbt) {
        super.readData(nbt);
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
    }

    @Override
    public void writeNetData(PacketByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeLong(amount);
        buffer.writeBoolean(shiny);
        buffer.writeLong(timeMin);
        buffer.writeLong(timeMax);
        buffer.writeString(writeArrayList(pokemons), Short.MAX_VALUE);
        buffer.writeString(writeArrayList(actions), Short.MAX_VALUE);
        buffer.writeString(writeArrayList(biomes), Short.MAX_VALUE);
        buffer.writeString(writeArrayList(dimensions), Short.MAX_VALUE);
        buffer.writeString(writeArrayList(forms), Short.MAX_VALUE);
        buffer.writeString(writeArrayList(genders), Short.MAX_VALUE);
        buffer.writeString(writeArrayList(pokeBallsUsed), Short.MAX_VALUE);
        buffer.writeString(writeArrayList(pokemonTypes), Short.MAX_VALUE);
        buffer.writeString(writeArrayList(regions), Short.MAX_VALUE);
    }

    @Override
    public void readNetData(PacketByteBuf buffer) {
        super.readNetData(buffer);
        amount = buffer.readLong();
        shiny = buffer.readBoolean();
        timeMin = buffer.readLong();
        timeMax = buffer.readLong();
        pokemons = readArrayList(buffer.readString(Short.MAX_VALUE));
        actions = readArrayList(buffer.readString(Short.MAX_VALUE));
        biomes = readArrayList(buffer.readString(Short.MAX_VALUE));
        dimensions = readArrayList(buffer.readString(Short.MAX_VALUE));
        forms = readArrayList(buffer.readString(Short.MAX_VALUE));
        genders = readArrayList(buffer.readString(Short.MAX_VALUE));
        pokeBallsUsed = readArrayList(buffer.readString(Short.MAX_VALUE));
        pokemonTypes = readArrayList(buffer.readString(Short.MAX_VALUE));
        regions = readArrayList(buffer.readString(Short.MAX_VALUE));
    }

    public String writeArrayList(ArrayList<String> list) {
        list.removeIf(Objects::isNull);
        list = new ArrayList<>(new LinkedHashSet<>(list));
        return String.join(",", list);
    }

    public ArrayList<String> readArrayList(String s) {
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(obj -> !obj.isEmpty() && !obj.contains("choice_any"))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        // Asserts that the client is in a world, something that always should be true when the config is opened.
        assert MinecraftClient.getInstance().world != null;
        DynamicRegistryManager registryManager = MinecraftClient.getInstance().world.getRegistryManager();

        List<String> actionList = Arrays.asList("catch", "defeat", "evolve", "kill", "level_up", "level_up_to", "release", "trade_away", "trade_for", "obtain", "select_starter","revive_fossil");
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
        List<String> biomesList = new ArrayList<>(registryManager.get(RegistryKeys.BIOME).getEntrySet().stream().map(entry -> entry.getKey().getValue().toString()).toList());
        addConfigList(config, "biomes", biomes, biomesList, null, biomeAndDimensionNameProcessor);

        List<String> dimensionsList = new ArrayList<>(registryManager.get(RegistryKeys.DIMENSION_TYPE).getEntrySet().stream().map(entry -> entry.getKey().getValue().toString()).toList());
        dimensionsList.remove("minecraft:overworld_caves");
        addConfigList(config, "dimensions", dimensions, dimensionsList, null, biomeAndDimensionNameProcessor);

        config.addLong("time_min", timeMin, v -> timeMin = v, 0L, 0L, 24000L).setNameKey(MOD_ID + ".task.time_min");
        config.addLong("time_max", timeMax, v -> timeMax = v, 24000L, 0L, 24000L).setNameKey(MOD_ID + ".task.time_max");
    }


    private void addConfigList(ConfigGroup config, String listName, List<String> listData, List<String> optionsList, Function<Identifier, Icon> iconProcessor, Function<String, String> nameProcessor) {
        NameMap<String> nameMap = NameMap.of(optionsList.get(0), optionsList)
                .id(s -> s)
                .name(s -> Text.translatable(nameProcessor == null ? MOD_ID + "." + listName + "." + s : nameProcessor.apply(s)))
                .icon(s -> iconProcessor == null ? pokeBallIcon : iconProcessor.apply(new Identifier(s)))
                .create();
        config.addList(listName, listData, new EnumConfig<>(nameMap), optionsList.get(optionsList.size()-1)).setNameKey(MOD_ID + ".task." + listName);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Text getAltTitle() {
        StringBuilder titleBuilder = new StringBuilder();
        for (String action : actions) {
            titleBuilder.append(Text.translatable("cobblemon_quests.actions." + action).getString()).append(" ");
        }
        titleBuilder.append(amount).append("x ");
        if (shiny) {
            titleBuilder.append(Text.translatable("cobblemon_quests.task.shiny").getString()).append(" ");
        }
        for (String gender : genders) {
            titleBuilder.append(Text.translatable("cobblemon_quests.genders." + gender).getString()).append(" ");
        }
        for (String form : forms) {
            titleBuilder.append(Text.translatable("cobblemon_quests.forms." + form).getString()).append(" ");
        }
        for (String region : regions) {
            titleBuilder.append(Text.translatable("cobblemon_quests.regions." + region).getString()).append(" ");
        }
        for (String pokemonType : pokemonTypes) {
            titleBuilder.append(Text.translatable("cobblemon.type." + pokemonType).getString()).append(" ");
        }
        if (pokemons.isEmpty()) {
            titleBuilder.append(Text.translatable("cobblemon_quests.task.pokemons").getString()).append(" ");
        } else {
            for (String pokemon : pokemons) {
                titleBuilder.append(Text.translatable("cobblemon.species." + pokemon.split(":")[1] + ".name").getString()).append(" ");
                if(pokemons.indexOf(pokemon) != pokemons.size() - 1) {
                    titleBuilder.append("or ");
                }
            }
        }
        for (String pokeballUsed : pokeBallsUsed) {
            if(pokeBallsUsed.indexOf(pokeballUsed) == 0) {
                titleBuilder.append("using a ");
            } else {
                titleBuilder.append("or ");
            }
            titleBuilder.append(Text.translatable("item." + pokeballUsed.replace(":", ".")).getString()).append(" ");
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

        return Text.of(titleBuilder.toString().trim());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getAltIcon() {
        if (pokemons.isEmpty()) {
            return pokeBallIcon;
        }
        return getPokemonIcon(new Identifier(pokemons.get(0)));
    }

    public Icon getIconFromIdentifier(Identifier identifier) {
        ItemStack itemStack = Registries.ITEM.get(identifier).getDefaultStack();
        if (itemStack.isEmpty()) {
            return pokeBallIcon;
        } else {
            return ItemIcon.getItemIcon(itemStack);
        }
    }

    public Icon getPokemonIcon(Identifier pokemon) {
        Item pokemonModelItem = Registries.ITEM.get(new Identifier("cobblemon", "pokemon_model"));
        NbtCompound nbt = new NbtCompound();
        nbt.putString("species", pokemon.toString());
        pokemonModelItem.getDefaultStack().setNbt(nbt);
        ItemStack stack = new ItemStack(pokemonModelItem);
        stack.setNbt(nbt);
        return ItemIcon.getItemIcon(stack);
    }

    public void CobblemonTaskIncrease(TeamData teamData, Pokemon pokemon, String executedAction, long progress, ServerPlayerEntity player) {

        String[] obtainingMethods = {"catch", "evolve", "trade_for", "obtain","revive_fossil"};
        if (CobblemonQuestsConfig.ignoredPokemon.contains(pokemon.getSpecies().toString().toLowerCase())) return;
        if (actions.contains(executedAction) || (actions.contains("obtain") && Arrays.asList(obtainingMethods).contains(executedAction))) {
            LivingEntity targetEntity = pokemon.getOwnerPlayer() != null ? pokemon.getOwnerPlayer() : pokemon.getEntity();
            if (targetEntity == null && player != null) {
                targetEntity = player;
            } else if (targetEntity == null) {
                throw new NullPointerException("The target entity is null. No player or pokemon entity was found. This will cause the quest to not update properly.\nExecuted action: " + executedAction);
            }

            // Check region
            if (!regions.isEmpty()) {
                if (!regions.contains(pokemon.getSpecies().getLabels().toArray()[0].toString())) {
                    return;
                }
            }

            // Check the time of action
            if (!(timeMin == 0 && timeMax == 24000)) {
                long timeOfDay = targetEntity.getEntityWorld().getTimeOfDay();
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
                if (!dimensions.contains(targetEntity.getEntityWorld().getRegistryKey().getValue().toString())) {
                    return;
                }
            }

            // Check biome
            if (!biomes.isEmpty()) {
                if (!biomes.contains(targetEntity.getEntityWorld().getBiome(targetEntity.getBlockPos()).getKey().get().getValue().toString())) {
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

            boolean shouldAddProgress = pokemons.stream().anyMatch(p -> p.split(":").length > 1 &&
                    p.split(":")[1].equals(pokemon.getSpecies().toString())) ||
                    pokemons.isEmpty();

            if (shouldAddProgress) {
                teamData.addProgress(this, progress);
            }
        }
    }
}
