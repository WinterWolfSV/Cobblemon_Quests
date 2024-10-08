package winterwolfsv.cobblemon_quests.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import net.fabricmc.api.ModInitializer;
import winterwolfsv.cobblemon_quests.fabric.config.ConfigCommandsFabric;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;

public class Cobblemon_QuestsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "v1.1.11"), new Item(new Item.Settings()));
        CobblemonQuests.init(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve(MOD_ID + ".config"),true);
        ConfigCommandsFabric.registerCommands();
    }
}