package winterwolfsv.cobblemon_quests.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
//import com.cobblemon.mod.common.api.events.pokemon.TradeCompletedEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionAcceptedEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent;
import com.cobblemon.mod.common.api.events.starter.StarterChosenEvent;
//import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.ftb.mods.ftbquests.quest.QuestFile;
import dev.ftb.mods.ftbquests.events.ClearFileCacheEvent;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbteams.data.Team;
import dev.ftb.mods.ftbteams.data.TeamManager;
import kotlin.Unit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
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
//        CobblemonEvents.TRADE_COMPLETED.subscribe(Priority.LOWEST, this::pokemonTrade);
//        CobblemonEvents.POKEMON_RELEASED_EVENT_PRE.subscribe(Priority.LOWEST, this::pokemonRelease);
        return this;
    }

    private void fileCacheClear(QuestFile file) {
        if (file.isServerSide()) {
            pokemonTasks = null;
        }
    }

//    private Unit pokemonRelease(ReleasePokemonEvent.Pre pre) {
//        try {
//            ServerPlayerEntity player = pre.getPlayer();
//            Pokemon pokemon = pre.getPokemon();
//
//            if (this.pokemonTasks == null) {
//                this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
//            }
//            if (this.pokemonTasks.isEmpty()) return Unit.INSTANCE;
//
//            Team team = TeamManager.INSTANCE.getPlayerTeam(player);
//            if (team == null) return Unit.INSTANCE;
//            TeamData data = ServerQuestFile.INSTANCE.getData(team);
//            processTasksForTeam(data, pokemon, "release", 1, null);
//
//        } catch (Exception e) {
//            CobblemonQuests.LOGGER.warning("Error processing release event " + Arrays.toString(e.getStackTrace()));
//        }
//        return Unit.INSTANCE;
//    }

    /**
     * Player 1 gives pokemon 1 to player 2
     * Player 2 gives pokemon 2 to player 1
     */
//    private Unit pokemonTrade(TradeCompletedEvent tradeCompletedEvent) {
//        try {
//            UUID playerUuid1 = tradeCompletedEvent.getTradeParticipant1().getUuid();
//            UUID playerUuid2 = tradeCompletedEvent.getTradeParticipant2().getUuid();
//            Pokemon pokemonGivenByPlayer1 = tradeCompletedEvent.getTradeParticipant2Pokemon();
//            Pokemon pokemonGivenByPlayer2 = tradeCompletedEvent.getTradeParticipant1Pokemon();
//
//            if (this.pokemonTasks == null) {
//                this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
//            }
//
//            Team teamPlayer1 = TeamManager.INSTANCE.getTeamByID(playerUuid1);
//            Team teamPlayer2 = TeamManager.INSTANCE.getTeamByID(playerUuid2);
//
//            TeamData dataPlayer1 = ServerQuestFile.INSTANCE.getData(teamPlayer1);
//            TeamData dataPlayer2 = ServerQuestFile.INSTANCE.getData(teamPlayer2);
//
//            processTasksForTeam(dataPlayer1, pokemonGivenByPlayer2, "trade_for", 1, null);
//            processTasksForTeam(dataPlayer1, pokemonGivenByPlayer1, "trade_away", 1, null);
//            processTasksForTeam(dataPlayer2, pokemonGivenByPlayer1, "trade_for", 1, null);
//            processTasksForTeam(dataPlayer2, pokemonGivenByPlayer2, "trade_away", 1, null);
//        } catch (Exception e) {
//            CobblemonQuests.LOGGER.warning("Error processing trade event " + Arrays.toString(e.getStackTrace()));
//        }
//        return Unit.INSTANCE;
//    }


    private Unit pokemonBattleVictory(BattleVictoryEvent battleVictoryEvent) {
        try {
            if (pokemonTasks == null) {
                pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
            }
            if (pokemonTasks.isEmpty()) return Unit.INSTANCE;

            List<ServerPlayerEntity> players = battleVictoryEvent.getBattle().getPlayers();
            if (players.size() != 1) return Unit.INSTANCE;

            ServerPlayerEntity player = players.get(0);
            if (!player.getName().equals(battleVictoryEvent.getWinners().get(0).getName())) return Unit.INSTANCE;

            Team team = TeamManager.INSTANCE.getPlayerTeam(player);
            if (team == null) return Unit.INSTANCE;
            TeamData data = ServerQuestFile.INSTANCE.getData(team);

            Iterable<BattleActor> battleActors = battleVictoryEvent.getBattle().getActors();
            for (BattleActor actor : battleActors) {
                // Checks if the pokemon is the last pokemon that was caught. Done to bypass an issue with two events being
                // fired for the same pokemon and adding progress to catch and defeat tasks.
                if (actor.getPokemonList().get(0).getEffectedPokemon().getUuid() == lastPokemonUuid)
                    return Unit.INSTANCE;
                if (actor != battleVictoryEvent.getWinners().get(0)) {
                    processTasksForTeam(data, actor.getPokemonList().get(0).getEffectedPokemon(), "defeat", 1, null);
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

    private EventResult entityKill(LivingEntity livingEntity, DamageSource damageSource) {
        try {
            if (damageSource.getAttacker() instanceof ServerPlayerEntity player && !PlayerHooks.isFake(player)) {
                if (pokemonTasks == null) {
                    pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
                }

                if (pokemonTasks.isEmpty()) {
                    return EventResult.pass();
                }
                Pokemon pokemon = livingEntity instanceof PokemonEntity ? ((PokemonEntity) livingEntity).getPokemon() : null;
                if (pokemon == null) return EventResult.pass();

                Team team = TeamManager.INSTANCE.getPlayerTeam(player);
                if (team == null) return EventResult.pass();
                TeamData data = ServerQuestFile.INSTANCE.getData(team);
                processTasksForTeam(data, pokemon, "kill", 1, null);
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing entity kill event " + Arrays.toString(e.getStackTrace()));
        }
        return EventResult.pass();
    }

    public Unit pokemonCatch(Pokemon pokemon, PlayerEntity player) {
        try {
            if (this.pokemonTasks == null) {
                this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
            }
            if (this.pokemonTasks.isEmpty()) return Unit.INSTANCE;

            Team team = TeamManager.INSTANCE.getPlayerTeam((ServerPlayerEntity) player);
            if (team == null) return Unit.INSTANCE;
            TeamData data = ServerQuestFile.INSTANCE.getData(team);

            processTasksForTeam(data, pokemon, "catch", 1, null);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing catch event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }

    public void pokemonObtain(Pokemon pokemon, PlayerEntity player) {
        try {
            if (this.pokemonTasks == null) {
                this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
            }
            if (this.pokemonTasks.isEmpty()) return;

            Team team = TeamManager.INSTANCE.getPlayerTeam((ServerPlayerEntity) player);
            if (team == null) return;
            TeamData data = ServerQuestFile.INSTANCE.getData(team);

            processTasksForTeam(data, pokemon, "obtain", 1, null);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing obtain event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private Unit pokemonStarterChosen(StarterChosenEvent starterChosenEvent) {
        try {
            if (this.pokemonTasks == null) {
                this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
            }
            if (this.pokemonTasks.isEmpty()) return Unit.INSTANCE;
            ServerPlayerEntity player = starterChosenEvent.getPlayer();
            Pokemon pokemon = starterChosenEvent.getPokemon();

            Team team = TeamManager.INSTANCE.getPlayerTeam(player);
            if (team == null) return Unit.INSTANCE;
            TeamData data = ServerQuestFile.INSTANCE.getData(team);

            processTasksForTeam(data, pokemon, "select_starter", 1, player);
            processTasksForTeam(data, pokemon, "catch", 1, player);
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
            if (this.pokemonTasks == null) {
                this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
            }
            if (this.pokemonTasks.isEmpty()) return Unit.INSTANCE;
            Pokemon pokemon = evolutionAcceptedEvent.getPokemon();

            Team team = TeamManager.INSTANCE.getPlayerTeam(pokemon.getOwnerPlayer());
            if (team == null) return Unit.INSTANCE;
            TeamData data = ServerQuestFile.INSTANCE.getData(team);

            processTasksForTeam(data, pokemon, "evolve", 1, null);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing evolution event " + Arrays.toString(e.getStackTrace()));
        }

        return Unit.INSTANCE;
    }


    private Unit pokemonLevelUp(LevelUpEvent levelUpEvent) {
        try {
            if (this.pokemonTasks == null) {
                this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
            }
            if (this.pokemonTasks.isEmpty()) return Unit.INSTANCE;

            ServerPlayerEntity player = levelUpEvent.getPokemon().getOwnerPlayer();
            Pokemon pokemon = levelUpEvent.getPokemon();

            if (player == null) return Unit.INSTANCE;

            long deltaLevel = levelUpEvent.getNewLevel() - levelUpEvent.getOldLevel();

            Team team = TeamManager.INSTANCE.getPlayerTeam(player);
            if (team == null) return Unit.INSTANCE;
            TeamData data = ServerQuestFile.INSTANCE.getData(team);

            processTasksForTeam(data, pokemon, "level_up", deltaLevel, null);

        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing level up event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }

    public void fossilRevivedHandler(ServerPlayerEntity player, Pokemon pokemon) {
        try {
            if (this.pokemonTasks == null) {
                this.pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
            }
            if (this.pokemonTasks.isEmpty()) return;

            Team team = TeamManager.INSTANCE.getPlayerTeam(player);
            if (team == null) return;
            TeamData data = ServerQuestFile.INSTANCE.getData(team);

            processTasksForTeam(data, pokemon, "revive_fossil", 1, player);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing fossil revive event " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void processTasksForTeam(TeamData data, Pokemon pokemon, String action, long amount, ServerPlayerEntity player) {
        try {
            for (CobblemonTask task : pokemonTasks) {
                if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                    task.CobblemonTaskIncrease(data, pokemon, action, amount, player);
                }
            }
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing task for team " + Arrays.toString(e.getStackTrace()));
        }
    }
}