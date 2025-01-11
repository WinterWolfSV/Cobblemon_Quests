package winterwolfsv.cobblemon_quests.tasks;

import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskData {
    public static final List<String> formList = List.of("normal",
            "alolan",
            "galarian",
            "paldean",
            "hisuian",
            "magikarp-jump-apricot-stripes",
            "magikarp-jump-apricot-tiger",
            "magikarp-jump-apricot-zebra",
            "magikarp-jump-black-forehead",
            "magikarp-jump-black-mask",
            "magikarp-jump-blue-raindrops",
            "magikarp-jump-blue-saucy",
            "magikarp-jump-brown-stripes",
            "magikarp-jump-brown-tiger",
            "magikarp-jump-brown-zebra",
            "magikarp-jump-calico-orange-gold",
            "magikarp-jump-calico-orange-white",
            "magikarp-jump-calico-orange-white-black",
            "magikarp-jump-calico-white-orange",
            "magikarp-jump-gray-bubbles",
            "magikarp-jump-gray-diamonds",
            "magikarp-jump-gray-patches",
            "magikarp-jump-orange-dapples",
            "magikarp-jump-orange-forehead",
            "magikarp-jump-orange-mask",
            "magikarp-jump-orange-orca",
            "magikarp-jump-orange-two-tone",
            "magikarp-jump-pink-dapples",
            "magikarp-jump-pink-orca",
            "magikarp-jump-pink-two-tone",
            "magikarp-jump-purple-bubbles",
            "magikarp-jump-purple-diamonds",
            "magikarp-jump-purple-patches",
            "magikarp-jump-skelly",
            "magikarp-jump-violet-raindrops",
            "magikarp-jump-violet-saucy"
    );
    public static final List<String> actionList = Arrays.asList("catch", "defeat", "evolve", "kill", "level_up", "level_up_to", "release", "trade_away", "trade_for", "obtain", "select_starter", "revive_fossil");
    public static final List<String> pokemonList = new ArrayList<>(PokemonSpecies.INSTANCE.getSpecies().stream().map(species -> species.resourceIdentifier.toString()).sorted().toList());
    public static final List<String> pokeBallList = new ArrayList<>(PokeBalls.INSTANCE.all().stream().map(pokeBall -> pokeBall.getName().toString()).sorted().toList());
    public static final List<String> genderList = Arrays.asList("male", "female", "genderless");
    public static final List<String> pokemonTypeList = Arrays.asList("normal", "fire", "water", "grass", "electric", "ice", "fighting", "poison", "ground", "flying", "psychic", "bug", "rock", "ghost", "dragon", "dark", "steel", "fairy");
    public static final List<String> regionList = Arrays.asList("gen1", "gen2", "gen3", "gen4", "gen5", "gen6", "gen7", "gen8", "gen9");


}
