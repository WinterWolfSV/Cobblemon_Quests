package winterwolfsv.cobblemon_quests.config;

import winterwolfsv.cobblemon_quests.CobblemonQuests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import static winterwolfsv.cobblemon_quests.CobblemonQuests.LOGGER;

public class CobblemonQuestsConfig {
    public static Path configPath = CobblemonQuests.configPath;
    public static double configVersion = 1.0;
    public static List<String> ignoredPokemon = new ArrayList<>();
    public static boolean suppressWarnings = false;

    public static void init() {
        File configFile = configPath.toFile();
        load(configFile);
    }

    private static void load(File configFile) {
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
            return;
        }
        try {
            for (String line : Files.readAllLines(configPath)) {
                String[] split = line.split(": ");
                if (split.length != 2) {
                    LOGGER.log(Level.WARNING, "Failed to parse config line: " + line);
                    continue;
                }
                String key = split[0];
                String value = split[1];
                switch (key) {
                    case "configVersion":
                        if (Double.parseDouble(value) < configVersion)
                            LOGGER.info("CobblemonQuestsConfig file for Cobblemon Quests is outdated. Attempting to migrate.");
                        // Handle migration here
                        break;
                    case "suppressWarnings":
                        suppressWarnings = Boolean.parseBoolean(value);
                        break;
                    case "ignoredPokemon":
                        ignoredPokemon = new ArrayList<>(List.of(Arrays.stream(value.split(", ")).map(String::trim).toArray(String[]::new)));
                        break;
                    default:
                        LOGGER.log(Level.WARNING, "Unknown config key: " + key);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load config file for Cobblemon Quests. This is a critical error and may cause crashes.", e);
        }
    }

    public static void save() {
        File configFile = configPath.toFile();
        createDefaultConfig(configFile);
    }

    private static void createDefaultConfig(File configFile) {
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
            List<String> defaultConfig = new ArrayList<>();
            defaultConfig.add("configVersion: " + configVersion);
            defaultConfig.add("ignoredPokemon: " + String.join(", ", ignoredPokemon));
            defaultConfig.add("suppressWarnings: " + suppressWarnings);
            Files.write(configPath, defaultConfig);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to populate config file for Cobblemon Quests.", e);
        }
    }


}
