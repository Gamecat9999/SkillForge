package com.skillforge.utils;

public class ExperienceUtils {
    
    public static int getLevel(long experience) {
        if (experience < 0) return 1;
        
        // Formula similar to MCMMO: uses exponential curve
        // Level = floor(sqrt(experience / 1020) + 1)
        return (int) Math.floor(Math.sqrt(experience / 1020.0) + 1);
    }
    
    public static long getExperienceForLevel(int level) {
        if (level <= 1) return 0;
        // Reverse formula: experience = (level - 1)² × 1020
        return (long) Math.pow(level - 1, 2) * 1020;
    }
    
    public static long getExperienceToNextLevel(long currentExperience) {
        int currentLevel = getLevel(currentExperience);
        long nextLevelExperience = getExperienceForLevel(currentLevel + 1);
        return nextLevelExperience - currentExperience;
    }
    
    public static double getProgressToNextLevel(long currentExperience) {
        int currentLevel = getLevel(currentExperience);
        long currentLevelExperience = getExperienceForLevel(currentLevel);
        long nextLevelExperience = getExperienceForLevel(currentLevel + 1);
        
        double progress = (double) (currentExperience - currentLevelExperience) / 
                         (nextLevelExperience - currentLevelExperience);
        
        return Math.max(0, Math.min(1, progress));
    }
}