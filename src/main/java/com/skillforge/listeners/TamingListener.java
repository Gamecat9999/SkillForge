package com.skillforge.listeners;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import com.skillforge.models.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class TamingListener implements Listener {
    private final SkillForge plugin;
    
    public TamingListener(SkillForge plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player)) return;
        
        Player player = (Player) event.getOwner();
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) return;
        
        // Award taming experience
        long experience = getTamingExperience(event.getEntity().getType());
        plugin.getPlayerManager().addExperience(player, SkillType.TAMING, experience);
    }
    
    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player)) return;
        
        Player player = (Player) event.getBreeder();
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) return;
        
        // Award breeding experience
        long experience = getBreedingExperience(event.getEntity().getType());
        plugin.getPlayerManager().addExperience(player, SkillType.TAMING, experience);
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) return;
        
        // Beast Lore ability - right click to see animal stats
        if (profile.getSkillLevel(SkillType.TAMING) >= 100) {
            if (event.getRightClicked() instanceof Wolf) {
                Wolf wolf = (Wolf) event.getRightClicked();
                showBeastLore(player, wolf);
            }
        }
    }
    
    private long getTamingExperience(org.bukkit.entity.EntityType entityType) {
        switch (entityType) {
            case WOLF:
                return 100;
            case CAT:
                return 80;
            case HORSE:
                return 150;
            case PARROT:
                return 60;
            default:
                return 50;
        }
    }
    
    private long getBreedingExperience(org.bukkit.entity.EntityType entityType) {
        return getTamingExperience(entityType) / 2;
    }
    
    private void showBeastLore(Player player, Wolf wolf) {
        player.sendMessage("§6═══ §eBeast Lore §6═══");
        player.sendMessage("§7Health: §f" + wolf.getHealth() + "/" + wolf.getMaxHealth());
        player.sendMessage("§7Owner: §f" + (wolf.getOwner() != null ? wolf.getOwner().getName() : "Wild"));
        player.sendMessage("§7Sitting: §f" + wolf.isSitting());
        player.sendMessage("§6═══════════════════");
    }
}