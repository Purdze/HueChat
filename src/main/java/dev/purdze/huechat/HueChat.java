package dev.purdze.huechat;

import dev.purdze.huechat.commands.HueChatCommand;
import dev.purdze.huechat.gui.ColorGUI;
import dev.purdze.huechat.listeners.ChatListener;
import dev.purdze.huechat.listeners.JoinListener;
import dev.purdze.huechat.hooks.PlaceholderAPIHook;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.HandlerList;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Bukkit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HueChat extends JavaPlugin {
    private static String PREFIX;
    private ColorGUI colorGUI;
    private PlaceholderAPIHook placeholderAPIHook;
    private final Map<UUID, String> playerColors = new HashMap<>();
    private final Map<UUID, Boolean> playerMentions = new HashMap<>();
    private static final int RESOURCE_ID = 121702;
    private String latestVersion;
    private int serverVersion;
    
    @Override
    public void onEnable() {
        // Get server version more safely
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName();
            String[] parts = version.split("\\.");
            if (parts.length >= 4) {
                String versionPart = parts[3];
                String[] versionNumbers = versionPart.split("_");
                if (versionNumbers.length >= 2) {
                    serverVersion = Integer.parseInt(versionNumbers[1]);
                } else {
                    serverVersion = 20; // Default to latest if can't determine
                    getLogger().warning("Could not determine server version, defaulting to latest");
                }
            } else {
                serverVersion = 20; // Default to latest if can't determine
                getLogger().warning("Could not determine server version, defaulting to latest");
            }
        } catch (Exception e) {
            serverVersion = 20; // Default to latest if any error occurs
            getLogger().warning("Error determining server version, defaulting to latest: " + e.getMessage());
        }
        
        saveDefaultConfig();
        
        updateConfig();
        updatePrefix();
        loadPlayerData();
        
        // Initialize GUI only if enabled
        if (getConfig().getBoolean("commands.gui", true)) {
            this.colorGUI = ColorGUI.getInstance(this);
        }
        
        // Register huechat command
        HueChatCommand hueChatCommand = new HueChatCommand(this);
        getCommand("huechat").setExecutor(hueChatCommand);
        getCommand("huechat").setTabCompleter(hueChatCommand);
        
        // Initialize listeners directly since they self-register
        new ChatListener(this);
        new JoinListener(this);
        
        // Initialize hooks
        this.placeholderAPIHook = new PlaceholderAPIHook(this);
        
        // Check for updates
        checkForUpdates();
        
        getLogger().info("has been enabled!");
    }

    private void loadPlayerData() {
        if (getConfig().contains("players")) {
            getConfig().getConfigurationSection("players").getKeys(false).forEach(uuid -> {
                UUID id = UUID.fromString(uuid);
                String color = getConfig().getString("players." + uuid + ".color");
                boolean mentions = getConfig().getBoolean("players." + uuid + ".mentions", true);
                if (color != null) playerColors.put(id, color);
                playerMentions.put(id, mentions);
            });
        }
    }

    @Override
    public void onDisable() {
        // Unregister all listeners
        HandlerList.unregisterAll(this);
        getLogger().info("has been disabled!");
    }
    
    public void reloadPlugin() {
        HandlerList.unregisterAll(this);
        ColorGUI.destroyInstance();
        reloadConfig();
        updatePrefix();
        
        // Re-initialize GUI only if enabled
        if (getConfig().getBoolean("commands.gui", true)) {
            this.colorGUI = ColorGUI.getInstance(this);
        }
        
        new ChatListener(this);
        new JoinListener(this);
    }
    
    private void updatePrefix() {
        PREFIX = ChatColor.translateAlternateColorCodes('&', 
            getConfig().getString("prefix", "&7[&6HueChat&7]")) + " ";
    }
    
    public static String getPrefix() {
        return PREFIX;
    }
    
    public ColorGUI getColorGUI() {
        return colorGUI;
    }
    
    public PlaceholderAPIHook getPlaceholderAPIHook() {
        return placeholderAPIHook;
    }

    public void setPlayerColor(UUID uuid, String color) {
        playerColors.put(uuid, color);
        getConfig().set("players." + uuid + ".color", color);
        saveConfig();
    }

    public String getPlayerColor(UUID uuid) {
        return playerColors.get(uuid);
    }

    public void setPlayerMentions(UUID uuid, boolean enabled) {
        playerMentions.put(uuid, enabled);
        getConfig().set("players." + uuid + ".mentions", enabled);
        saveConfig();
    }

    public boolean getPlayerMentions(UUID uuid) {
        return playerMentions.getOrDefault(uuid, true);
    }

    private void updateConfig() {
        // Backup old config
        File oldConfig = new File(getDataFolder(), "config.yml");
        File backupConfig = new File(getDataFolder(), "config.old.yml");
        if (oldConfig.exists()) {
            oldConfig.renameTo(backupConfig);
            
            // Create new config with defaults
            saveDefaultConfig();
            reloadConfig();
            
            // Load and restore settings from backup
            FileConfiguration oldCfg = YamlConfiguration.loadConfiguration(backupConfig);
            if (oldCfg.contains("prefix")) {
                getConfig().set("prefix", oldCfg.getString("prefix"));
            }
            if (oldCfg.contains("commands.gui")) {
                getConfig().set("commands.gui", oldCfg.getBoolean("commands.gui"));
            }
            if (oldCfg.contains("commands.color")) {
                getConfig().set("commands.color", oldCfg.getBoolean("commands.color"));
            }
            saveConfig();
            
            getLogger().info("Old config backed up to config.old.yml and new config created");
        }
    }

    private void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID);
                URLConnection conn = url.openConnection();
                conn.addRequestProperty("User-Agent", "HueChat-UpdateChecker");
                
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    latestVersion = reader.readLine();
                    String currentVersion = getDescription().getVersion();
                    
                    if (!currentVersion.equals(latestVersion)) {
                        getLogger().info("A new version of HueChat is available!");
                        getLogger().info("Current version: " + currentVersion);
                        getLogger().info("Latest version: " + latestVersion);
                        getLogger().info("Download it at: https://www.spigotmc.org/resources/" + RESOURCE_ID);
                    }
                }
            } catch (Exception e) {
                getLogger().warning("Unable to check for updates: " + e.getMessage());
            }
        });
    }

    // Add this method to get update status
    public boolean isUpdateAvailable() {
        return latestVersion != null && !getDescription().getVersion().equals(latestVersion);
    }

    // Add this method to get the latest version
    public String getLatestVersion() {
        return latestVersion;
    }

    public int getServerVersion() {
        return serverVersion;
    }
} 