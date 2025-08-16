package com.skillforge.listeners;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import com.skillforge.models.SkillType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BlockListener implements Listener {
    private final SkillForge plugin;
    private final Random random = new Random();
    
    public BlockListener(SkillForge plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        Block block = event.getBlock();
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        
        if (profile == null) return;
        
        handleMining(player, block, profile);
        handleWoodcutting(player, block, profile);
        handleExcavation(player, block, profile);
        handleHerbalism(player, block, profile);
    }
    
    private void handleMining(Player player, Block block, PlayerProfile profile) {
        if (!isMiningBlock(block.getType())) return;
        
        long experience = getMiningExperience(block.getType());
        plugin.getPlayerManager().addExperience(player, SkillType.MINING, experience);
        
        // Super Breaker ability effects
        if (profile.hasActiveAbility(SkillType.MINING)) {
            // Triple drops chance
            if (random.nextDouble() < 0.33) {
                block.getDrops().forEach(item -> 
                    player.getWorld().dropItemNaturally(block.getLocation(), item));
            }
        }
        
        // Double drops based on level
        double doubleDropChance = Math.min(0.1 + (profile.getSkillLevel(SkillType.MINING) * 0.001), 0.75);
        if (random.nextDouble() < doubleDropChance) {
            block.getDrops().forEach(item -> 
                player.getWorld().dropItemNaturally(block.getLocation(), item));
        }
    }
    
    private void handleWoodcutting(Player player, Block block, PlayerProfile profile) {
        if (!isWoodcuttingBlock(block.getType())) return;
        
        long experience = getWoodcuttingExperience(block.getType());
        plugin.getPlayerManager().addExperience(player, SkillType.WOODCUTTING, experience);
        
        // Tree Feller ability
        if (profile.hasActiveAbility(SkillType.WOODCUTTING)) {
            // Add tree felling logic here
            activateTreeFeller(player, block);
        }
    }
    
    private void handleExcavation(Player player, Block block, PlayerProfile profile) {
        if (!isExcavationBlock(block.getType())) return;
        
        long experience = getExcavationExperience(block.getType());
        plugin.getPlayerManager().addExperience(player, SkillType.EXCAVATION, experience);
        
        // Treasure hunting
        double treasureChance = profile.getSkillLevel(SkillType.EXCAVATION) * 0.001;
        if (random.nextDouble() < treasureChance) {
            dropTreasure(player, block.getLocation());
        }
    }
    
    private void handleHerbalism(Player player, Block block, PlayerProfile profile) {
        if (!isHerbalismBlock(block.getType())) return;
        
        long experience = getHerbalismExperience(block.getType());
        plugin.getPlayerManager().addExperience(player, SkillType.HERBALISM, experience);
        
        // Green Thumb - replant crops
        if (profile.getSkillLevel(SkillType.HERBALISM) >= 25 && isCrop(block.getType())) {
            if (random.nextDouble() < 0.15) {
                // Replant logic
                block.getWorld().getBlockAt(block.getLocation()).setType(getSeedType(block.getType()));
            }
        }
    }
    
    private boolean isMiningBlock(Material material) {
        return material == Material.STONE || material == Material.COAL_ORE || 
               material == Material.IRON_ORE || material == Material.GOLD_ORE || 
               material == Material.DIAMOND_ORE || material == Material.EMERALD_ORE ||
               material == Material.DEEPSLATE_COAL_ORE || material == Material.DEEPSLATE_IRON_ORE ||
               material == Material.DEEPSLATE_GOLD_ORE || material == Material.DEEPSLATE_DIAMOND_ORE;
    }
    
    private boolean isWoodcuttingBlock(Material material) {
        return material.name().contains("LOG") || material.name().contains("WOOD");
    }
    
    private boolean isExcavationBlock(Material material) {
        return material == Material.DIRT || material == Material.GRASS_BLOCK || 
               material == Material.SAND || material == Material.GRAVEL || 
               material == Material.MYCELIUM || material == Material.SOUL_SAND;
    }
    
    private boolean isHerbalismBlock(Material material) {
        return material == Material.WHEAT || material == Material.CARROTS || 
               material == Material.POTATOES || material == Material.BEETROOTS ||
               material == Material.SUGAR_CANE || material == Material.CACTUS;
    }
    
    private long getMiningExperience(Material material) {
        switch (material) {
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                return 30;
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
                return 50;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
                return 75;
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
                return 150;
            case EMERALD_ORE:
                return 200;
            default:
                return 10;
        }
    }
    
    private long getWoodcuttingExperience(Material material) {
        if (material.name().contains("LOG")) return 35;
        return 25;
    }
    
    private long getExcavationExperience(Material material) {
        switch (material) {
            case GRAVEL:
                return 20;
            case SAND:
                return 15;
            case SOUL_SAND:
                return 25;
            default:
                return 10;
        }
    }
    
    private long getHerbalismExperience(Material material) {
        switch (material) {
            case WHEAT:
            case CARROTS:
            case POTATOES:
            case BEETROOTS:
                return 30;
            case SUGAR_CANE:
                return 20;
            case CACTUS:
                return 25;
            default:
                return 15;
        }
    }
    
    private void activateTreeFeller(Player player, Block block) {
        // Tree felling logic - break connected logs
        // This is a simplified version
    }
    
    private void dropTreasure(Player player, org.bukkit.Location location) {
        ItemStack[] treasures = {
            new ItemStack(Material.DIAMOND, 1),
            new ItemStack(Material.EMERALD, 1),
            new ItemStack(Material.GOLD_INGOT, 2),
            new ItemStack(Material.IRON_INGOT, 3)
        };
        
        ItemStack treasure = treasures[random.nextInt(treasures.length)];
        player.getWorld().dropItemNaturally(location, treasure);
    }
    
    private boolean isCrop(Material material) {
        return material == Material.WHEAT || material == Material.CARROTS || 
               material == Material.POTATOES || material == Material.BEETROOTS;
    }
    
    private Material getSeedType(Material crop) {
        switch (crop) {
            case WHEAT:
                return Material.WHEAT_SEEDS;
            case CARROTS:
                return Material.CARROTS;
            case POTATOES:
                return Material.POTATOES;
            case BEETROOTS:
                return Material.BEETROOT_SEEDS;
            default:
                return Material.WHEAT_SEEDS;
        }
    }
}