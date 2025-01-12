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
import java.util.HashMap;
import java.util.Map;
import com.vdurmont.emoji.EmojiParser;

public class ChatListener implements Listener {

    private final HueChat plugin;
    private final Map<String, String> emojis = new HashMap<>();

    public ChatListener(HueChat plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        // Smileys & Faces
        emojis.put(":)", "☺");
        emojis.put(":(", "☹");
        emojis.put(":D", "ツ");
        
        // Hearts & Symbols
        emojis.put("<3", "❤");
        emojis.put(":heart:", "❤");
        emojis.put(":star:", "★");
        emojis.put(":sun:", "☀");
        emojis.put(":cloud:", "☁");
        emojis.put(":umbrella:", "☂");
        emojis.put(":snowman:", "☃");
        emojis.put(":comet:", "☄");
        emojis.put(":peace:", "☮");
        emojis.put(":skull:", "☠");
        emojis.put(":radioactive:", "☢");
        emojis.put(":biohazard:", "☣");
        emojis.put(":cross:", "✞");
        emojis.put(":check:", "✓");
        emojis.put(":x:", "✗");
        emojis.put(":mark:", "✧");
        emojis.put(":flower:", "✿");
        emojis.put(":sparkle:", "❋");
        emojis.put(":note:", "♪");
        emojis.put(":notes:", "♫");
        emojis.put(":arrow:", "➤");
        emojis.put(":point:", "►");
        emojis.put(":crown:", "♔");
        emojis.put(":dice1:", "⚀");
        emojis.put(":dice2:", "⚁");
        emojis.put(":dice3:", "⚂");
        emojis.put(":dice4:", "⚃");
        emojis.put(":dice5:", "⚄");
        emojis.put(":dice6:", "⚅");
        
        // Zodiac symbols
        emojis.put(":aries:", "♈");
        emojis.put(":taurus:", "♉");
        emojis.put(":gemini:", "♊");
        emojis.put(":cancer:", "♋");
        emojis.put(":leo:", "♌");
        emojis.put(":virgo:", "♍");
        emojis.put(":libra:", "♎");
        emojis.put(":scorpius:", "♏");
        emojis.put(":sagittarius:", "♐");
        emojis.put(":capricorn:", "♑");
        emojis.put(":aquarius:", "♒");
        emojis.put(":pisces:", "♓");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        boolean isMentioned = false;
        
        // Convert emoji aliases to Unicode emojis only if player has permission
        if (player.hasPermission("huechat.emoji.*")) {
            message = EmojiParser.parseToUnicode(message);
        } else {
            // Check individual emoji permissions
            for (Map.Entry<String, String> emoji : emojis.entrySet()) {
                String emojiName = emoji.getKey().replace(":", "");
                if (message.contains(emoji.getKey()) && !player.hasPermission("huechat.emoji." + emojiName)) {
                    player.sendMessage(HueChat.getPrefix() + ChatColor.RED + "You don't have permission to use the " + emoji.getKey() + " emoji!");
                    event.setCancelled(true);
                    return;
                }
            }
            message = EmojiParser.parseToUnicode(message);
        }
        
        // Always handle emojis
        message = replaceEmojis(message);
        
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

    private String replaceEmojis(String message) {
        String result = message;
        for (Map.Entry<String, String> emoji : emojis.entrySet()) {
            result = result.replace(emoji.getKey(), emoji.getValue());
        }
        return result;
    }
} 