package fr.hytaleconnect.stats;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.plugin.PluginBase;

import java.util.ArrayList;
import java.util.List;

public class ServerStatsCollector {

    public ServerStatsCollector() {
    }

    public String getServerName() {
        try {
            return HytaleServer.get().getConfig().getServerName();
        } catch (Exception e) {
            return "Hytale Server";
        }
    }

    public String getMotd() {
        try {
            return HytaleServer.get().getConfig().getMotd();
        } catch (Exception e) {
            return "Welcome to our server!";
        }
    }

    public int getMaxPlayers() {
        try {
            return HytaleServer.get().getConfig().getMaxPlayers();
        } catch (Exception e) {
            return 20;
        }
    }

    public int getCurrentPlayers() {
        try {
            return com.hypixel.hytale.server.core.universe.Universe.get().getPlayers().size();
        } catch (Exception e) {
            System.out.println("[HytaleConnect-Stats] Error getting player count: " + e.getMessage());
            return 0;
        }
    }

    public List<PlayerRef> getPlayerList() {
        try {
            return com.hypixel.hytale.server.core.universe.Universe.get().getPlayers();
        } catch (Exception e) {
            System.out.println("[HytaleConnect-Stats] Error getting player list: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public String getVersion() {
        return "Hytale 1.0";
    }

    public int getProtocolVersion() {
        return 1;
    }

    public String getProtocolHash() {
        return "hytale_v1";
    }

    public List<PluginInfo> getPlugins() {
        List<PluginInfo> result = new ArrayList<>();
        try {
            List<PluginBase> plugins = HytaleServer.get().getPluginManager().getPlugins();
            for (PluginBase plugin : plugins) {
                com.hypixel.hytale.common.plugin.PluginManifest manifest = plugin.getManifest();
                String name = (manifest != null && manifest.getName() != null) ? manifest.getName()
                        : plugin.getClass().getSimpleName();
                String version = (manifest != null && manifest.getVersion() != null) ? manifest.getVersion().toString()
                        : "1.0";
                result.add(new PluginInfo(name, version, plugin.isEnabled()));
            }
        } catch (Exception e) {
            System.out.println("[HytaleConnect-Stats] Error getting plugin list: " + e.getMessage());
        }
        return result;
    }

    public long getUptime() {
        try {
            java.time.Instant boot = HytaleServer.get().getBoot();
            if (boot != null) {
                return java.time.Duration.between(boot, java.time.Instant.now()).toMillis();
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public List<String> getWorldNames() {
        List<String> names = new ArrayList<>();
        try {
            java.util.Map<String, com.hypixel.hytale.server.core.universe.world.World> worlds = com.hypixel.hytale.server.core.universe.Universe
                    .get().getWorlds();
            if (worlds != null) {
                names.addAll(worlds.keySet());
            }
        } catch (Exception e) {
        }
        return names;
    }

    public long[] getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long max = runtime.maxMemory();
        long used = runtime.totalMemory() - runtime.freeMemory();
        return new long[] { used, max };
    }

    public static class PluginInfo {
        public final String identifier;
        public final String version;
        public final boolean enabled;

        public PluginInfo(String identifier, String version, boolean enabled) {
            this.identifier = identifier;
            this.version = version;
            this.enabled = enabled;
        }
    }
}
