package fr.hytaleconnect.command;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import fr.hytaleconnect.service.VoteService;
import fr.hytaleconnect.config.HytaleConnectConfig;

public class ClaimCommand extends AbstractCommand {
    private final VoteService voteService;
    private final HytaleConnectConfig config;

    public ClaimCommand(VoteService voteService, HytaleConnectConfig config) {
        super(config.getCommandName());
        this.voteService = voteService;
        this.config = config;
    }

    @Override
    public String getName() {
        return config.getCommandName();
    }

    @Override
    public String getDescription() {
        return "Récupère votre récompense de vote.";
    }

    @Override
    public java.util.concurrent.CompletableFuture<Void> execute(CommandContext ctx) {
        try {
            if (!ctx.isPlayer()) {
                System.out.println("[HytaleConnect] This command can only be executed by a player.");
                return java.util.concurrent.CompletableFuture.completedFuture(null);
            }

            CommandSender sender = ctx.sender();
            if (!(sender instanceof Player)) {
                System.out.println(
                        "[HytaleConnect] Sender is not a valid Player instance: " + sender.getClass().getName());
                return java.util.concurrent.CompletableFuture.completedFuture(null);
            }

            Player player = (Player) sender;
            String playerName = player.getPlayerRef().getUsername();

            if (playerName == null || playerName.isEmpty()) {
                playerName = player.getUuid().toString();
            }

            final String finalPlayerName = playerName;

            // Notify user
            sendMessage(player, config.getMessages().getCheckingVote());
            System.out.println("[HytaleConnect] Checking vote for player: " + finalPlayerName);

            // Async vote check
            voteService.hasVoted(finalPlayerName).thenAccept(result -> {
                if (result.isError) {
                    sendMessage(player, config.getMessages().getErrorChecking());
                    return;
                }

                // Priority 1: Claim if valid vote and not claimed
                if (result.hasVoted && !result.claimed) {
                    voteService.claimVote(finalPlayerName).thenAccept(success -> {
                        if (success) {
                            int iterations = config.isStackRewards() ? result.unclaimedCount : 1;
                            if (iterations < 1)
                                iterations = 1;

                            for (int i = 0; i < iterations; i++) {
                                for (String cmdTemplate : config.getRewardCommands()) {
                                    String finalCmd = cmdTemplate.replace("{player}", finalPlayerName);
                                    HytaleServer.get().getCommandManager().handleCommand(ConsoleSender.INSTANCE,
                                            finalCmd);
                                }
                            }

                            String rewardMsg = config.getRewardMessage()
                                    .replace("{count}", String.valueOf(iterations))
                                    .replace("{player}", finalPlayerName);
                            sendMessage(player, rewardMsg);

                            // Broadcast to all players avoiding Console prefix
                            String broadcastMsg = config.getMessages().getVoteBroadcast()
                                    .replace("{player}", finalPlayerName)
                                    .replace("{command}", config.getCommandName());

                            if (iterations > 1) {
                                broadcastMsg += " (x" + iterations + ")";
                            }

                            if (broadcastMsg != null && !broadcastMsg.isEmpty()) {
                                try {
                                    Message globalMsg = formatMessage(broadcastMsg);
                                    if (com.hypixel.hytale.server.core.universe.Universe.get() != null) {
                                        com.hypixel.hytale.server.core.universe.Universe.get().getWorlds().values()
                                                .forEach(world -> {
                                                    if (world != null) {
                                                        world.getPlayers().forEach(entity -> {
                                                            if (entity instanceof Player) {
                                                                ((Player) entity).sendMessage(globalMsg);
                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                } catch (Exception e) {
                                    System.out.println(
                                            "[HytaleConnect-Broadcast] Failed to broadcast: " + e.getMessage());
                                }
                            }

                            System.out.println("[HytaleConnect] Rewards claimed for " + finalPlayerName);
                        } else {
                            sendMessage(player, config.getMessages().getErrorClaiming());
                        }
                    });
                    return;
                }

                // Priority 2: Cooldown active (User cannot vote currently)
                if (result.nextVoteWait != null) {
                    String waitMsg = config.getMessages().getNextVoteWait()
                            .replace("{time}", result.nextVoteWait);
                    sendMessage(player, waitMsg);
                    return;
                }

                // Priority 3: Already claimed (but no wait time returned?)
                // This shouldn't happen often if API returns wait time, but as fallback
                if (result.hasVoted && result.claimed) {
                    String alreadyClaimedMsg = config.getMessages().getAlreadyClaimed();
                    // Clean up {time} just in case
                    alreadyClaimedMsg = alreadyClaimedMsg.replace(" {time}", "").replace("{time}", "");
                    sendMessage(player, alreadyClaimedMsg);
                    return;
                }

                // Priority 4: Not voted and no cooldown -> Show vote link
                String baseVoteUrl = config.getVoteUrl();
                String fullVoteUrl = baseVoteUrl + (baseVoteUrl.contains("?") ? "&" : "?") + "username="
                        + finalPlayerName;

                String voteMsg = config.getVoteMessage()
                        .replace("{player}", finalPlayerName)
                        .replace("{url}", fullVoteUrl);
                sendMessage(player, voteMsg);
            });

        } catch (Throwable t) {
            System.out.println("[HytaleConnect] Error executing command: " + t.getMessage());
        }
        return java.util.concurrent.CompletableFuture.completedFuture(null);
    }

    private void sendMessage(Player player, String message) {
        try {
            player.sendMessage(formatMessage(message));
        } catch (Throwable t) {
            System.out.println("[HytaleConnect-DEBUG] Failed to send message: " + t.getMessage());
        }
    }

    private Message formatMessage(String input) {
        if (input == null || input.isEmpty())
            return Message.empty();

        Message result = Message.empty();
        String[] colorParts = input.split("\u00A7");

        int start = 0;
        if (!input.startsWith("\u00A7")) {
            result = processTextWithLinks(colorParts[0], null, false, false);
            start = 1;
        }

        for (int i = start; i < colorParts.length; i++) {
            String part = colorParts[i];
            if (part.isEmpty())
                continue;

            char code = Character.toLowerCase(part.charAt(0));
            String text = part.substring(1);

            boolean isColor = false;
            switch (code) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'r':
                    isColor = true;
                    break;
                case 'l': // bold
                case 'o': // italic
                case 'n': // underline
                case 'm': // strikethrough
                case 'k': // magic
                    break;
                default:
                    text = "\u00A7" + part;
                    break;
            }

            String color = mapColor(code);
            boolean bold = (code == 'l');
            boolean italic = (code == 'o');

            if (!text.isEmpty()) {
                Message msgPart = processTextWithLinks(text, color, bold, italic);
                result = Message.join(result, msgPart);
            }
        }
        return result;
    }

    // Helper to map codes to color strings
    private String mapColor(char code) {
        switch (code) {
            case '0':
                return "black";
            case '1':
                return "dark_blue";
            case '2':
                return "dark_green";
            case '3':
                return "dark_aqua";
            case '4':
                return "dark_red";
            case '5':
                return "dark_purple";
            case '6':
                return "gold";
            case '7':
                return "gray";
            case '8':
                return "dark_gray";
            case '9':
                return "blue";
            case 'a':
                return "green";
            case 'b':
                return "aqua";
            case 'c':
                return "red";
            case 'd':
                return "light_purple";
            case 'e':
                return "yellow";
            case 'f':
                return "white";
            default:
                return null;
        }
    }

    private Message processTextWithLinks(String text, String color, boolean bold, boolean italic) {
        // Simple regex to find URLs
        java.util.regex.Pattern urlPattern = java.util.regex.Pattern.compile("(https?://\\S+)");
        java.util.regex.Matcher matcher = urlPattern.matcher(text);

        Message container = Message.empty();
        int lastEnd = 0;

        while (matcher.find()) {
            // Append text before the URL
            if (matcher.start() > lastEnd) {
                container = Message.join(container,
                        createStyledMessage(text.substring(lastEnd, matcher.start()), color, bold, italic, null));
            }

            // Append the URL as a link and make it bold
            String url = matcher.group(1);
            container = Message.join(container, createStyledMessage(url, color, true, italic, url));

            lastEnd = matcher.end();
        }

        // Append remaining text
        if (lastEnd < text.length()) {
            container = Message.join(container,
                    createStyledMessage(text.substring(lastEnd), color, bold, italic, null));
        }

        return container;
    }

    private Message createStyledMessage(String text, String color, boolean bold, boolean italic, String link) {
        Message msg = Message.raw(text);
        if (color != null)
            msg.color(color);
        if (bold)
            msg.bold(true);
        if (italic)
            msg.italic(true);
        if (link != null)
            msg.link(link);
        return msg;
    }
}
