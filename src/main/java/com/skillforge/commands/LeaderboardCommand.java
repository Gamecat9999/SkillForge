package com.skillforge.commands;

import com.skillforge.SkillForge;
import com.skillforge.models.SkillType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardCommand implements CommandExecutor {
    private final SkillForge plugin;
    
    public LeaderboardCommand(SkillForge plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showPowerLevelLeaderboard(sender);
        } else {
            String skillName = args[0];
            SkillType skill = SkillType.fromString(skillName);
            if (skill == null) {
                sender.sendMessage(ChatColor.RED + "Invalid skill name!");
                return true;
            }
            showSkillLeaderboard(sender, skill);
        }
        
        return true;
    }
    
    private void showPowerLevelLeaderboard(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "═══ Power Level Leaderboard ═══");
        sender.sendMessage(ChatColor.GRAY + "Feature coming soon!");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
    }
    
    private void showSkillLeaderboard(CommandSender sender, SkillType skill) {
        sender.sendMessage(ChatColor.GOLD + "═══ " + skill.getDisplayName() + " Leaderboard ═══");
        sender.sendMessage(ChatColor.GRAY + "Feature coming soon!");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
    }
}