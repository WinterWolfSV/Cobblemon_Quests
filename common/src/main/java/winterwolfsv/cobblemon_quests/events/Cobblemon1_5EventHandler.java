package winterwolfsv.cobblemon_quests.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.FossilRevivedEvent;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import net.minecraft.server.network.ServerPlayerEntity;
import winterwolfsv.cobblemon_quests.CobblemonQuests;

import java.util.Arrays;

public class Cobblemon1_5EventHandler {

    public Cobblemon1_5EventHandler init() {
        CobblemonEvents.FOSSIL_REVIVED.subscribe(Priority.LOWEST, this::fossilRevived);

        return new Cobblemon1_5EventHandler();
    }

    private Unit fossilRevived(FossilRevivedEvent fossilRevivedEvent) {
        try {
            ServerPlayerEntity player = fossilRevivedEvent.getPlayer();
            Pokemon pokemon = fossilRevivedEvent.getPokemon();
            System.out.println("Fossil Revived Event with player " + player.getName().getString() + " and pokemon " + pokemon.getDisplayName());
            CobblemonQuests.eventHandler.fossilRevivedHandler(player, pokemon);
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Error processing fossil revive event " + Arrays.toString(e.getStackTrace()));
        }
        return Unit.INSTANCE;
    }
}
