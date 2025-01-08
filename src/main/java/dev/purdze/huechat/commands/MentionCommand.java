package dev.purdze.huechat.commands;

import dev.purdze.huechat.HueChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MentionCommand {
    public static boolean execute(CommandSender sender, HueChat plugin, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        if (args.length != 2 || !args[1].equalsIgnoreCase("toggle")) {
            sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "Usage: /" + label + " mention toggle");
            return true;
        }

        Player player = (Player) sender;
        boolean currentState = plugin.getConfig().getBoolean("players." + player.getUniqueId() + ".mentions", true);
        plugin.getConfig().set("players." + player.getUniqueId() + ".mentions", !currentState);
        plugin.saveConfig();
        player.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Mentions have been " + 
            (!currentState ? "enabled" : "disabled") + "!");
        return true;
    }
} 