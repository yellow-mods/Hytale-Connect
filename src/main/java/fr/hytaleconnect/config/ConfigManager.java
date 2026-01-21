package fr.hytaleconnect.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private final File configFile;
    private final Gson gson;
    private HytaleConnectConfig config;

    public ConfigManager(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.configFile = new File(dataFolder, "config.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        try (FileReader reader = new FileReader(configFile)) {
            this.config = gson.fromJson(reader, HytaleConnectConfig.class);
            System.out.println("[HytaleConnect] Configuration loaded.");

            if ("CHANGE_ME".equals(config.getServerApiKey())) {
                System.out.println("[HytaleConnect] WARNING: Server API Key is not set!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to default
            this.config = new HytaleConnectConfig();
        }
    }

    public void saveDefaultConfig() {
        this.config = new HytaleConnectConfig();
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(config, writer);
            System.out.println("[HytaleConnect] Default config created.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HytaleConnectConfig getConfig() {
        return config;
    }
}
