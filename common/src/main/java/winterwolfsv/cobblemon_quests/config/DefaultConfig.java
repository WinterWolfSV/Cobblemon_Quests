package winterwolfsv.cobblemon_quests.config;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.MOD_ID;


public interface DefaultConfig {
    // All private final fields will be added to the config file. These fields must be primitive types or Strings.
    Path CONFIG_FILE_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve(MOD_ID + "_config.json");

    default HashMap<String, Object> getConfigValues() {
        Field[] fields = this.getClass().getDeclaredFields();
        HashMap<String, Object> configValues = new HashMap<>();
        for (Field field : fields) {
            try {
                if (field.getModifiers() == Modifier.FINAL) {
                    configValues.put(field.getName(), field.get(this));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return configValues;
    }

    default HashMap<String, Object> getConfigTypes() {
        Field[] fields = this.getClass().getDeclaredFields();
        HashMap<String, Object> configTypes = new HashMap<>();
        for (Field field : fields) {
            if (field.getModifiers() == Modifier.FINAL) {
                configTypes.put(field.getName(), field.getType().getSimpleName());
            }
        }

        return configTypes;

    }
}
