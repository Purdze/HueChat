package dev.purdze.huechat.commands;

import dev.purdze.huechat.HueChat;
import dev.purdze.huechat.gui.ColorGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HueChatCommand implements CommandExecutor, TabCompleter {
    private final HueChat plugin;
    private final List<String> colors = Arrays.asList(
        "yellow", "lightpurple", "red", "aqua", "green", "blue", "darkgray",
        "gray", "gold", "darkpurple", "darkred", "darkaqua", "darkblue", "darkgreen", "rainbow"
    );

    public HueChatCommand(HueChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "profanity":
                handleProfanityCommand(sender, args);
                break;

            case "color":
                handleColorCommand(sender, args);
                break;

            case "reload":
                handleReloadCommand(sender);
                break;

            case "help":
                sendHelp(sender);
                break;

            case "mention":
                return MentionCommand.execute(sender, plugin, label, args);
            case "emojis":
                return EmojisCommand.execute(sender, plugin);
            default:
                sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "Unknown command. Type /huechat help for help.");
                break;
        }

        return true;
    }

    private void handleProfanityCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("huechat.profanity")) {
            sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "You don't have permission to use profanity filter commands!");
            return;
        }

        if (args.length == 1) {
            sendProfanityHelp(sender);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "enable":
                plugin.getProfanityFilter().setEnabled(true);
                sender.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Profanity filter has been enabled!");
                break;

            case "disable":
                plugin.getProfanityFilter().setEnabled(false);
                sender.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Profanity filter has been disabled!");
                break;

            case "status":
                boolean isEnabled = plugin.getProfanityFilter().isEnabled();
                sender.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "Profanity filter is currently " + 
                    (isEnabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                break;

            case "list":
                Set<String> bannedWords = plugin.getProfanityFilter().getBannedWords();
                if (bannedWords.isEmpty()) {
                    sender.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "No words are currently banned.");
                } else {
                    sender.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "Banned words:");
                    sender.sendMessage(ChatColor.GRAY + String.join(", ", bannedWords));
                }
                break;

            case "add":
                if (args.length < 3) {
                    sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "Usage: /huechat profanity add <word>");
                    return;
                }
                plugin.getProfanityFilter().addBannedWord(args[2]);
                sender.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Added '" + args[2] + "' to banned words!");
                break;

            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "Usage: /huechat profanity remove <word>");
                    return;
                }
                plugin.getProfanityFilter().removeBannedWord(args[2]);
                sender.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Removed '" + args[2] + "' from banned words!");
                break;

            case "reload":
                plugin.getProfanityFilter().reloadBannedWords();
                sender.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Reloaded banned words list!");
                break;

            default:
                sendProfanityHelp(sender);
                break;
        }
    }

    private void handleColorCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "This command can only be used by players!");
            return;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("huechat.color")) {
            player.sendMessage(HueChat.getPrefix() + ChatColor.RED + "You don't have permission to use this command!");
            return;
        }

        if (plugin.getConfig().getBoolean("commands.gui", true)) {
            plugin.getColorGUI().openGUI(player);
        } else {
            player.sendMessage(HueChat.getPrefix() + ChatColor.RED + "The color GUI is disabled in the config!");
        }
    }

    private void handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("huechat.reload")) {
            sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "You don't have permission to use this command!");
            return;
        }

        plugin.reloadPlugin();
        sender.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Plugin reloaded successfully!");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "HueChat Commands:");
        if (sender.hasPermission("huechat.color")) {
            sender.sendMessage(ChatColor.GOLD + "/huechat color " + ChatColor.GRAY + "- Open color selection GUI");
        }
        if (sender.hasPermission("huechat.profanity")) {
            sender.sendMessage(ChatColor.GOLD + "/huechat profanity " + ChatColor.GRAY + "- Manage profanity filter");
        }
        if (sender.hasPermission("huechat.reload")) {
            sender.sendMessage(ChatColor.GOLD + "/huechat reload " + ChatColor.GRAY + "- Reload the plugin");
        }
        sender.sendMessage(ChatColor.GOLD + "/huechat help " + ChatColor.GRAY + "- Show this help message");
    }

    private void sendProfanityHelp(CommandSender sender) {
        sender.sendMessage(HueChat.getPrefix() + ChatColor.YELLOW + "Profanity Filter Commands:");
        sender.sendMessage(ChatColor.GOLD + "/huechat profanity enable " + ChatColor.GRAY + "- Enable the profanity filter");
        sender.sendMessage(ChatColor.GOLD + "/huechat profanity disable " + ChatColor.GRAY + "- Disable the profanity filter");
        sender.sendMessage(ChatColor.GOLD + "/huechat profanity status " + ChatColor.GRAY + "- Check if filter is enabled");
        sender.sendMessage(ChatColor.GOLD + "/huechat profanity list " + ChatColor.GRAY + "- List all banned words");
        sender.sendMessage(ChatColor.GOLD + "/huechat profanity add <word> " + ChatColor.GRAY + "- Add a word to the filter");
        sender.sendMessage(ChatColor.GOLD + "/huechat profanity remove <word> " + ChatColor.GRAY + "- Remove a word from the filter");
        sender.sendMessage(ChatColor.GOLD + "/huechat profanity reload " + ChatColor.GRAY + "- Reload the banned words list");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            if (sender.hasPermission("huechat.color")) commands.add("color");
            if (sender.hasPermission("huechat.profanity")) commands.add("profanity");
            if (sender.hasPermission("huechat.reload")) commands.add("reload");
            commands.add("help");

            return commands.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("profanity") && sender.hasPermission("huechat.profanity")) {
            return Arrays.asList("enable", "disable", "status", "list", "add", "remove", "reload")
                .stream()
                .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("profanity") && args[1].equalsIgnoreCase("remove")) {
            return new ArrayList<>(plugin.getProfanityFilter().getBannedWords())
                .stream()
                .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                .collect(Collectors.toList());
        }

        return completions;
    }

    private boolean handleColorCommand(Player player, String colorName) {
        if (!player.hasPermission("huechat.color." + colorName.toLowerCase()) && !player.hasPermission("huechat.color.*")) {
            player.sendMessage(HueChat.getPrefix() + ChatColor.RED + "You don't have permission to use this color!");
            return true;
        }

        if (colorName.equalsIgnoreCase("rainbow")) {
            plugin.getConfig().set("players." + player.getUniqueId() + ".color", "RAINBOW");
            plugin.saveConfig();
            player.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Your chat color has been set to " + makeRainbow("Rainbow"));
            return true;
        }

        ChatColor selectedColor = getColorFromName(colorName.toLowerCase());
        if (selectedColor == null) {
            player.sendMessage(HueChat.getPrefix() + ChatColor.RED + "Invalid color! Use tab completion to see available colors.");
            return true;
        }

        plugin.getConfig().set("players." + player.getUniqueId() + ".color", selectedColor.name());
        plugin.saveConfig();
        player.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Your chat color has been set to " + selectedColor + 
                         getProperColorName(colorName));
        return true;
    }

    private String getProperColorName(String colorName) {
        return switch (colorName.toLowerCase()) {
            case "yellow" -> "Yellow";
            case "lightpurple" -> "Light Purple";
            case "red" -> "Red";
            case "aqua" -> "Aqua";
            case "green" -> "Green";
            case "blue" -> "Blue";
            case "darkgray" -> "Dark Gray";
            case "gray" -> "Gray";
            case "gold" -> "Gold";
            case "darkpurple" -> "Dark Purple";
            case "darkred" -> "Dark Red";
            case "darkaqua" -> "Dark Aqua";
            case "darkblue" -> "Dark Blue";
            case "darkgreen" -> "Dark Green";
            default -> colorName;
        };
    }

    private ChatColor getColorFromName(String name) {
        return switch (name) {
            case "yellow" -> ChatColor.YELLOW;
            case "lightpurple" -> ChatColor.LIGHT_PURPLE;
            case "red" -> ChatColor.RED;
            case "aqua" -> ChatColor.AQUA;
            case "green" -> ChatColor.GREEN;
            case "blue" -> ChatColor.BLUE;
            case "darkgray" -> ChatColor.DARK_GRAY;
            case "gray" -> ChatColor.GRAY;
            case "gold" -> ChatColor.GOLD;
            case "darkpurple" -> ChatColor.DARK_PURPLE;
            case "darkred" -> ChatColor.DARK_RED;
            case "darkaqua" -> ChatColor.DARK_AQUA;
            case "darkblue" -> ChatColor.DARK_BLUE;
            case "darkgreen" -> ChatColor.DARK_GREEN;
            default -> null;
        };
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
} 