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
            if (!(sender instanceof Player)) {
                sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "This command can only be used by players!");
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("huechat.gui")) {
                player.sendMessage(HueChat.getPrefix() + ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }
            if (!plugin.getConfig().getBoolean("commands.gui", true)) {
                player.sendMessage(HueChat.getPrefix() + ChatColor.RED + "The GUI feature is currently disabled!");
                return true;
            }
            ColorGUI.getInstance(plugin).openGUI(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                HelpCommand.execute(sender);
                return true;
            case "reload":
                ReloadCommand.execute(sender, plugin);
                return true;
            case "color":
                if (!plugin.getConfig().getBoolean("commands.color", true)) {
                    sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "The color command is currently disabled!");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "This command can only be used by players!");
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "Usage: /" + label + " color <color>");
                    return true;
                }
                return handleColorCommand((Player) sender, args[1]);
            case "mention":
                return MentionCommand.execute(sender, plugin, label, args);
            default:
                sender.sendMessage(HueChat.getPrefix() + ChatColor.RED + "Unknown command. Use /" + label + " help for help.");
                return true;
        }
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("huechat.gui")) completions.add("help");
            if (sender.hasPermission("huechat.reload")) completions.add("reload");
            if (sender instanceof Player) {
                completions.add("color");
                completions.add("mention");
            }
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("mention")) {
                return Arrays.asList("toggle").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
            if (args[0].equalsIgnoreCase("color") && sender instanceof Player) {
                Player player = (Player) sender;
                return colors.stream()
                    .filter(color -> color.startsWith(args[1].toLowerCase()))
                    .filter(color -> player.hasPermission("huechat.color." + color) || player.hasPermission("huechat.color.*"))
                    .collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
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