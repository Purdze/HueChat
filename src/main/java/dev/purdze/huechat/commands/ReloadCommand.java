package dev.purdze.huechat.commands;

import dev.purdze.huechat.HueChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    public static void execute(CommandSender sender, HueChat plugin) {
        if (!sender.hasPermission("huechat.reload")) {
            sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "You don't have permission to use this command!");
            return;
        }
        
        plugin.reloadPlugin();
        sender.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Configuration reloaded successfully!");
    }
} 