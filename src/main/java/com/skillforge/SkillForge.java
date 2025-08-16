package com.skillforge;

import com.skillforge.commands.*;
import com.skillforge.listeners.*;
import com.skillforge.managers.*;
import com.skillforge.database.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillForge extends JavaPlugin {
    
    private static SkillForge instance;
    private DatabaseManager databaseManager;
    private PlayerManager playerManager;
    private SkillManager skillManager;
    private PartyManager partyManager;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        configManager = new ConfigManager(this);
        databaseManager = new DatabaseManager(this);
        skillManager = new SkillManager(this);
        playerManager = new PlayerManager(this);
        partyManager = new PartyManager(this);
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        // Initialize database
        databaseManager.initialize();
        
        getLogger().info("SkillForge has been enabled! LitRPG adventures await!");
    }
    
    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("SkillForge has been disabled!");
    }
    
    private void registerCommands() {
        getCommand("sf").setExecutor(new MainCommand(this));
        getCommand("party").setExecutor(new PartyCommand(this));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));
        getCommand("inspect").setExecutor(new InspectCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(new TamingListener(this), this);
    }
    
    public static SkillForge getInstance() {
        return instance;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public SkillManager getSkillManager() {
        return skillManager;
    }
    
    public PartyManager getPartyManager() {
        return partyManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}