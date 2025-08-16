package com.skillforge.commands;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import com.skillforge.models.SkillType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InspectCommand implements CommandExecutor {
    private final SkillForge plugin;
    
    public InspectCommand(SkillForge plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /inspect <player>");
            return true;
        }
        
        String targetName = args[0];
        Player target = plugin.getServer().getPlayer(targetName);
        
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        
        PlayerProfile profile = plugin.getPlayerManager().getProfile(target);
        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "Could not load player profile!");
            return true;
        }
        
        showPlayerInspection(sender, target, profile);
        return true;
    }
    
    private void showPlayerInspection(CommandSender sender, Player target, PlayerProfile profile) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        sender.sendMessage(ChatColor.YELLOW + "      " + target.getName() + "'s SkillForge Stats");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        sender.sendMessage(ChatColor.AQUA + "Power Level: " + ChatColor.WHITE + profile.getPowerLevel());
        sender.sendMessage(ChatColor.AQUA + "Total XP: " + ChatColor.WHITE + profile.getTotalExperience());
        sender.sendMessage("");
        
        // Show top 5 skills
        sender.sendMessage(ChatColor.YELLOW + "Top Skills:");
        for (SkillType skill : SkillType.values()) {
            if (!plugin.getSkillManager().isSkillEnabled(skill)) continue;
            
            int level = profile.getSkillLevel(skill);
            if (level > 1) { // Only show skills above level 1
                sender.sendMessage(ChatColor.GREEN + skill.getIcon() + " " + skill.getDisplayName() + ": " + 
                                 ChatColor.YELLOW + "Level " + level);
            }
        }
        
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
    }
}