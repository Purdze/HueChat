package dev.purdze.huechat.utils;

import dev.purdze.huechat.HueChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final HueChat plugin;
    private final int resourceId = 121702;

    public UpdateChecker(HueChat plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates(Player player) {
        try {
            String currentVersion = plugin.getDescription().getVersion();
            URL url = new URL("https://api.spiget.org/v2/resources/" + resourceId + "/versions/latest");
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "HueChat UpdateChecker");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            String latestVersion = response.toString().split("\"name\":\"")[1].split("\"")[0];
            
            if (!currentVersion.equals(latestVersion)) {
                player.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "A new version of HueChat is available!");
                player.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion);
                player.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "Latest version: " + ChatColor.GREEN + latestVersion);
                player.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "Download: " + ChatColor.AQUA + "https://www.spigotmc.org/resources/" + resourceId);
            }
            
        } catch (Exception exception) {
            plugin.getLogger().warning("Unable to check for updates: " + exception.getMessage());
        }
    }
} 