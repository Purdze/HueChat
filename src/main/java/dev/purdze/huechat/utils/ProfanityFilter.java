package dev.purdze.huechat.utils;

import dev.purdze.huechat.HueChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ProfanityFilter {
    private final HueChat plugin;
    private final Set<String> bannedWords;
    private boolean enabled;
    private File bannedWordsFile;
    private FileConfiguration bannedWordsConfig;
    private static final String REPLACEMENT = "****";

    public ProfanityFilter(HueChat plugin) {
        this.plugin = plugin;
        this.bannedWords = new HashSet<>();
        loadConfiguration();
    }

    private void loadConfiguration() {
        // Load settings from main config
        FileConfiguration config = plugin.getConfig();
        this.enabled = config.getBoolean("profanity-filter.enabled", true);

        // Initialize banned words file
        this.bannedWordsFile = new File(plugin.getDataFolder(), "banned-words.yml");
        if (!bannedWordsFile.exists()) {
            plugin.saveResource("banned-words.yml", false);
        }

        // Load banned words
        this.bannedWordsConfig = YamlConfiguration.loadConfiguration(bannedWordsFile);
        List<String> words = bannedWordsConfig.getStringList("banned-words");
        bannedWords.addAll(words);
    }

    public void reloadBannedWords() {
        bannedWordsConfig = YamlConfiguration.loadConfiguration(bannedWordsFile);
        bannedWords.clear();
        bannedWords.addAll(bannedWordsConfig.getStringList("banned-words"));
    }

    public void addBannedWord(String word) {
        bannedWords.add(word.toLowerCase());
        List<String> words = bannedWordsConfig.getStringList("banned-words");
        words.add(word.toLowerCase());
        bannedWordsConfig.set("banned-words", words);
        try {
            bannedWordsConfig.save(bannedWordsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save banned words: " + e.getMessage());
        }
    }

    public void removeBannedWord(String word) {
        bannedWords.remove(word.toLowerCase());
        List<String> words = bannedWordsConfig.getStringList("banned-words");
        words.remove(word.toLowerCase());
        bannedWordsConfig.set("banned-words", words);
        try {
            bannedWordsConfig.save(bannedWordsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save banned words: " + e.getMessage());
        }
    }

    public String filterMessage(String message) {
        if (!enabled) return message;

        String filteredMessage = message;
        for (String word : bannedWords) {
            // Create a pattern that matches the word with word boundaries
            String pattern = "\\b" + Pattern.quote(word) + "\\b";
            filteredMessage = filteredMessage.replaceAll("(?i)" + pattern, REPLACEMENT);
        }
        return filteredMessage;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        plugin.getConfig().set("profanity-filter.enabled", enabled);
        plugin.saveConfig();
    }

    public Set<String> getBannedWords() {
        return new HashSet<>(bannedWords);
    }
} 