package dev.purdze.huechat.placeholders;

import dev.purdze.huechat.HueChat;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HueChatExpansion extends PlaceholderExpansion {
    private final HueChat plugin;

    public HueChatExpansion(HueChat plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "huechat";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "Purdze";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        if (params.equals("color")) {
            String colorName = plugin.getConfig().getString("players." + player.getUniqueId() + ".color");
            if (colorName == null) return "";
            try {
                ChatColor color = ChatColor.valueOf(colorName);
                return color + colorName;
            } catch (IllegalArgumentException e) {
                return "";
            }
        }

        return null;
    }
} 