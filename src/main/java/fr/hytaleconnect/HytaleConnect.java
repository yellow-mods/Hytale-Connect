package fr.hytaleconnect;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.command.system.CommandManager;

public class HytaleConnect extends JavaPlugin {

    public HytaleConnect(JavaPluginInit init) {
        super(init);
    }

    private fr.hytaleconnect.config.ConfigManager configManager;
    private fr.hytaleconnect.service.VoteService voteService;
    private fr.hytaleconnect.service.StatPushService statPushService;
    private fr.hytaleconnect.service.VoteReminderService reminderService;

    @Override
    protected void start() {
        System.out.println("[HytaleConnect] Plugin enabled!");

        // Initialize config
        java.io.File dataFolder = new java.io.File("mods/HytaleConnect");
        this.configManager = new fr.hytaleconnect.config.ConfigManager(dataFolder);
        this.configManager.loadConfig();

        if (configManager.getConfig().isVoteEnabled()) {
            // Initialize Service
            this.voteService = new fr.hytaleconnect.service.VoteService(configManager.getConfig());

            // Initialize Commands
            fr.hytaleconnect.command.ClaimCommand claimCommand = new fr.hytaleconnect.command.ClaimCommand(voteService,
                    configManager.getConfig());
            // Register Command
            CommandManager commandManager = HytaleServer.get().getCommandManager();
            commandManager.register(claimCommand);

            // Initialize Reminder Service
            this.reminderService = new fr.hytaleconnect.service.VoteReminderService(configManager.getConfig());
            this.reminderService.start();

            System.out.println("[HytaleConnect] Vote system enabled!");
        } else {
            System.out.println("[HytaleConnect] Vote system is disabled in config.");
        }

        System.out.println("[HytaleConnect] API Key configured: "
                + (!configManager.getConfig().getServerApiKey().equals("CHANGE_ME")));

        // Initialize Stats Push Service
        if (configManager.getConfig().getStats().isEnabled()) {
            fr.hytaleconnect.stats.ServerStatsCollector statsCollector = new fr.hytaleconnect.stats.ServerStatsCollector();
            this.statPushService = new fr.hytaleconnect.service.StatPushService(configManager.getConfig(),
                    statsCollector);
            this.statPushService.start();
        } else {
            System.out.println("[HytaleConnect] Stats push service is disabled in config.");
        }
    }

    protected void stop() {
        System.out.println("[HytaleConnect] Plugin disabled!");

        // Stop push service
        if (statPushService != null) {
            statPushService.stop();
        }
        if (reminderService != null) {
            reminderService.stop();
        }
    }
}
