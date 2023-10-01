package winterwolfsv.cobblemon_quests.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.ftb.mods.ftbquests.events.ClearFileCacheEvent;
import dev.ftb.mods.ftbquests.quest.QuestFile;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
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

    public void init() {
        EntityEvent.LIVING_DEATH.register(this::entityKill);
        ClearFileCacheEvent.EVENT.register(this::fileCacheClear);
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, this::pokemonCatch);
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.LOWEST, this::pokemonBattleVictory);

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

        PlayerEntity player = players.get(0);
        if (!player.getName().equals(battleVictoryEvent.getWinners().get(0).getName())) return Unit.INSTANCE;

        TeamData data = ServerQuestFile.INSTANCE.getNullableTeamData(player.getUuid());
        Iterable<BattleActor> battleActors = battleVictoryEvent.getBattle().getActors();
        for (BattleActor actor : battleActors) {
            // Checks if the pokemon is the last pokemon that was caught. Done to bypass an issue with two events being
            // fired for the same pokemon and adding progress to catch and defeat tasks.
            if (actor.getPokemonList().get(0).getEffectedPokemon().getUuid() == lastPokemonUuid) return Unit.INSTANCE;
            if (actor != battleVictoryEvent.getWinners().get(0)) {
                for (CobblemonTask task : pokemonTasks) {
                    if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                        task.CobblemonTaskIncrease(data, actor.getPokemonList().get(0).getEffectedPokemon(), "defeat");
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

        TeamData data = ServerQuestFile.INSTANCE.getNullableTeamData(pokemonCapturedEvent.getPlayer().getUuid());
        for (CobblemonTask task : pokemonTasks) {
            if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                task.CobblemonTaskIncrease(data, pokemonCapturedEvent.getPokemon(), "catch");
            }
        }
        return Unit.INSTANCE;
    }

    private EventResult entityKill(LivingEntity livingEntity, DamageSource damageSource) {
        if (damageSource.getAttacker() instanceof PlayerEntity player && !PlayerHooks.isFake(player)) {
            if (pokemonTasks == null) {
                pokemonTasks = ServerQuestFile.INSTANCE.collect(CobblemonTask.class);
            }

            if (pokemonTasks.isEmpty()) {
                return EventResult.pass();
            }

            TeamData data = ServerQuestFile.INSTANCE.getData(player);

            for (CobblemonTask task : pokemonTasks) {
                if (data.getProgress(task) < task.getMaxProgress() && data.canStartTasks(task.quest)) {
                    // Checks if the entity is a pokémon
                    if(livingEntity instanceof PokemonEntity pokemon){
                        // Calls the appropriate method to add progress to the task
//                        task.pokemonKill(data, pokemon.getPokemon());
                        task.CobblemonTaskIncrease(data, pokemon.getPokemon(), "kill");
                    }
                }
            }
        }
        return EventResult.pass();
    }


}
