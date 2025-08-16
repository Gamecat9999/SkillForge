# SkillForge 🔥⚔️

**The Ultimate LitRPG Experience for Minecraft**

SkillForge is a comprehensive RPG plugin that brings deep character progression, skills, and abilities to your Minecraft server. Inspired by classic MMORPG mechanics and similar to MCMMO, SkillForge offers an immersive LitRPG experience with modern features and polish.

## ✨ Features

### 🎯 Core Skills System
- **13 Unique Skills**: Mining, Woodcutting, Excavation, Herbalism, Archery, Swords, Axes, Unarmed, Taming, Fishing, Acrobatics, Repair, and Alchemy
- **Progressive Leveling**: Gain experience through normal gameplay activities
- **Power Level**: Combined level across all skills showing total progression
- **Skill Abilities**: Powerful temporary abilities that unlock at specific levels

### ⚡ Special Abilities
- **Mining**: Super Breaker, Blast Mining
- **Woodcutting**: Tree Feller, Leaf Blower  
- **Excavation**: Giga Drill Breaker
- **Herbalism**: Green Terra
- **Combat**: Serrated Strikes, Skull Splitter, Berserk
- **Archery**: Power Shot
- **And many more!**

### 🎉 Party System
- Form parties with friends
- Share experience gains
- Party-wide buffs and bonuses
- Distance-based experience sharing

### 🏆 Progression Features
- **Double Drops**: Chance increases with skill level
- **Treasure Hunting**: Find rare items while excavating
- **Green Thumb**: Auto-replant crops
- **Enhanced Fishing**: Better loot with higher levels
- **Combat Bonuses**: Increased damage and special effects

### 📊 Statistics & Leaderboards
- Detailed player statistics
- Server-wide leaderboards for each skill
- Player inspection system
- Power level rankings

### 🔧 Admin Features
- Highly configurable skill settings
- MySQL and SQLite database support
- Custom experience multipliers
- Ability cooldown management
- Comprehensive configuration files

## 🚀 Installation

1. Download the latest SkillForge JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure the plugin using the generated config files
5. Enjoy the LitRPG experience!

## ⚙️ Configuration

### Main Config (`config.yml`)
```yaml
general:
  max-level: 1000
  experience-multiplier: 1.0
  party-experience-share: true
  
database:
  type: sqlite # or mysql
  # MySQL settings if needed
```

### Skills Config (`skills.yml`)
Each skill can be individually configured with:
- Enable/disable toggle
- Custom experience multipliers
- Ability unlock levels
- Cooldown times
- Duration settings

## 📖 Commands

### Player Commands
- `/sf` or `/skills` - View your skill statistics
- `/sf skill <skillname>` - View specific skill details
- `/sf ability <skillname>` - Activate skill ability
- `/party create` - Create a new party
- `/party invite <player>` - Invite a player to your party
- `/party leave` - Leave your current party
- `/leaderboard [skill]` - View skill leaderboards
- `/inspect <player>` - View another player's stats

### Admin Commands
- `/sf reload` - Reload plugin configuration
- `/sf addxp <player> <skill> <amount>` - Add experience to a player
- `/sf setlevel <player> <skill> <level>` - Set a player's skill level

## 🎮 Gameplay

### Gaining Experience
- **Mining**: Break stone, ores, and minerals
- **Woodcutting**: Chop trees and wood blocks
- **Excavation**: Dig dirt, sand, gravel, and soul sand
- **Herbalism**: Harvest crops, sugar cane, and cactus
- **Combat**: Fight monsters and players with weapons
- **Archery**: Use bows and crossbows in combat
- **Fishing**: Catch fish and treasure
- **Taming**: Tame and breed animals
- **Acrobatics**: Take fall damage to build resistance
- **Repair**: Repair items at iron blocks
- **Alchemy**: Brew potions

### Using Abilities
1. Gain enough experience to unlock an ability
2. Right-click with the appropriate tool to activate
3. Enjoy powerful temporary bonuses!
4. Wait for cooldown to finish before using again

### Party Benefits
- Share experience with nearby party members
- Coordinate abilities for maximum effectiveness
- Build stronger communities through cooperation

## 🔌 API for Developers

SkillForge provides a comprehensive API for other plugins:

```java
// Get player's skill level
int level = SkillForge.getInstance()
    .getPlayerManager()
    .getProfile(player)
    .getSkillLevel(SkillType.MINING);

// Add experience
SkillForge.getInstance()
    .getPlayerManager()
    .addExperience(player, SkillType.MINING, 100);
```

## 🤝 Contributing

We welcome contributions! Please feel free to submit issues, feature requests, or pull requests on our GitHub repository.

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

Need help? Join our Discord community or create an issue on GitHub!

---

**Transform your Minecraft server into an epic LitRPG adventure with SkillForge!** ⚔️✨