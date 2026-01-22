package fr.serverweblink.service;

import fr.serverweblink.config.ServerWebLinkConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class VoteService {
    private final ServerWebLinkConfig config;
    private final HttpClient httpClient;
    private final Gson gson;

    public enum VoteStatus {
        NOT_VOTED,
        VOTED_UNCLAIMED,
        VOTED_CLAIMED,
        ERROR
    }

    public VoteService(ServerWebLinkConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public static class VoteResult {
        public boolean hasVoted;
        public boolean claimed;
        public String nextVoteWait;
        public int unclaimedCount;
        public boolean isError;

        public VoteResult(boolean hasVoted, boolean claimed, String nextVoteWait, int unclaimedCount, boolean isError) {
            this.hasVoted = hasVoted;
            this.claimed = claimed;
            this.nextVoteWait = nextVoteWait;
            this.unclaimedCount = unclaimedCount;
            this.isError = isError;
        }

        public static VoteResult error() {
            return new VoteResult(false, false, null, 0, true);
        }
    }

    public CompletableFuture<VoteResult> hasVoted(String playerName) {
        if (config.getServerApiKey().equals("CHANGE_ME")) {
            System.out.println("[ServerWebLink] Cannot check vote: API Key not configured.");
            return CompletableFuture.completedFuture(VoteResult.error());
        }

        String encodedName = playerName;
        try {
            encodedName = java.net.URLEncoder.encode(playerName, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
        }

        String url = config.getApiUrl() + "/vote/check?username=" + encodedName;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-api-key", config.getServerApiKey())
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                            boolean hasVoted = json.has("hasVoted") && json.get("hasVoted").getAsBoolean();
                            boolean claimed = json.has("claimed") && json.get("claimed").getAsBoolean();
                            int unclaimedCount = json.has("unclaimedCount") ? json.get("unclaimedCount").getAsInt()
                                    : (hasVoted && !claimed ? 1 : 0);
                            String nextVoteWait = json.has("nextVoteWait") && !json.get("nextVoteWait").isJsonNull()
                                    ? json.get("nextVoteWait").getAsString()
                                    : null;

                            return new VoteResult(hasVoted, claimed, nextVoteWait, unclaimedCount, false);
                        } catch (Exception e) {
                            return VoteResult.error();
                        }
                    } else {
                        return VoteResult.error();
                    }
                })
                .exceptionally(e -> {
                    System.out.println("[ServerWebLink] Exception checking vote (Connection error or timeout)");
                    return VoteResult.error();
                });
    }

    public CompletableFuture<Boolean> claimVote(String playerName) {
        if (config.getServerApiKey().equals("CHANGE_ME"))
            return CompletableFuture.completedFuture(false);

        String url = config.getApiUrl() + "/vote/claim";
        JsonObject payload = new JsonObject();
        payload.addProperty("username", playerName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-api-key", config.getServerApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    return response.statusCode() == 200 || response.statusCode() == 201;
                })
                .exceptionally(e -> {
                    System.out.println("[ServerWebLink] Claim Exception (Connection error or timeout)");
                    return false;
                });
    }
}
