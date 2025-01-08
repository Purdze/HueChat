package dev.purdze.huechat.listeners;

import dev.purdze.huechat.HueChat;
import dev.purdze.huechat.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final HueChat plugin;
    private static final int RESOURCE_ID = 121702;
    private static final String UPDATE_PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "HueChat" + ChatColor.GRAY + "] ";

    public JoinListener(HueChat plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("huechat.updates")) {
            new UpdateChecker(plugin).checkForUpdates(player);
        }
        
        // Check for updates and notify player if they have permission
        if (player.hasPermission("huechat.update") && plugin.isUpdateAvailable()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(UPDATE_PREFIX + ChatColor.YELLOW + "A new version is available! " +
                    ChatColor.YELLOW + "(" + ChatColor.RED + plugin.getDescription().getVersion() + 
                    ChatColor.YELLOW + " → " + ChatColor.GREEN + plugin.getLatestVersion() + 
                    ChatColor.YELLOW + ") " + ChatColor.AQUA + 
                    "https://www.spigotmc.org/resources/" + RESOURCE_ID);
            }, 40L);
        }
    }
} 