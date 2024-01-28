package winterwolfsv.cobblemon_quests.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionAcceptedEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent;
import com.cobblemon.mod.common.api.events.starter.StarterChosenEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.ftb.mods.ftbquests.api.QuestFile;
import dev.ftb.mods.ftbquests.events.ClearFileCacheEvent;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.data.TeamManagerImpl;
import kotlin.Unit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import winterwolfsv.cobblemon_quests.tasks.CobblemonTask;

import java.util.List;
import java.util.UUID;


public class FTBCobblemonEventHandler {
    private List<CobblemonTask> pokemonTasks = null;
    private UUID lastPokemonUuid = null;

    public FTBCobblemonEventHandler init() {
        EntityEvent.LIVING_DEATH.register(this::entityKill);
        ClearFileCacheEvent.EVENT.register(this::fileCacheClear);
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, this::pokemonCatch);
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.LOWEST, this::pokemonBattleVictory);
        CobblemonEvents.STARTER_CHOSEN.subscribe(Priority.LOWEST, this::pokemonStarterChosen);
        CobblemonEvents.EVOLUTION_COMPLETE.subscribe(Priority.LOWEST, this::pokemonEvolutionComplete);
        CobblemonEvents.LEVEL_UP_EVENT.subscribe(Priority.LOWEST, this::pokemonLevelUp);
        CobblemonEvents.EVOLUTION_ACCEPTED.subscribe(Priority.LOWEST, this::pokemonEvolutionAccepted);
        return this;
    }

    private void fileCacheClear(QuestFile file) {
        if (file.isServerSide()) {
            pokemonTasks = null;
        }
    }

    private Unit pokemonBattleVictory(BattleVictoryEvent battleVictoryEvent) {
        if (pokemonTasks == null) {
            pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
        }
        if (pokemonTasks.isEmpty()) return Unit.INSTANCE;

        List<ServerPlayerEntity> players = battleVictoryEvent.getBattle().getPlayers();
        if (players.size() != 1) return Unit.INSTANCE;

        ServerPlayerEntity player = players.get(0);
        if (!player.getName().equals(battleVictoryEvent.getWinners().get(0).getName())) return Unit.INSTANCE;

        Team team = TeamManagerImpl.INSTANCE.getTeamForPlayer(player).orElse(null);
        if (team == null) return Unit.INSTANCE;
        TeamData data = ServerQuestFile.INSTANCE.getOrCreateTeamData(team);

        Iterable<BattleActor> battleActors = battleVictoryEvent.getBattle().getActors();
        for (BattleActor actor : battleActors) {
            // Checks if the pokemon is the last pokemon that was caught. Done to bypass an issue with two events being
            // fired for the same pokemon and adding progress to catch and defeat tasks.
            if (actor.getPokemonList().get(0).getEffectedPokemon().getUuid() == lastPokemonUuid) return Unit.INSTANCE;
            if (actor != battleVictoryEvent.getWinners().get(0)) {
                for (CobblemonTask task : pokemonTasks) {
                    if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.getQuest())) {
                        task.CobblemonTaskIncrease(data, actor.getPokemonList().get(0).getEffectedPokemon(), "defeat", 1);
                    }
                }
            }
        }
        return Unit.INSTANCE;
    }

    private Unit pokemonCatch(PokemonCapturedEvent pokemonCapturedEvent) {
        lastPokemonUuid = pokemonCapturedEvent.getPokemon().getUuid();

        if (this.pokemonTasks == null) {
            this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
        }
        if (this.pokemonTasks.isEmpty()) return Unit.INSTANCE;

        Team team = TeamManagerImpl.INSTANCE.getTeamForPlayer(pokemonCapturedEvent.getPlayer()).orElse(null);
        if (team == null) return Unit.INSTANCE;
        TeamData data = ServerQuestFile.INSTANCE.getOrCreateTeamData(team);

        for (CobblemonTask task : pokemonTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.getQuest())) {
                task.CobblemonTaskIncrease(data, pokemonCapturedEvent.getPokemon(), "catch", 1);
            }
        }
        return Unit.INSTANCE;
    }

    private EventResult entityKill(LivingEntity livingEntity, DamageSource damageSource) {
        if (damageSource.getAttacker() instanceof ServerPlayerEntity player && !PlayerHooks.isFake(player)) {
            if (pokemonTasks == null) {
                pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
            }

            if (pokemonTasks.isEmpty()) {
                return EventResult.pass();
            }

            Team team = TeamManagerImpl.INSTANCE.getTeamForPlayer(player).orElse(null);
            if (team == null) return EventResult.pass();
            TeamData data = ServerQuestFile.INSTANCE.getOrCreateTeamData(team);

            for (CobblemonTask task : pokemonTasks) {
                if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.getQuest())) {
                    // Checks if the entity is a pokémon
                    if (livingEntity instanceof PokemonEntity pokemon) {
                        task.CobblemonTaskIncrease(data, pokemon.getPokemon(), "kill", 1);
                    }
                }
            }
        }
        return EventResult.pass();
    }

    public Unit pokemonCatch(Pokemon pokemon, PlayerEntity player) {
        if (this.pokemonTasks == null) {
            this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
        }
        if (this.pokemonTasks.isEmpty()) return Unit.INSTANCE;

        Team team = TeamManagerImpl.INSTANCE.getTeamForPlayer((ServerPlayerEntity) player).orElse(null);
        if (team == null) return Unit.INSTANCE;
        TeamData data = ServerQuestFile.INSTANCE.getOrCreateTeamData(team);

        for (CobblemonTask task : pokemonTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.getQuest())) {
                task.CobblemonTaskIncrease(data, pokemon, "catch", 1);
            }
        }
        return Unit.INSTANCE;
    }

    private Unit pokemonStarterChosen(StarterChosenEvent starterChosenEvent) {
        return pokemonCatch(starterChosenEvent.getPokemon(), starterChosenEvent.getPlayer());
    }

    private Unit pokemonEvolutionComplete(EvolutionCompleteEvent evolutionCompleteEvent) {
        Pokemon pokemon = evolutionCompleteEvent.getPokemon();
        return pokemonCatch(pokemon, pokemon.getOwnerPlayer());
    }

    private Unit pokemonEvolutionAccepted(EvolutionAcceptedEvent evolutionAcceptedEvent) {
        if (this.pokemonTasks == null) {
            this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
        }
        if (this.pokemonTasks.isEmpty()) return Unit.INSTANCE;
        Pokemon pokemon = evolutionAcceptedEvent.getPokemon();

        Team team = TeamManagerImpl.INSTANCE.getTeamForPlayer(pokemon.getOwnerPlayer()).orElse(null);
        if (team == null) return Unit.INSTANCE;
        TeamData data = ServerQuestFile.INSTANCE.getOrCreateTeamData(team);

        for (CobblemonTask task : pokemonTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.getQuest())) {
                task.CobblemonTaskIncrease(data, pokemon, "evolve", 1);
            }
        }

        return Unit.INSTANCE;
    }


    private Unit pokemonLevelUp(LevelUpEvent levelUpEvent) {
        if (this.pokemonTasks == null) {
            this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
        }
        if (this.pokemonTasks.isEmpty()) return Unit.INSTANCE;

        ServerPlayerEntity player = levelUpEvent.getPokemon().getOwnerPlayer();
        Pokemon pokemon = levelUpEvent.getPokemon();

        if (player == null) return Unit.INSTANCE;

        long deltaLevel = levelUpEvent.getNewLevel() - levelUpEvent.getOldLevel();

        Team team = TeamManagerImpl.INSTANCE.getTeamForPlayer(player).orElse(null);
        if (team == null) return Unit.INSTANCE;
        TeamData data = ServerQuestFile.INSTANCE.getOrCreateTeamData(team);

        for (CobblemonTask task : pokemonTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.getQuest())) {
                task.CobblemonTaskIncrease(data, pokemon, "level_up", deltaLevel);
            }
        }

        return Unit.INSTANCE;
    }
}