package com.skillforge.models;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerProfile {
    private final UUID playerId;
    private final String playerName;
    private final Map<SkillType, Integer> skillLevels;
    private final Map<SkillType, Long> skillExperience;
    private final Map<SkillType, Long> abilityCooldowns;
    private final Set<SkillType> activeAbilities;
    private UUID partyId;
    private long powerLevel;
    private long totalExperience;
    
    public PlayerProfile(UUID playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.skillLevels = new ConcurrentHashMap<>();
        this.skillExperience = new ConcurrentHashMap<>();
        this.abilityCooldowns = new ConcurrentHashMap<>();
        this.activeAbilities = ConcurrentHashMap.newKeySet();
        
        // Initialize all skills at level 1
        for (SkillType skill : SkillType.values()) {
            skillLevels.put(skill, 1);
            skillExperience.put(skill, 0L);
        }
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public int getSkillLevel(SkillType skill) {
        return skillLevels.getOrDefault(skill, 1);
    }
    
    public void setSkillLevel(SkillType skill, int level) {
        skillLevels.put(skill, level);
        calculatePowerLevel();
    }
    
    public long getSkillExperience(SkillType skill) {
        return skillExperience.getOrDefault(skill, 0L);
    }
    
    public void addSkillExperience(SkillType skill, long experience) {
        long currentExp = getSkillExperience(skill);
        skillExperience.put(skill, currentExp + experience);
        totalExperience += experience;
        calculatePowerLevel();
    }
    
    public boolean isAbilityOnCooldown(SkillType skill) {
        Long cooldownEnd = abilityCooldowns.get(skill);
        if (cooldownEnd == null) return false;
        return System.currentTimeMillis() < cooldownEnd;
    }
    
    public void setAbilityCooldown(SkillType skill, long cooldownDuration) {
        abilityCooldowns.put(skill, System.currentTimeMillis() + (cooldownDuration * 1000));
    }
    
    public long getAbilityCooldownRemaining(SkillType skill) {
        Long cooldownEnd = abilityCooldowns.get(skill);
        if (cooldownEnd == null) return 0;
        long remaining = cooldownEnd - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }
    
    public boolean hasActiveAbility(SkillType skill) {
        return activeAbilities.contains(skill);
    }
    
    public void activateAbility(SkillType skill) {
        activeAbilities.add(skill);
    }
    
    public void deactivateAbility(SkillType skill) {
        activeAbilities.remove(skill);
    }
    
    public UUID getPartyId() {
        return partyId;
    }
    
    public void setPartyId(UUID partyId) {
        this.partyId = partyId;
    }
    
    public boolean hasParty() {
        return partyId != null;
    }
    
    public long getPowerLevel() {
        return powerLevel;
    }
    
    public long getTotalExperience() {
        return totalExperience;
    }
    
    private void calculatePowerLevel() {
        powerLevel = skillLevels.values().stream().mapToInt(Integer::intValue).sum();
    }
}