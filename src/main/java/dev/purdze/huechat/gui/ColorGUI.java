package dev.purdze.huechat.gui;

import dev.purdze.huechat.HueChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.event.HandlerList;

public class ColorGUI implements Listener {

    private final HueChat plugin;
    private final Inventory gui;
    private static final String GUI_TITLE = "Chat Color Selector";
    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4");
    private static ColorGUI instance;

    public static ColorGUI getInstance(HueChat plugin) {
        if (instance == null) {
            instance = new ColorGUI(plugin);
        }
        return instance;
    }

    public static void destroyInstance() {
        if (instance != null) {
            HandlerList.unregisterAll(instance);
            instance = null;
        }
    }

    private ColorGUI(HueChat plugin) {
        this.plugin = plugin;
        this.gui = Bukkit.createInventory(null, 54, GUI_TITLE);
        initializeItems();
        HandlerList.unregisterAll(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private PlayerProfile getProfile(String textureUrl) {
        PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID);
        PlayerTextures textures = profile.getTextures();
        try {
            URL urlObject = new URL(textureUrl);
            textures.setSkin(urlObject);
            profile.setTextures(textures);
        } catch (MalformedURLException exception) {
            plugin.getLogger().warning("Invalid texture URL: " + textureUrl);
        }
        return profile;
    }

    private void initializeItems() {
        // First row (10-16)
        addColorItem(ChatColor.YELLOW, "Yellow", 10, "http://textures.minecraft.net/texture/1f7a7de25b164f899bd6e8a2aa5956e86e7841e82273b1f8790622fc6275e9");
        addColorItem(ChatColor.LIGHT_PURPLE, "Light Purple", 11, "http://textures.minecraft.net/texture/dcf2835180cbfec3b317d6a47491a74ae71435ba169a57925b9096ea2f9c61b6");
        addColorItem(ChatColor.RED, "Red", 12, "http://textures.minecraft.net/texture/63f79b207d61e122523b83d61508d99cfa079d45bf23df2a9a5127f9071d4b00");
        addColorItem(ChatColor.AQUA, "Aqua", 13, "http://textures.minecraft.net/texture/2d6a8b47da923b7d10142447fdbdcfd1e8e82eb484964252bb36ddb5f73b51c2");
        addColorItem(ChatColor.GREEN, "Green", 14, "http://textures.minecraft.net/texture/925b8eed5c565bd440ec47c79c20d5cf370162b1d9b5dd3100ed6283fe01d6e");
        addColorItem(ChatColor.BLUE, "Blue", 15, "http://textures.minecraft.net/texture/3f9c32138c9764c639aebd819cd91992aed01bf448f0e710a03ab443ac490ee9");
        addColorItem(ChatColor.DARK_GRAY, "Dark Gray", 16, "http://textures.minecraft.net/texture/ff9bb9e56125c8227b94bbda9f6e0f862931c229255ba8f1205d13c44c1bb561");

        // Second row (19-25)
        addColorItem(ChatColor.GRAY, "Gray", 19, "http://textures.minecraft.net/texture/1c8e0ddf2432f4332b87691b5952c7679763ef4f275b874e9bceb888ed5b5b9");
        addColorItem(ChatColor.GOLD, "Gold", 20, "http://textures.minecraft.net/texture/2090d09e173ee34138c3b01b48ee0be534bbb1ace0ddf5ff98e66f7b02113995");
        addColorItem(ChatColor.DARK_PURPLE, "Dark Purple", 21, "http://textures.minecraft.net/texture/9b82e72b8e4832e5a114ab0fc127c8acb83f31fd4d266d08b2cacc5b6401a400");
        addColorItem(ChatColor.DARK_RED, "Dark Red", 22, "http://textures.minecraft.net/texture/68d40935279771adc63936ed9c8463abdf5c5ba78d2e86cb1ec10b4d1d225fb");
        addColorItem(ChatColor.DARK_AQUA, "Dark Aqua", 23, "http://textures.minecraft.net/texture/31f57051130e850848e8e37e72110a16f09dbdab7d9d6e33a9fecfd348d5a110");
        addColorItem(ChatColor.DARK_BLUE, "Dark Blue", 24, "http://textures.minecraft.net/texture/e1194fe9edf583c0ebe7dc1d34309beefb229bb15b6a8c3c7b0c76de27c8b7bf");
        addColorItem(ChatColor.DARK_GREEN, "Dark Green", 25, "http://textures.minecraft.net/texture/8e9b27fccd80921bd263c91dc511d09e9a746555e6c9cad52e8562ed0182a2f");

        // Rainbow color (31)
        addColorItem(null, "Rainbow", 31, "http://textures.minecraft.net/texture/6b50d67a16bfea416ba52eaad8b198d287fffa13a6837512d7a17348f3093310");

        // Add exit button
        ItemStack exitButton = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitButton.getItemMeta();
        exitMeta.setDisplayName(ChatColor.RED + "Exit");
        exitMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to close the menu"));
        exitButton.setItemMeta(exitMeta);
        gui.setItem(49, exitButton);
    }

    private void addColorItem(ChatColor color, String name, int slot, String textureId) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwnerProfile(getProfile(textureId));
        
        // Special handling for rainbow
        if (name.equals("Rainbow")) {
            meta.setDisplayName(makeRainbow("Rainbow"));
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Click to set your chat color to " + makeRainbow("Rainbow")
            ));
        } else {
            meta.setDisplayName(color + name);
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Click to set your chat color to " + color + name
            ));
        }
        head.setItemMeta(meta);
        gui.setItem(slot, head);
    }

    public void openGUI(Player player) {
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getClick().isShiftClick()) return;
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        if (clickedItem.getItemMeta() == null) return;
        
        String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
        
        if (displayName.equals("Rainbow")) {
            if (!player.hasPermission("huechat.color.rainbow") && !player.hasPermission("huechat.color.*")) {
                player.sendMessage(HueChat.getPrefix() + ChatColor.RED + "You don't have permission to use this color!");
                return;
            }
            plugin.getConfig().set("players." + player.getUniqueId() + ".color", "RAINBOW");
            plugin.saveConfig();
            player.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Your chat color has been set to " + makeRainbow("Rainbow"));
            player.closeInventory();
            return;
        }
        
        ChatColor selectedColor = ChatColor.getByChar(clickedItem.getItemMeta().getDisplayName().charAt(1));
        if (selectedColor == null) return;
        
        String colorName = getColorName(selectedColor, clickedItem).toLowerCase();
        if (!player.hasPermission("huechat.color." + colorName) && !player.hasPermission("huechat.color.*")) {
            player.sendMessage(HueChat.getPrefix() + ChatColor.RED + "You don't have permission to use this color!");
            return;
        }

        plugin.getConfig().set("players." + player.getUniqueId() + ".color", selectedColor.name());
        plugin.saveConfig();
        player.sendMessage(HueChat.getPrefix() + ChatColor.GREEN + "Your chat color has been set to " + selectedColor + 
                          ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()));
        player.closeInventory();
    }

    private String getColorName(ChatColor color, ItemStack item) {
        return switch (color) {
            case YELLOW -> "yellow";
            case LIGHT_PURPLE -> {
                String name = item.getItemMeta().getDisplayName();
                yield name.contains("Rainbow") ? "rainbow" : "lightpurple";
            }
            case RED -> "red";
            case AQUA -> "aqua";
            case GREEN -> "green";
            case BLUE -> "blue";
            case DARK_GRAY -> "darkgray";
            case GRAY -> "gray";
            case GOLD -> "gold";
            case DARK_PURPLE -> "darkpurple";
            case DARK_RED -> "darkred";
            case DARK_AQUA -> "darkaqua";
            case DARK_BLUE -> "darkblue";
            case DARK_GREEN -> "darkgreen";
            default -> color.name().toLowerCase();
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