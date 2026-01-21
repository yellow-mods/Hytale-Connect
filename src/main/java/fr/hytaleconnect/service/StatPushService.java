package fr.hytaleconnect.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import fr.hytaleconnect.config.HytaleConnectConfig;
import fr.hytaleconnect.stats.ServerStatsCollector;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StatPushService {

    private final HytaleConnectConfig config;
    private final ServerStatsCollector statsCollector;
    private final HttpClient httpClient;
    private final Gson gson;
    private ScheduledFuture<?> scheduledTask;

    public StatPushService(HytaleConnectConfig config, ServerStatsCollector statsCollector) {
        this.config = config;
        this.statsCollector = statsCollector;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public void start() {
        if (!config.getStats().isEnabled()) {
            return;
        }

        int intervalMinutes = config.getStats().getIntervalMinutes();

        System.out.println("[HytaleConnect] Starting Stat Push Service (Interval: "
                + intervalMinutes + " min)");

        pushStats();

        this.scheduledTask = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(
                this::pushStats,
                intervalMinutes,
                intervalMinutes,
                TimeUnit.MINUTES);
    }

    public void stop() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            scheduledTask = null;
        }
    }

    private void pushStats() {
        try {
            System.out.println("[HytaleConnect-Push] Collecting and pushing server stats...");
            JsonObject payload = new JsonObject();
            HytaleConnectConfig.StatPushConfig.PrivacyConfig privacy = config.getStats().getPrivacy();

            payload.addProperty("players", statsCollector.getCurrentPlayers());
            payload.addProperty("max_players", statsCollector.getMaxPlayers());
            payload.addProperty("status", "online");

            if (privacy.isShowUptime()) {
                payload.addProperty("uptime", statsCollector.getUptime());
            }

            if (privacy.isShowRAM()) {
                long[] mem = statsCollector.getMemoryUsage();
                payload.addProperty("memory_used", mem[0]);
                payload.addProperty("memory_max", mem[1]);
            }

            if (privacy.isShowWorlds()) {
                JsonArray worlds = new JsonArray();
                for (String world : statsCollector.getWorldNames()) {
                    worlds.add(world);
                }
                payload.add("worlds", worlds);
            }

            if (privacy.isShowPlayerNames()) {
                JsonArray playerList = new JsonArray();
                for (PlayerRef player : statsCollector.getPlayerList()) {
                    JsonObject pObj = new JsonObject();
                    pObj.addProperty("name", player.getUsername());
                    pObj.addProperty("uuid", player.getUuid().toString());
                    playerList.add(pObj);
                }
                payload.add("player_list", playerList);
            }

            if (privacy.isShowPlugins()) {
                JsonArray plugins = new JsonArray();
                for (ServerStatsCollector.PluginInfo plugin : statsCollector.getPlugins()) {
                    JsonObject pObj = new JsonObject();
                    pObj.addProperty("name", plugin.identifier);
                    pObj.addProperty("version", plugin.version);
                    pObj.addProperty("enabled", plugin.enabled);
                    plugins.add(pObj);
                }
                payload.add("plugins", plugins);
            }

            String jsonPayload = gson.toJson(payload);

            String baseUrl = config.getApiUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            String pushUrl = baseUrl + "/push-stats";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pushUrl))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", config.getServerApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            System.out.println("[HytaleConnect-Push] Stats pushed successfully!");
                        } else {
                            System.out.println(
                                    "[HytaleConnect-Push] FAILED to push stats. Status: " + response.statusCode());
                        }
                    })
                    .exceptionally(ex -> {
                        System.out.println(
                                "[HytaleConnect-Push] API Connection Error (The API might be down or unreachable)");
                        return null;
                    });

        } catch (Exception e) {
            System.out.println("[HytaleConnect] Error preparing stats push: " + e.getMessage());
        }
    }
}
