package winterwolfsv.cobblemon_quests.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;


public interface DefaultConfig {
    // All private final fields will be added to the config file. These fields must be primitive types or Strings.

    // Check if the mod is in fabric or forge

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
