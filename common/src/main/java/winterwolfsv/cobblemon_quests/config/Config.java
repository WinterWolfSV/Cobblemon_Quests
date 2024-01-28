package winterwolfsv.cobblemon_quests.config;

import com.google.gson.*;
import winterwolfsv.cobblemon_quests.CobblemonQuests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.LOGGER;

// TODO Rewrite this mess to actually be usable
public class Config {
    private final Path configPath;
    private JsonObject configData;
    private DefaultConfig defaultConfig;

    public Config(DefaultConfig defaultConfig) {
        this.configPath = CobblemonQuests.configPath;
        this.defaultConfig = defaultConfig;
        loadConfig(defaultConfig);
    }

    private void initializeConfig() {
        File configFile = new File(configPath.toUri());
        File configDir = new File(configPath.getParent().toUri());
        if (!configDir.exists()) {
            try {
                Files.createDirectory(configPath.getParent());
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to create config directory. Please report this with a log file to WinterWolfSV e1 " + Arrays.toString(e.getStackTrace()));
            }
        }
        if (!configFile.exists()) {
            try {
                Files.createFile(configPath);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to create config file. Please report this with a log file to WinterWolfSV e2 " + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private void loadConfig(DefaultConfig defaultConfig) {
        initializeConfig();
        try {
            InputStream configStream = Files.newInputStream(configPath);
            JsonElement root = JsonParser.parseReader(new InputStreamReader(configStream));
            try {
                configData = root.getAsJsonObject();
            } catch (IllegalStateException e) {
                configData = new JsonObject();
                LOGGER.log(Level.INFO, "Config file is empty. Creating new config file.");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load config file. Please report this with a log file to WinterWolfSV e3 " + Arrays.toString(e.getStackTrace()));
            }
            configStream.close();

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error IOException ccq" + Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load config file. Please report this with a log file to WinterWolfSV e4 " + Arrays.toString(e.getStackTrace()));
        }


        HashMap<String, Object> defaultConfigValues = defaultConfig.getConfigValues();

        for (String key : defaultConfigValues.keySet()) {
            if (configData.get(key) == null) {
                setConfigValue(key, defaultConfigValues.get(key));
            }
        }

        // Check if each value in the config exsists in the default config file:

        Object[] defaultConfigKeys = configData.keySet().toArray();
        for (int i = 0; i < defaultConfigKeys.length; i++) {
            String key = defaultConfigKeys[i].toString();

            if (defaultConfigValues.get(key) == null) {
                LOGGER.log(Level.INFO, "Config value " + key + " is not in the default config file. Cleaning up...");
                configData.remove(key);
            }
        }
        saveConfig();
    }

    public void saveConfig() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String prettyJson = gson.toJson(configData);
            Files.write(configPath, prettyJson.getBytes());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save config file. Please report this with a log file to WinterWolfSV " + Arrays.toString(e.getStackTrace()));
        }
    }

    public float getConfigFloat(String key) {
        try {
            return configData.get(key).getAsFloat();
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    public boolean getConfigBool(String key) {
        return configData.get(key).getAsBoolean();
    }

    public String getConfigString(String key) {
        return configData.get(key).getAsString();
    }

    public List<String> getConfigList(String key) {
        JsonArray jsonArray = configData.getAsJsonArray(key);
        return jsonArrayToList(jsonArray);
    }


    private List<String> jsonArrayToList(JsonArray jsonArray) {
        List<String> list = new java.util.ArrayList<>(jsonArray.size());
        for (JsonElement jsonElement : jsonArray) {
            list.add(jsonElement.getAsString());
        }
        return list;
    }


    public void setConfigValue(String key, Object value) {
        if (value instanceof Number) {
            configData.addProperty(key, (Number) value);
        } else if (value instanceof Boolean) {
            configData.addProperty(key, (boolean) value);
        } else if (value instanceof String) {
            configData.addProperty(key, (String) value);
        } else if (value instanceof List<?>) {
            JsonArray jsonArray = new JsonArray();
            for (Object object : (List<?>) value) {
                if (object instanceof Number) {
                    jsonArray.add((Number) object);
                } else if (object instanceof Boolean) {
                    jsonArray.add((boolean) object);
                } else if (object instanceof String) {
                    jsonArray.add((String) object);
                }
            }
            configData.add(key, jsonArray);
        }
        saveConfig();
    }

    public void addConfigValueStringToList(String listKey, String value) {

        configData.getAsJsonArray(listKey).add(value);
        saveConfig();
    }

    public void removeConfigValueFromList(String listKey, String value) {
        JsonArray jsonArray = configData.getAsJsonArray(listKey);
        JsonElement jsonElement = new JsonPrimitive(value);
        jsonArray.remove(jsonElement);
        saveConfig();
    }

    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

}
