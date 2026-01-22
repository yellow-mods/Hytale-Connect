package fr.serverweblink.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private final File configFile;
    private final Gson gson;
    private ServerWebLinkConfig config;

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
            this.config = gson.fromJson(reader, ServerWebLinkConfig.class);
            System.out.println("[ServerWebLink] Configuration loaded.");

            if ("CHANGE_ME".equals(config.getServerApiKey())) {
                System.out.println("[ServerWebLink] WARNING: Server API Key is not set!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to default
            this.config = new ServerWebLinkConfig();
        }
    }

    public void saveDefaultConfig() {
        this.config = new ServerWebLinkConfig();
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(config, writer);
            System.out.println("[ServerWebLink] Default config created.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerWebLinkConfig getConfig() {
        return config;
    }
}
