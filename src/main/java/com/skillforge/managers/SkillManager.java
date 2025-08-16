package com.skillforge.managers;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import com.skillforge.models.SkillType;
import com.skillforge.utils.MessageUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class SkillManager {
    private final SkillForge plugin;
    private final Map<SkillType, Map<String, Object>> skillConfigs;
    
    public SkillManager(SkillForge plugin) {
        this.plugin = plugin;
        this.skillConfigs = new HashMap<>();
        loadSkillConfigs();
    }
    
    private void loadSkillConfigs() {
        for (SkillType skill : SkillType.values()) {
            String path = "skills." + skill.name().toLowerCase();
            if (plugin.getConfigManager().getSkillsConfig().contains(path)) {
                skillConfigs.put(skill, plugin.getConfigManager().getSkillsConfig().getConfigurationSection(path).getValues(true));
            }
        }
    }
    
    public boolean isSkillEnabled(SkillType skill) {
        return (boolean) skillConfigs.getOrDefault(skill, new HashMap<>()).getOrDefault("enabled", true);
    }
    
    public void checkForNewAbilities(Player player, SkillType skill, int level) {
        Map<String, Object> config = skillConfigs.get(skill);
        if (config == null || !config.containsKey("abilities")) return;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> abilities = (Map<String, Object>) config.get("abilities");
        
        for (String abilityName : abilities.keySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> abilityConfig = (Map<String, Object>) abilities.get(abilityName);
            int unlockLevel = (int) abilityConfig.getOrDefault("unlock-level", 50);
            
            if (level == unlockLevel) {
                MessageUtils.sendAbilityUnlockedMessage(player, skill, abilityName);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 2.0f);
            }
        }
    }
    
    public boolean canUseAbility(Player player, SkillType skill, String abilityName) {
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) return false;
        
        if (profile.isAbilityOnCooldown(skill)) return false;
        if (profile.hasActiveAbility(skill)) return false;
        
        Map<String, Object> config = skillConfigs.get(skill);
        if (config == null) return false;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> abilities = (Map<String, Object>) config.get("abilities");
        if (abilities == null || !abilities.containsKey(abilityName)) return false;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> abilityConfig = (Map<String, Object>) abilities.get(abilityName);
        int unlockLevel = (int) abilityConfig.getOrDefault("unlock-level", 50);
        
        return profile.getSkillLevel(skill) >= unlockLevel;
    }
    
    public void activateAbility(Player player, SkillType skill, String abilityName) {
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) return;
        
        Map<String, Object> config = skillConfigs.get(skill);
        if (config == null) return;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> abilities = (Map<String, Object>) config.get("abilities");
        @SuppressWarnings("unchecked")
        Map<String, Object> abilityConfig = (Map<String, Object>) abilities.get(abilityName);
        
        int duration = (int) abilityConfig.getOrDefault("duration", 30);
        int cooldown = (int) abilityConfig.getOrDefault("cooldown", 240);
        
        // Activate ability
        profile.activateAbility(skill);
        profile.setAbilityCooldown(skill, cooldown);
        
        MessageUtils.sendAbilityActivatedMessage(player, abilityName, duration);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 1.2f);
        
        // Schedule deactivation
        new BukkitRunnable() {
            @Override
            public void run() {
                profile.deactivateAbility(skill);
            }
        }.runTaskLater(plugin, duration * 20L);
    }
    
    public double getExperienceMultiplier(SkillType skill) {
        Map<String, Object> config = skillConfigs.get(skill);
        if (config == null) return 1.0;
        return (double) config.getOrDefault("experience-multiplier", 1.0);
    }
}