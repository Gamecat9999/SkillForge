package com.skillforge.managers;

import com.skillforge.SkillForge;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final SkillForge plugin;
    private FileConfiguration skillsConfig;
    private File skillsConfigFile;
    
    public ConfigManager(SkillForge plugin) {
        this.plugin = plugin;
        setupConfigs();
    }
    
    private void setupConfigs() {
        // Save default config if it doesn't exist
        plugin.saveDefaultConfig();
        
        // Setup skills.yml
        skillsConfigFile = new File(plugin.getDataFolder(), "skills.yml");
        if (!skillsConfigFile.exists()) {
            plugin.saveResource("skills.yml", false);
        }
        skillsConfig = YamlConfiguration.loadConfiguration(skillsConfigFile);
    }
    
    public FileConfiguration getSkillsConfig() {
        return skillsConfig;
    }
    
    public void reloadConfigs() {
        plugin.reloadConfig();
        skillsConfig = YamlConfiguration.loadConfiguration(skillsConfigFile);
    }
    
    public void saveSkillsConfig() {
        try {
            skillsConfig.save(skillsConfigFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save skills.yml: " + e.getMessage());
        }
    }
}