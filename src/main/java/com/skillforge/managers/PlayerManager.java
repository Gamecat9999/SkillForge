package com.skillforge.managers;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import com.skillforge.models.SkillType;
import com.skillforge.utils.ExperienceUtils;
import com.skillforge.utils.MessageUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private final SkillForge plugin;
    private final Map<UUID, PlayerProfile> profiles;
    
    public PlayerManager(SkillForge plugin) {
        this.plugin = plugin;
        this.profiles = new ConcurrentHashMap<>();
    }
    
    public PlayerProfile getProfile(UUID playerId) {
        return profiles.get(playerId);
    }
    
    public PlayerProfile getProfile(Player player) {
        return getProfile(player.getUniqueId());
    }
    
    public void loadProfile(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerProfile profile = plugin.getDatabaseManager().loadPlayerProfile(playerId, player.getName());
        profiles.put(playerId, profile);
    }
    
    public void saveProfile(Player player) {
        PlayerProfile profile = getProfile(player);
        if (profile != null) {
            plugin.getDatabaseManager().savePlayerProfile(profile);
        }
    }
    
    public void unloadProfile(UUID playerId) {
        profiles.remove(playerId);
    }
    
    public void addExperience(Player player, SkillType skill, long experience) {
        PlayerProfile profile = getProfile(player);
        if (profile == null) return;
        
        int oldLevel = profile.getSkillLevel(skill);
        profile.addSkillExperience(skill, experience);
        
        // Check for level up
        int newLevel = ExperienceUtils.getLevel(profile.getSkillExperience(skill));
        if (newLevel > oldLevel) {
            profile.setSkillLevel(skill, newLevel);
            handleLevelUp(player, skill, newLevel);
        }
        
        // Show experience gain
        MessageUtils.sendExperienceMessage(player, skill, experience);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);
    }
    
    private void handleLevelUp(Player player, SkillType skill, int newLevel) {
        // Send level up message
        MessageUtils.sendLevelUpMessage(player, skill, newLevel);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
        // Check for new abilities
        plugin.getSkillManager().checkForNewAbilities(player, skill, newLevel);
        
        // Party experience sharing
        if (getProfile(player).hasParty()) {
            plugin.getPartyManager().shareExperience(player, skill, newLevel * 10);
        }
    }
}