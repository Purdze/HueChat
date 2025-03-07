# HueChat

A simple yet powerful chat color and mentions plugin for Minecraft servers.

## New in Version 1.2.1

### 🌟 Major Updates
- **Legacy Support**: Now compatible with Minecraft versions 1.8 to latest
- **Improved Update Checker**: Switched to Spigot's legacy API for more reliable update checking
- **Self-Mention Support**: Players can now trigger mentions of their own name
- **Emoji Support**: Players can now use emojis in chat with permission-based control

### 🔧 Technical Changes
- Multi-Java support:
  - Java 8 for legacy servers (MC 1.8-1.16)
  - Java 17 for modern servers (MC 1.17-1.19)
  - Java 21 for newest servers (MC 1.20+)
- Added legacy sound support for 1.8 servers
- Improved version detection system
- Optimized update notifications

### 🎨 Features
- Chat color customization
- Rainbow text support
- Player mentions with sound and color highlighting
- GUI color selector (toggleable)
- PlaceholderAPI support
- Configurable mention sounds
- Permission-based update notifications
- Emoji support with categories:
  - Smileys (e.g., :smile:, :wink:)
  - Hearts (e.g., :heart:, :broken_heart:)
  - Symbols (e.g., :star:, :warning:)
  - And more!

## Permissions
- `huechat.update` - Receive update notifications
- `huechat.color.*` - Access to all colors
- `huechat.gui` - Access to color GUI
- `huechat.reload` - Permission to reload plugin
- `huechat.mention` - Permission to use mentions
- `huechat.emoji.*` - Access to all emojis
- `huechat.emoji.<emojiname>` - Permission for specific emojis

## Commands
- `/huechat` - Open color selection menu
- `/huechat help` - Show help message
- `/huechat reload` - Reload configuration
- `/huechat emojis` - View available emojis

## Configuration
All settings can be found in `config.yml`. The plugin will automatically backup your old config as `config.old.yml` when updating.

## Support
For support, please visit our [Spigot page](https://www.spigotmc.org/resources/121702) 