package dev.purdze.huechat.listeners;

import dev.purdze.huechat.HueChat;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class ChatListener implements Listener {

    private final HueChat plugin;

    public ChatListener(HueChat plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        boolean isMentioned = false;
        
        // Handle mentions first
        if (plugin.getConfig().getBoolean("mentions.enabled", true)) {
            String highlightColor = ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfig().getString("mentions.highlight-color", "&e"));
            
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String playerName = onlinePlayer.getName();
                // Check if player has mentions enabled
                boolean mentionsEnabled = plugin.getConfig().getBoolean("players." + onlinePlayer.getUniqueId() + ".mentions", true);
                if (!mentionsEnabled) continue;
                
                if (message.toLowerCase().contains(playerName.toLowerCase())) {
                    // Highlight the entire message
                    message = highlightColor + message;
                    isMentioned = true;
                    
                    // Play sound
                    if (plugin.getConfig().getBoolean("mentions.play-sound", true)) {
                        String soundName = plugin.getConfig().getString("mentions.sound.type", "ENTITY_EXPERIENCE_ORB_PICKUP");
                        // Convert new sound name to legacy if needed
                        if (soundName.equals("ENTITY_EXPERIENCE_ORB_PICKUP")) {
                            soundName = plugin.getServerVersion() <= 8 ? "ORB_PICKUP" : soundName;
                        }
                        
                        final String finalSound = soundName;
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            playSound(onlinePlayer, finalSound);
                        });
                    }
                    break; // Exit loop once we've highlighted the message
                }
            }
        }

        // Only apply chat color if message isn't already highlighted from a mention
        if (!isMentioned) {
            String colorName = plugin.getConfig().getString("players." + player.getUniqueId() + ".color");
            if (colorName != null) {
                if (colorName.equals("RAINBOW")) {
                    message = makeRainbow(message);
                } else {
                    try {
                        ChatColor color = ChatColor.valueOf(colorName);
                        message = color + message;
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid color in config for player " + player.getName() + ": " + colorName);
                    }
                }
            }
        }
        
        event.setMessage(message);
    }

    private String makeRainbow(String message) {
        ChatColor[] colors = {
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.LIGHT_PURPLE,
            ChatColor.DARK_PURPLE
        };
        
        StringBuilder rainbow = new StringBuilder();
        String[] letters = message.split("");
        int currentColor = 0;
        
        for (String letter : letters) {
            if (!letter.trim().isEmpty()) {
                rainbow.append(colors[currentColor]).append(letter);
                currentColor = (currentColor + 1) % colors.length;
            } else {
                rainbow.append(letter);
            }
        }
        
        return rainbow.toString();
    }

    private void playSound(Player player, String configuredSound) {
        try {
            // Try 1.9+ sound first
            Sound sound = Sound.valueOf(configuredSound);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            try {
                // Try legacy sound (1.8)
                player.playSound(player.getLocation(), configuredSound, 1.0f, 1.0f);
            } catch (Exception ex) {
                plugin.getLogger().warning("Invalid sound in config: " + configuredSound);
            }
        }
    }
} 