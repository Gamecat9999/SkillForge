package com.skillforge.commands;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class PartyCommand implements CommandExecutor {
    private final SkillForge plugin;
    
    public PartyCommand(SkillForge plugin) {
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
            showPartyHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                createParty(player);
                break;
            case "invite":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party invite <player>");
                    return true;
                }
                invitePlayer(player, args[1]);
                break;
            case "leave":
                leaveParty(player);
                break;
            case "info":
                showPartyInfo(player);
                break;
            case "help":
                showPartyHelp(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown party command! Use /party help for help.");
                break;
        }
        
        return true;
    }
    
    private void createParty(Player player) {
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) return;
        
        if (profile.hasParty()) {
            player.sendMessage(ChatColor.RED + "You are already in a party!");
            return;
        }
        
        UUID partyId = plugin.getPartyManager().createParty(player);
        player.sendMessage(ChatColor.GREEN + "✦ Party created! You are now the party leader.");
    }
    
    private void invitePlayer(Player player, String targetName) {
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null || !profile.hasParty()) {
            player.sendMessage(ChatColor.RED + "You must be in a party to invite players!");
            return;
        }
        
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        
        PlayerProfile targetProfile = plugin.getPlayerManager().getProfile(target);
        if (targetProfile.hasParty()) {
            player.sendMessage(ChatColor.RED + "That player is already in a party!");
            return;
        }
        
        UUID partyId = profile.getPartyId();
        if (!plugin.getPartyManager().isPartyLeader(player, partyId)) {
            player.sendMessage(ChatColor.RED + "Only the party leader can invite players!");
            return;
        }
        
        if (plugin.getPartyManager().invitePlayer(partyId, target)) {
            player.sendMessage(ChatColor.GREEN + "✦ " + target.getName() + " has been invited to the party!");
            target.sendMessage(ChatColor.GREEN + "✦ You have been invited to " + player.getName() + "'s party!");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to invite player (party might be full)!");
        }
    }
    
    private void leaveParty(Player player) {
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null || !profile.hasParty()) {
            player.sendMessage(ChatColor.RED + "You are not in a party!");
            return;
        }
        
        plugin.getPartyManager().leaveParty(player);
        player.sendMessage(ChatColor.YELLOW + "You have left the party.");
    }
    
    private void showPartyInfo(Player player) {
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null || !profile.hasParty()) {
            player.sendMessage(ChatColor.RED + "You are not in a party!");
            return;
        }
        
        UUID partyId = profile.getPartyId();
        Set<UUID> members = plugin.getPartyManager().getPartyMembers(partyId);
        
        player.sendMessage(ChatColor.GOLD + "═══ Party Info ═══");
        player.sendMessage(ChatColor.AQUA + "Members (" + members.size() + "/6):");
        
        for (UUID memberId : members) {
            Player member = plugin.getServer().getPlayer(memberId);
            if (member != null) {
                String status = plugin.getPartyManager().isPartyLeader(member, partyId) ? " §6[Leader]" : "";
                player.sendMessage(ChatColor.WHITE + "- " + member.getName() + status);
            }
        }
        
        player.sendMessage(ChatColor.GOLD + "═══════════════");
    }
    
    private void showPartyHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "═══ Party Help ═══");
        player.sendMessage(ChatColor.YELLOW + "/party create" + ChatColor.WHITE + " - Create a new party");
        player.sendMessage(ChatColor.YELLOW + "/party invite <player>" + ChatColor.WHITE + " - Invite a player");
        player.sendMessage(ChatColor.YELLOW + "/party leave" + ChatColor.WHITE + " - Leave your party");
        player.sendMessage(ChatColor.YELLOW + "/party info" + ChatColor.WHITE + " - View party information");
    }
}