package dev.purdze.huechat.commands;

import dev.purdze.huechat.HueChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;

public class EmojisCommand {
    private static final Map<String, List<String>> EMOJI_CATEGORIES = new HashMap<>();
    
    static {
        EMOJI_CATEGORIES.put("Smileys", Arrays.asList(
            "smile", "sad", "grin"
        ));
        EMOJI_CATEGORIES.put("Symbols", Arrays.asList(
            "heart", "star", "check", "x", "mark", "arrow", "point", "crown"
        ));
        EMOJI_CATEGORIES.put("Weather", Arrays.asList(
            "sun", "cloud", "umbrella", "snowman", "comet"
        ));
        EMOJI_CATEGORIES.put("Music", Arrays.asList(
            "note", "notes"
        ));
        EMOJI_CATEGORIES.put("Warning", Arrays.asList(
            "skull", "radioactive", "biohazard"
        ));
        EMOJI_CATEGORIES.put("Dice", Arrays.asList(
            "dice1", "dice2", "dice3", "dice4", "dice5", "dice6"
        ));
        EMOJI_CATEGORIES.put("Zodiac", Arrays.asList(
            "aries", "taurus", "gemini", "cancer", "leo", "virgo",
            "libra", "scorpius", "sagittarius", "capricorn", "aquarius", "pisces"
        ));
        EMOJI_CATEGORIES.put("Other", Arrays.asList(
            "peace", "cross", "flower", "sparkle"
        ));
    }

    public static boolean execute(CommandSender sender, HueChat plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        sender.sendMessage(ChatColor.GOLD + "=== " + ChatColor.WHITE + "Available Emojis" + ChatColor.GOLD + " ===");

        boolean hasAnyEmoji = false;
        
        // Show categories and their emojis
        for (Map.Entry<String, List<String>> category : EMOJI_CATEGORIES.entrySet()) {
            StringBuilder categoryEmojis = new StringBuilder();
            boolean hasEmojiInCategory = false;

            for (String emojiName : category.getValue()) {
                if (player.hasPermission("huechat.emoji." + emojiName) || player.hasPermission("huechat.emoji.*")) {
                    if (hasEmojiInCategory) {
                        categoryEmojis.append(" ");
                    }
                    categoryEmojis.append(":").append(emojiName).append(":");
                    hasEmojiInCategory = true;
                    hasAnyEmoji = true;
                }
            }

            if (hasEmojiInCategory) {
                sender.sendMessage(ChatColor.YELLOW + category.getKey() + ": " + ChatColor.GRAY + categoryEmojis.toString());
            }
        }

        if (!hasAnyEmoji) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use any emojis!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Tip: " + ChatColor.GRAY + "Type the emoji code in chat to use it!");
        }
        
        return true;
    }
} 