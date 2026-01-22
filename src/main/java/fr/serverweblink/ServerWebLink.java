package fr.serverweblink;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.command.system.CommandManager;

public class ServerWebLink extends JavaPlugin {

    public ServerWebLink(JavaPluginInit init) {
        super(init);
    }

    private fr.serverweblink.config.ConfigManager configManager;
    private fr.serverweblink.service.VoteService voteService;
    private fr.serverweblink.service.StatPushService statPushService;
    private fr.serverweblink.service.VoteReminderService reminderService;

    @Override
    protected void start() {
        System.out.println("[ServerWebLink] Plugin enabled!");

        // Initialize config
        java.io.File dataFolder = new java.io.File("mods/ServerWebLink");
        this.configManager = new fr.serverweblink.config.ConfigManager(dataFolder);
        this.configManager.loadConfig();

        if (configManager.getConfig().isVoteEnabled()) {
            // Initialize Service
            this.voteService = new fr.serverweblink.service.VoteService(configManager.getConfig());

            // Initialize Commands
            fr.serverweblink.command.ClaimCommand claimCommand = new fr.serverweblink.command.ClaimCommand(voteService,
                    configManager.getConfig());
            // Register Command
            CommandManager commandManager = HytaleServer.get().getCommandManager();
            commandManager.register(claimCommand);

            // Initialize Reminder Service
            this.reminderService = new fr.serverweblink.service.VoteReminderService(configManager.getConfig());
            this.reminderService.start();

            System.out.println("[ServerWebLink] Vote system enabled!");
        } else {
            System.out.println("[ServerWebLink] Vote system is disabled in config.");
        }

        System.out.println("[ServerWebLink] API Key configured: "
                + (!configManager.getConfig().getServerApiKey().equals("CHANGE_ME")));

        // Initialize Stats Push Service
        if (configManager.getConfig().getStats().isEnabled()) {
            fr.serverweblink.stats.ServerStatsCollector statsCollector = new fr.serverweblink.stats.ServerStatsCollector();
            this.statPushService = new fr.serverweblink.service.StatPushService(configManager.getConfig(),
                    statsCollector);
            this.statPushService.start();
        } else {
            System.out.println("[ServerWebLink] Stats push service is disabled in config.");
        }
    }

    protected void stop() {
        System.out.println("[ServerWebLink] Plugin disabled!");

        // Stop push service
        if (statPushService != null) {
            statPushService.stop();
        }
        if (reminderService != null) {
            reminderService.stop();
        }
    }
}
