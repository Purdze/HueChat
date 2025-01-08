package dev.purdze.huechat.hooks;

import dev.purdze.huechat.HueChat;
import dev.purdze.huechat.placeholders.HueChatExpansion;
import org.bukkit.Bukkit;

public class PlaceholderAPIHook {
    private final HueChat plugin;
    private boolean enabled = false;

    public PlaceholderAPIHook(HueChat plugin) {
        this.plugin = plugin;
        tryRegister();
    }

    private void tryRegister() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new HueChatExpansion(plugin).register();
            enabled = true;
            plugin.getLogger().info("Successfully hooked into PlaceholderAPI!");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
} 