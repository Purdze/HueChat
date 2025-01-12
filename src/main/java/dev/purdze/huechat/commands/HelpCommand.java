package dev.purdze.huechat.commands;
import dev.purdze.huechat.HueChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {
    public static void execute(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== " + ChatColor.WHITE + "HueChat Commands" + ChatColor.GOLD + " ===");
        sender.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "/huechat " + ChatColor.GRAY + "- Open the color selection menu");
        sender.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "/huechat help " + ChatColor.GRAY + "- Shows this help message");
        sender.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "/huechat emojis " + ChatColor.GRAY + "- View available emojis");
        if (sender.hasPermission("huechat.reload")) {
            sender.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "/huechat reload " + ChatColor.GRAY + "- Reloads the plugin configuration");
        }
    }
} 