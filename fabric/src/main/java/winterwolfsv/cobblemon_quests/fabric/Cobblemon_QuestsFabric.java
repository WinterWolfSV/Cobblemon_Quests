package winterwolfsv.cobblemon_quests.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import winterwolfsv.cobblemon_quests.CobblemonQuests;
import net.fabricmc.api.ModInitializer;
import winterwolfsv.cobblemon_quests.fabric.config.ConfigCommandsFabric;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;

public class Cobblemon_QuestsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "v1.1.12"), new Item(new Item.Properties()));

        CobblemonQuests.init(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve(MOD_ID + ".config"),true);
        ConfigCommandsFabric.registerCommands();
    }
}