package com.skillforge.managers;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import com.skillforge.models.SkillType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PartyManager {
    private final SkillForge plugin;
    private final Map<UUID, Set<UUID>> parties; // Party ID -> Set of player UUIDs
    private final Map<UUID, UUID> partyLeaders; // Party ID -> Leader UUID
    
    public PartyManager(SkillForge plugin) {
        this.plugin = plugin;
        this.parties = new ConcurrentHashMap<>();
        this.partyLeaders = new ConcurrentHashMap<>();
    }
    
    public UUID createParty(Player leader) {
        UUID partyId = UUID.randomUUID();
        Set<UUID> members = new HashSet<>();
        members.add(leader.getUniqueId());
        
        parties.put(partyId, members);
        partyLeaders.put(partyId, leader.getUniqueId());
        
        PlayerProfile profile = plugin.getPlayerManager().getProfile(leader);
        if (profile != null) {
            profile.setPartyId(partyId);
        }
        
        return partyId;
    }
    
    public boolean invitePlayer(UUID partyId, Player invitee) {
        Set<UUID> members = parties.get(partyId);
        if (members == null || members.size() >= 6) return false; // Max 6 players per party
        
        members.add(invitee.getUniqueId());
        PlayerProfile profile = plugin.getPlayerManager().getProfile(invitee);
        if (profile != null) {
            profile.setPartyId(partyId);
        }
        
        return true;
    }
    
    public void leaveParty(Player player) {
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null || !profile.hasParty()) return;
        
        UUID partyId = profile.getPartyId();
        Set<UUID> members = parties.get(partyId);
        if (members != null) {
            members.remove(player.getUniqueId());
            
            // If party is empty, remove it
            if (members.isEmpty()) {
                parties.remove(partyId);
                partyLeaders.remove(partyId);
            }
            // If leader left, assign new leader
            else if (partyLeaders.get(partyId).equals(player.getUniqueId())) {
                UUID newLeader = members.iterator().next();
                partyLeaders.put(partyId, newLeader);
            }
        }
        
        profile.setPartyId(null);
    }
    
    public void shareExperience(Player player, SkillType skill, long experience) {
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null || !profile.hasParty()) return;
        
        UUID partyId = profile.getPartyId();
        Set<UUID> members = parties.get(partyId);
        if (members == null) return;
        
        double maxDistance = plugin.getConfig().getDouble("general.party-max-distance", 75.0);
        long sharedExp = experience / 4; // Share 25% of experience
        
        for (UUID memberId : members) {
            if (memberId.equals(player.getUniqueId())) continue;
            
            Player member = plugin.getServer().getPlayer(memberId);
            if (member == null || !member.isOnline()) continue;
            
            // Check distance
            if (player.getLocation().distance(member.getLocation()) > maxDistance) continue;
            
            // Give shared experience
            plugin.getPlayerManager().addExperience(member, skill, sharedExp);
        }
    }
    
    public Set<UUID> getPartyMembers(UUID partyId) {
        return parties.getOrDefault(partyId, new HashSet<>());
    }
    
    public boolean isPartyLeader(Player player, UUID partyId) {
        UUID leaderId = partyLeaders.get(partyId);
        return leaderId != null && leaderId.equals(player.getUniqueId());
    }
}