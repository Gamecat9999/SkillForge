package com.skillforge.listeners;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import com.skillforge.models.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CombatListener implements Listener {
    private final SkillForge plugin;
    private final Random random = new Random();
    
    public CombatListener(SkillForge plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();
        
        // Handle direct player attacks
        if (damager instanceof Player) {
            Player player = (Player) damager;
            handlePlayerAttack(player, victim, event);
        }
        
        // Handle projectile attacks (arrows)
        if (damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;
            if (projectile.getShooter() instanceof Player) {
                Player shooter = (Player) projectile.getShooter();
                handleArcheryAttack(shooter, victim, event);
            }
        }
    }
    
    private void handlePlayerAttack(Player player, Entity victim, EntityDamageByEntityEvent event) {
        if (!(victim instanceof LivingEntity)) return;
        
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) return;
        
        ItemStack weapon = player.getInventory().getItemInMainHand();
        Material weaponType = weapon.getType();
        
        // Determine skill type and handle accordingly
        if (isUnarmedAttack(weaponType)) {
            handleUnarmedCombat(player, (LivingEntity) victim, event, profile);
        } else if (isSwordWeapon(weaponType)) {
            handleSwordsCombat(player, (LivingEntity) victim, event, profile);
        } else if (isAxeWeapon(weaponType)) {
            handleAxesCombat(player, (LivingEntity) victim, event, profile);
        }
    }
    
    private void handleUnarmedCombat(Player player, LivingEntity victim, EntityDamageByEntityEvent event, PlayerProfile profile) {
        // Award experience
        long experience = getUnarmedExperience(victim);
        plugin.getPlayerManager().addExperience(player, SkillType.UNARMED, experience);
        
        int level = profile.getSkillLevel(SkillType.UNARMED);
        double damage = event.getDamage();
        
        // Berserk ability effects
        if (profile.hasActiveAbility(SkillType.UNARMED)) {
            // Increased damage during berserk
            event.setDamage(damage * 1.5);
            
            // Chance to ignore armor
            if (random.nextDouble() < 0.25) {
                event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
            }
        }
        
        // Iron Arm Style - Disarm chance
        double disarmChance = Math.min(0.05 + (level * 0.0005), 0.25);
        if (victim instanceof Player && random.nextDouble() < disarmChance) {
            Player victimPlayer = (Player) victim;
            disarmPlayer(victimPlayer);
        }
        
        // Arrow Deflect - Passive ability
        // This is handled in a separate method when player takes projectile damage
        
        // Damage bonus based on level
        double damageBonus = 1.0 + (level * 0.002); // +0.2% damage per level
        event.setDamage(damage * damageBonus);
    }
    
    private void handleSwordsCombat(Player player, LivingEntity victim, EntityDamageByEntityEvent event, PlayerProfile profile) {
        long experience = getSwordsExperience(victim);
        plugin.getPlayerManager().addExperience(player, SkillType.SWORDS, experience);
        
        int level = profile.getSkillLevel(SkillType.SWORDS);
        
        // Serrated Strikes ability
        if (profile.hasActiveAbility(SkillType.SWORDS)) {
            // Area damage effect
            performSerratedStrikes(player, victim, event.getDamage());
        }
        
        // Bleed chance
        double bleedChance = Math.min(level * 0.001, 0.15);
        if (random.nextDouble() < bleedChance) {
            applyBleedEffect(victim, level);
        }
        
        // Counter attack chance
        // This would be handled when the player takes damage
        
        // Damage bonus
        double damageBonus = 1.0 + (level * 0.0015);
        event.setDamage(event.getDamage() * damageBonus);
    }
    
    private void handleAxesCombat(Player player, LivingEntity victim, EntityDamageByEntityEvent event, PlayerProfile profile) {
        long experience = getAxesExperience(victim);
        plugin.getPlayerManager().addExperience(player, SkillType.AXES, experience);
        
        int level = profile.getSkillLevel(SkillType.AXES);
        
        // Skull Splitter ability
        if (profile.hasActiveAbility(SkillType.AXES)) {
            performSkullSplitter(player, victim, event.getDamage());
        }
        
        // Armor Impact - damage armor
        if (victim instanceof Player && random.nextDouble() < 0.25) {
            damageArmor((Player) victim);
        }
        
        // Greater Impact - bonus damage chance
        if (random.nextDouble() < Math.min(level * 0.001, 0.2)) {
            event.setDamage(event.getDamage() * 1.5);
        }
        
        // Base damage bonus
        double damageBonus = 1.0 + (level * 0.002);
        event.setDamage(event.getDamage() * damageBonus);
    }
    
    private void handleArcheryAttack(Player shooter, Entity victim, EntityDamageByEntityEvent event) {
        if (!(victim instanceof LivingEntity)) return;
        
        PlayerProfile profile = plugin.getPlayerManager().getProfile(shooter);
        if (profile == null) return;
        
        long experience = getArcheryExperience((LivingEntity) victim);
        plugin.getPlayerManager().addExperience(shooter, SkillType.ARCHERY, experience);
        
        int level = profile.getSkillLevel(SkillType.ARCHERY);
        
        // Power Shot ability
        if (profile.hasActiveAbility(SkillType.ARCHERY)) {
            event.setDamage(event.getDamage() * 2.0);
            // Add knockback effect
            if (victim instanceof LivingEntity) {
                ((LivingEntity) victim).setVelocity(victim.getLocation().getDirection().multiply(-2));
            }
        }
        
        // Daze chance
        double dazeChance = Math.min(level * 0.0008, 0.12);
        if (victim instanceof Player && random.nextDouble() < dazeChance) {
            applyDazeEffect((Player) victim);
        }
        
        // Damage bonus
        double damageBonus = 1.0 + (level * 0.001);
        event.setDamage(event.getDamage() * damageBonus);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTakeDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) return;
        
        // Arrow Deflect for Unarmed
        if (event.getDamager() instanceof Projectile && isUnarmedFighting(player)) {
            int level = profile.getSkillLevel(SkillType.UNARMED);
            double deflectChance = Math.min(level * 0.0005, 0.1);
            
            if (random.nextDouble() < deflectChance) {
                event.setCancelled(true);
                player.sendMessage("§6⚡ §7You deflected the arrow with your bare hands!");
            }
        }
        
        // Counter Attack for Swords
        if (event.getDamager() instanceof LivingEntity && isSwordFighting(player)) {
            int level = profile.getSkillLevel(SkillType.SWORDS);
            double counterChance = Math.min(level * 0.0003, 0.08);
            
            if (random.nextDouble() < counterChance) {
                LivingEntity attacker = (LivingEntity) event.getDamager();
                double counterDamage = event.getDamage() * 0.5;
                attacker.damage(counterDamage, player);
                player.sendMessage("§6⚡ §7Counter attack!");
            }
        }
    }
    
    // Helper methods
    private boolean isUnarmedAttack(Material weapon) {
        return weapon == Material.AIR || weapon == null;
    }
    
    private boolean isSwordWeapon(Material weapon) {
        return weapon.name().contains("SWORD");
    }
    
    private boolean isAxeWeapon(Material weapon) {
        return weapon.name().contains("_AXE") && !weapon.name().contains("PICKAXE");
    }
    
    private boolean isUnarmedFighting(Player player) {
        ItemStack weapon = player.getInventory().getItemInMainHand();
        return isUnarmedAttack(weapon.getType());
    }
    
    private boolean isSwordFighting(Player player) {
        ItemStack weapon = player.getInventory().getItemInMainHand();
        return isSwordWeapon(weapon.getType());
    }
    
    private long getUnarmedExperience(LivingEntity victim) {
        if (victim instanceof Player) return 80;
        
        switch (victim.getType()) {
            case ZOMBIE:
            case SKELETON:
            case SPIDER:
                return 40;
            case CREEPER:
            case ENDERMAN:
                return 60;
            case BLAZE:
            case WITHER_SKELETON:
                return 80;
            case ENDER_DRAGON:
            case WITHER:
                return 500;
            default:
                return 20;
        }
    }
    
    private long getSwordsExperience(LivingEntity victim) {
        return getUnarmedExperience(victim); // Same base values
    }
    
    private long getAxesExperience(LivingEntity victim) {
        return getUnarmedExperience(victim); // Same base values
    }
    
    private long getArcheryExperience(LivingEntity victim) {
        return getUnarmedExperience(victim); // Same base values
    }
    
    private void disarmPlayer(Player player) {
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon.getType() != Material.AIR) {
            player.getWorld().dropItemNaturally(player.getLocation(), weapon);
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            player.sendMessage("§c⚡ §7You have been disarmed!");
        }
    }
    
    private void performSerratedStrikes(Player player, LivingEntity victim, double damage) {
        // Area damage around the victim
        victim.getNearbyEntities(3, 3, 3).forEach(entity -> {
            if (entity instanceof LivingEntity && entity != player && entity != victim) {
                LivingEntity target = (LivingEntity) entity;
                target.damage(damage * 0.5, player);
            }
        });
    }
    
    private void performSkullSplitter(Player player, LivingEntity victim, double damage) {
        // Similar to serrated strikes but for axes
        victim.getNearbyEntities(2, 2, 2).forEach(entity -> {
            if (entity instanceof LivingEntity && entity != player && entity != victim) {
                LivingEntity target = (LivingEntity) entity;
                target.damage(damage * 0.6, player);
            }
        });
    }
    
    private void applyBleedEffect(LivingEntity victim, int level) {
        // Apply bleeding damage over time
        int duration = Math.min(3 + (level / 100), 10);
        // This would need a scheduler to apply damage over time
        // For now, just apply immediate bonus damage
        victim.damage(2.0 + (level * 0.01));
    }
    
    private void applyDazeEffect(Player victim) {
        // Confuse the player briefly
        victim.sendMessage("§c⚡ §7You feel dazed from the arrow!");
        // Could add nausea effect here
    }
    
    private void damageArmor(Player player) {
        // Damage a random piece of armor
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack piece : armor) {
            if (piece != null && piece.getType() != Material.AIR) {
                // Damage the armor piece
                piece.setDurability((short) (piece.getDurability() + 5));
                break;
            }
        }
    }
}