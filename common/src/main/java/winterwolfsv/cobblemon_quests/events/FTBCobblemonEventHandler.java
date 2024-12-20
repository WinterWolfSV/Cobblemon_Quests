package winterwolfsv.cobblemon_quests.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.events.pokemon.TradeCompletedEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionAcceptedEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent;
import com.cobblemon.mod.common.api.events.starter.StarterChosenEvent;
import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import winterwolfsv.cobblemon_quests.tasks.CobblemonTask;

import java.util.Arrays;
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
        CobblemonEvents.TRADE_COMPLETED.subscribe(Priority.LOWEST, this::pokemonTrade);
        CobblemonEvents.POKEMON_RELEASED_EVENT_PRE.subscribe(Priority.LOWEST, this::pokemonRelease);
        return this;
    }

    private void fileCacheClear(QuestFile file) {
        if (file.isServerSide()) {
            pokemonTasks = null;
        }
    }

    private Unit pokemonRelease(ReleasePokemonEvent.Pre pre) {
        try {
            ServerPlayer player = pre.getPlayer();
            Pokemon pokemon = pre.getPokemon();
            processTasksForTeam(pokemon, "release", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing release event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }

    /**
     * Player 1 gives pokemon 1 to player 2
     * Player 2 gives pokemon 2 to player 1
     */
    private Unit pokemonTrade(TradeCompletedEvent tradeCompletedEvent) {
        try {
            Pokemon pokemonGivenByPlayer1 = tradeCompletedEvent.getTradeParticipant2Pokemon();
            Pokemon pokemonGivenByPlayer2 = tradeCompletedEvent.getTradeParticipant1Pokemon();
            ServerPlayer player1 = pokemonGivenByPlayer2.getOwnerPlayer();
            ServerPlayer player2 = pokemonGivenByPlayer1.getOwnerPlayer();
            processTasksForTeam(pokemonGivenByPlayer2, "trade_for", 1, player1);
            processTasksForTeam(pokemonGivenByPlayer1, "trade_away", 1, player1);
            processTasksForTeam(pokemonGivenByPlayer1, "trade_for", 1, player2);
            processTasksForTeam(pokemonGivenByPlayer2, "trade_away", 1, player2);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing trade event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }

    private Unit pokemonBattleVictory(BattleVictoryEvent battleVictoryEvent) {
        try {
            List<ServerPlayer> players = battleVictoryEvent.getBattle().getPlayers();
            // Ensures that the battle is only player vs npc and not player vs player
            if (players.size() != 1) return Unit.INSTANCE;
            ServerPlayer player = players.get(0);
            if (!player.getName().equals(battleVictoryEvent.getWinners().get(0).getName())) return Unit.INSTANCE;
            Iterable<BattleActor> battleActors = battleVictoryEvent.getBattle().getActors();
            for (BattleActor actor : battleActors) {
                // Checks if the pokemon is the last pokemon that was caught. Done to bypass an issue with two events being
                // fired for the same pokemon and adding progress to catch and defeat tasks.
                if (actor.getPokemonList().get(0).getEffectedPokemon().getUuid() == lastPokemonUuid)
                    return Unit.INSTANCE;
                if (actor != battleVictoryEvent.getWinners().get(0)) {
                    processTasksForTeam(actor.getPokemonList().get(0).getEffectedPokemon(), "defeat", 1, player);
                }
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing battle victory event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }

    private Unit pokemonCatch(PokemonCapturedEvent pokemonCapturedEvent) {
        lastPokemonUuid = pokemonCapturedEvent.getPokemon().getUuid();
        return pokemonCatch(pokemonCapturedEvent.getPokemon(), pokemonCapturedEvent.getPlayer());
    }

    private EventResult entityKill(Entity livingEntity, DamageSource damageSource) {
        try {
            if (damageSource.getEntity() instanceof ServerPlayer player && !PlayerHooks.isFake(player)) {
                Pokemon pokemon = livingEntity instanceof PokemonEntity ? ((PokemonEntity) livingEntity).getPokemon() : null;
                if (pokemon == null) return EventResult.pass();
                processTasksForTeam(pokemon, "kill", 1, player);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing entity kill event " + Arrays.toString(e.getStackTrace()));
        }
        return EventResult.pass();
    }

    public Unit pokemonCatch(Pokemon pokemon, ServerPlayer player) {
        try {
            processTasksForTeam(pokemon, "catch", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing catch event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }

    public void pokemonObtain(Pokemon pokemon, ServerPlayer player) {
        try {
            processTasksForTeam(pokemon, "obtain", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing obtain event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private Unit pokemonStarterChosen(StarterChosenEvent starterChosenEvent) {
        try {
            ServerPlayer player = starterChosenEvent.getPlayer();
            Pokemon pokemon = starterChosenEvent.getPokemon();
            processTasksForTeam(pokemon, "select_starter", 1, player);
            pokemonCatch(pokemon, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing starter chosen event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }

    private Unit pokemonEvolutionComplete(EvolutionCompleteEvent evolutionCompleteEvent) {
        try {
            Pokemon pokemon = evolutionCompleteEvent.getPokemon();
            return pokemonCatch(pokemon, pokemon.getOwnerPlayer());
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing evolution complete event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }

    private Unit pokemonEvolutionAccepted(EvolutionAcceptedEvent evolutionAcceptedEvent) {
        try {
            Pokemon pokemon = evolutionAcceptedEvent.getPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            processTasksForTeam(pokemon, "evolve", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing evolution event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }

    private Unit pokemonLevelUp(LevelUpEvent levelUpEvent) {
        try {
            ServerPlayer player = levelUpEvent.getPokemon().getOwnerPlayer();
            Pokemon pokemon = levelUpEvent.getPokemon();
            long deltaLevel = levelUpEvent.getNewLevel() - levelUpEvent.getOldLevel();
            System.out.println("Old level: " + levelUpEvent.getOldLevel() + " New level: " + levelUpEvent.getNewLevel() + " Delta level: " + deltaLevel);
            processTasksForTeam(pokemon, "level_up_to", levelUpEvent.getNewLevel(), player);
            processTasksForTeam(pokemon, "level_up", deltaLevel, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing level up event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }

    public void fossilRevivedHandler(ServerPlayer player, Pokemon pokemon) {
        try {
            processTasksForTeam(pokemon, "revive_fossil", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing fossil revive event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void processTasksForTeam(Pokemon pokemon, String action, long amount, ServerPlayer player) {
        try {
            if (this.pokemonTasks == null) {
                this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
            }
            if (this.pokemonTasks.isEmpty()) return;
            Team team = TeamManagerImpl.INSTANCE.getTeamForPlayer(player).orElse(null);
            if (team == null) return;
            TeamData data = ServerQuestFile.INSTANCE.getOrCreateTeamData(team);
            for (CobblemonTask task : pokemonTasks) {
                if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.getQuest())) {
                    task.CobblemonTaskIncrease(data, pokemon, action, amount, player);
                }
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing task for team " + Arrays.toString(e.getStackTrace()));
        }
    }
}