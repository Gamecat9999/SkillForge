package com.skillforge.listeners;

import com.skillforge.SkillForge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final SkillForge plugin;
    
    public PlayerListener(SkillForge plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Load player profile when they join
        plugin.getPlayerManager().loadProfile(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save and unload player profile when they leave
        plugin.getPlayerManager().saveProfile(event.getPlayer());
        plugin.getPlayerManager().unloadProfile(event.getPlayer().getUniqueId());
    }
}