package winterwolfsv.cobblemon_quests.config;

import org.yaml.snakeyaml.Yaml;
import winterwolfsv.cobblemon_quests.CobblemonQuests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.LOGGER;

public class CobblemonQuestsConfig {
    public static Path configPath = CobblemonQuests.configPath;
    public static double configVersion = 1.0;
    public static List<String> ignoredPokemon = new ArrayList<>();
    public static boolean suppressWarnings = false;

    public static void init() {
        Yaml yaml = new Yaml();
        File configFile = configPath.toFile();
        if (!configFile.exists()) {
            createDefaultConfig(configFile, yaml);
            return;
        }
        Map<String, Object> config = new HashMap<>();
        try {
            config = yaml.load(Files.newBufferedReader(configPath));
            if (config == null) throw new Exception();
        } catch (Exception e) {
            CobblemonQuests.LOGGER.warning("Failed to load config file for Cobblemon Quests. This is a critical error and may cause crashes.");
        }
        try {
            if ((double) config.get("configVersion") < configVersion) {
                CobblemonQuests.LOGGER.info("CobblemonQuestsConfig file for Cobblemon Quests is outdated. Attempting to migrate.");
                // Handle migration here
            }
            ignoredPokemon = (List<String>) config.get("ignoredPokemon");
            suppressWarnings = (boolean) config.get("suppressWarnings");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config values for Cobblemon Quests. Please verify the config file or delete it to generate a new one. " + e);
        }
    }

    public static void save() {
        Yaml yaml = new Yaml();
        File configFile = configPath.toFile();
        createDefaultConfig(configFile, yaml);
    }

    private static void createDefaultConfig(File configFile, Yaml yaml) {
        if (!configFile.exists()) {
            try {
                Files.createDirectories(configPath.getParent());
                if (!Files.exists(configPath)) {
                    Files.createFile(configPath);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to create config directory or file. Please report this with a log file to WinterWolfSV", e);
            }
        }
        try {
            Map<String, Object> defaultConfig = new HashMap<>();
            defaultConfig.put("configVersion", configVersion);
            defaultConfig.put("ignoredPokemon", ignoredPokemon);
            defaultConfig.put("suppressWarnings", suppressWarnings);
            yaml.dump(defaultConfig, new FileWriter(configFile));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to populate config file for Cobblemon Quests.", e);
        }
    }


}
