package winterwolfsv.cobblemon_quests.neoforge;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import winterwolfsv.cobblemon_quests.commands.arguments.types.ActionListArgumentType;
import winterwolfsv.cobblemon_quests.neoforge.config.ConfigCommandsForge;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;

@Mod(MOD_ID)
public class Cobblemon_QuestsNeoForge {
    public Cobblemon_QuestsNeoForge(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(ConfigCommandsForge.class);
        CobblemonQuests.init(FMLPaths.CONFIGDIR.get().resolve(MOD_ID).resolve(MOD_ID + ".config"), true);

        DeferredRegister<ArgumentTypeInfo<?, ?>> deferredRegister = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, MOD_ID);
        deferredRegister.register("argument_list", () -> ArgumentTypeInfos.registerByClass(
                ActionListArgumentType.class,
                SingletonArgumentInfo.contextFree(ActionListArgumentType::actionList)));
        deferredRegister.register(modEventBus);

    }
}