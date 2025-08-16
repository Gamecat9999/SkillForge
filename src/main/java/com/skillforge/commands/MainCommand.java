package com.skillforge.commands;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import com.skillforge.models.SkillType;
import com.skillforge.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {
    private final SkillForge plugin;
    
    public MainCommand(SkillForge plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showPlayerStats(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "stats":
            case "s":
                showPlayerStats(player);
                break;
            case "skill":
            case "sk":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /sf skill <skillname>");
                    return true;
                }
                showSkillStats(player, args[1]);
                break;
            case "ability":
            case "ab":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /sf ability <skillname>");
                    return true;
                }
                activateAbility(player, args[1]);
                break;
            case "help":
            case "h":
                showHelp(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown command! Use /sf help for help.");
                break;
        }
        
        return true;
    }
    
    private void showPlayerStats(Player player) {
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Could not load your profile!");
            return;
        }
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        player.sendMessage(ChatColor.YELLOW + "      " + player.getName() + "'s SkillForge Stats");
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        player.sendMessage(ChatColor.AQUA + "Power Level: " + ChatColor.WHITE + profile.getPowerLevel());
        player.sendMessage(ChatColor.AQUA + "Total XP: " + ChatColor.WHITE + profile.getTotalExperience());
        player.sendMessage("");
        
        for (SkillType skill : SkillType.values()) {
            if (!plugin.getSkillManager().isSkillEnabled(skill)) continue;
            
            int level = profile.getSkillLevel(skill);
            long xp = profile.getSkillExperience(skill);
            
            player.sendMessage(ChatColor.GREEN + skill.getIcon() + " " + skill.getDisplayName() + ": " + 
                             ChatColor.YELLOW + "Level " + level + ChatColor.GRAY + " (" + xp + " XP)");
        }
        
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
    }
    
    private void showSkillStats(Player player, String skillName) {
        SkillType skill = SkillType.fromString(skillName);
        if (skill == null) {
            player.sendMessage(ChatColor.RED + "Invalid skill name!");
            return;
        }
        
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) return;
        
        int level = profile.getSkillLevel(skill);
        long xp = profile.getSkillExperience(skill);
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══ " + skill.getIcon() + " " + skill.getDisplayName() + " ═══");
        player.sendMessage(ChatColor.AQUA + "Level: " + ChatColor.WHITE + level);
        player.sendMessage(ChatColor.AQUA + "Experience: " + ChatColor.WHITE + xp);
        
        // Show ability cooldowns
        if (profile.isAbilityOnCooldown(skill)) {
            long cooldown = profile.getAbilityCooldownRemaining(skill);
            player.sendMessage(ChatColor.RED + "Ability Cooldown: " + cooldown + "s");
        } else {
            player.sendMessage(ChatColor.GREEN + "Ability Ready!");
        }
        
        player.sendMessage(ChatColor.GOLD + "═══════════════════");
    }
    
    private void activateAbility(Player player, String skillName) {
        SkillType skill = SkillType.fromString(skillName);
        if (skill == null) {
            player.sendMessage(ChatColor.RED + "Invalid skill name!");
            return;
        }
        
        String abilityName = getMainAbilityName(skill);
        if (abilityName == null) {
            player.sendMessage(ChatColor.RED + "This skill doesn't have an activatable ability!");
            return;
        }
        
        if (plugin.getSkillManager().canUseAbility(player, skill, abilityName)) {
            plugin.getSkillManager().activateAbility(player, skill, abilityName);
        } else {
            PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
            if (profile.isAbilityOnCooldown(skill)) {
                long cooldown = profile.getAbilityCooldownRemaining(skill);
                player.sendMessage(ChatColor.RED + "Ability is on cooldown for " + cooldown + " seconds!");
            } else {
                player.sendMessage(ChatColor.RED + "You don't have the required level for this ability!");
            }
        }
    }
    
    private String getMainAbilityName(SkillType skill) {
        switch (skill) {
            case MINING: return "super-breaker";
            case WOODCUTTING: return "tree-feller";
            case EXCAVATION: return "giga-drill-breaker";
            case HERBALISM: return "green-terra";
            case ARCHERY: return "power-shot";
            case SWORDS: return "serrated-strikes";
            case AXES: return "skull-splitter";
            case UNARMED: return "berserk";
            default: return null;
        }
    }
    
    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "═══ SkillForge Help ═══");
        player.sendMessage(ChatColor.YELLOW + "/sf stats" + ChatColor.WHITE + " - View your skill stats");
        player.sendMessage(ChatColor.YELLOW + "/sf skill <name>" + ChatColor.WHITE + " - View specific skill info");
        player.sendMessage(ChatColor.YELLOW + "/sf ability <skill>" + ChatColor.WHITE + " - Activate skill ability");
        player.sendMessage(ChatColor.YELLOW + "/party" + ChatColor.WHITE + " - Party management commands");
        player.sendMessage(ChatColor.YELLOW + "/leaderboard [skill]" + ChatColor.WHITE + " - View leaderboards");
        player.sendMessage(ChatColor.YELLOW + "/inspect <player>" + ChatColor.WHITE + " - Inspect another player");
    }
}