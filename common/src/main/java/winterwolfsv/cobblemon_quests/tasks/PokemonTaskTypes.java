package winterwolfsv.cobblemon_quests.tasks;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.util.Identifier;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;

public interface PokemonTaskTypes {

    Identifier icon = new Identifier(MOD_ID, "textures/item/poke_ball_icon.png");
    TaskType COBBLEMON = TaskTypes.register(new Identifier("cobblemon_tasks", "cobblemon_task"), CobblemonTask::new, () -> Icon.getIcon(icon));

    static void init() {
    }
}
