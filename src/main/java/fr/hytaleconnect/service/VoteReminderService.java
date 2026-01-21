package fr.hytaleconnect.service;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.entity.entities.Player;
import fr.hytaleconnect.config.HytaleConnectConfig;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class VoteReminderService {

    private final HytaleConnectConfig config;
    private ScheduledFuture<?> scheduledTask;

    public VoteReminderService(HytaleConnectConfig config) {
        this.config = config;
    }

    public void start() {
        if (!config.getReminder().isEnabled()) {
            return;
        }

        int interval = config.getReminder().getIntervalMinutes();
        if (interval <= 0)
            interval = 30; // Safety default

        System.out.println("[HytaleConnect] Starting Vote Reminder Service (Interval: " + interval + " min)");

        this.scheduledTask = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(
                this::broadcastReminder,
                interval,
                interval,
                TimeUnit.MINUTES);
    }

    public void stop() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            scheduledTask = null;
        }
    }

    private void broadcastReminder() {
        try {
            String msg = config.getReminder().getMessage();
            if (msg == null || msg.isEmpty())
                return;

            msg = msg.replace("{command}", config.getCommandName());

            Message formattedMsg = formatMessage(msg);

            try {
                if (Universe.get() != null && Universe.get().getWorlds() != null) {
                    Universe.get().getWorlds().values().forEach(world -> {
                        if (world != null) {
                            world.getPlayers().forEach(entity -> {
                                if (entity instanceof Player) {
                                    ((Player) entity).sendMessage(formattedMsg);
                                }
                            });
                        }
                    });
                }
            } catch (Exception e) {
                System.out.println("[HytaleConnect] Error accessing Universe: " + e.getMessage());
            }

            System.out.println("[HytaleConnect] Vote reminder broadcasted.");
        } catch (Exception e) {
            System.out.println("[HytaleConnect] Error broadcasting reminder: " + e.getMessage());
        }
    }

    private Message formatMessage(String input) {
        if (input == null || input.isEmpty())
            return Message.empty();

        Message result = Message.empty();
        String[] colorParts = input.split("\u00A7");

        int start = 0;
        if (!input.startsWith("\u00A7")) {
            result = Message.join(result, Message.raw(colorParts[0]));
            start = 1;
        }

        for (int i = start; i < colorParts.length; i++) {
            String part = colorParts[i];
            if (part.isEmpty())
                continue;

            char code = Character.toLowerCase(part.charAt(0));
            String text = part.substring(1);
            if (text.isEmpty())
                continue;

            Message partMsg = Message.raw(text);
            switch (code) {
                case '0':
                    partMsg.color("black");
                    break;
                case '1':
                    partMsg.color("dark_blue");
                    break;
                case '2':
                    partMsg.color("dark_green");
                    break;
                case '3':
                    partMsg.color("dark_aqua");
                    break;
                case '4':
                    partMsg.color("dark_red");
                    break;
                case '5':
                    partMsg.color("dark_purple");
                    break;
                case '6':
                    partMsg.color("gold");
                    break;
                case '7':
                    partMsg.color("gray");
                    break;
                case '8':
                    partMsg.color("dark_gray");
                    break;
                case '9':
                    partMsg.color("blue");
                    break;
                case 'a':
                    partMsg.color("green");
                    break;
                case 'b':
                    partMsg.color("aqua");
                    break;
                case 'c':
                    partMsg.color("red");
                    break;
                case 'd':
                    partMsg.color("light_purple");
                    break;
                case 'e':
                    partMsg.color("yellow");
                    break;
                case 'f':
                    partMsg.color("white");
                    break;
                case 'l':
                    partMsg.bold(true);
                    break;
                case 'o':
                    partMsg.italic(true);
                    break;
                default:
                    partMsg = Message.raw("\u00A7" + part);
                    break;
            }
            result = Message.join(result, partMsg);
        }
        return result;
    }
}
