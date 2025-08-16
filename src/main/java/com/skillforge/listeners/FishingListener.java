package com.skillforge.listeners;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import com.skillforge.models.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class FishingListener implements Listener {
    private final SkillForge plugin;
    private final Random random = new Random();
    
    public FishingListener(SkillForge plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item)) return;
        
        Player player = event.getPlayer();
        PlayerProfile profile = plugin.getPlayerManager().getProfile(player);
        if (profile == null) return;
        
        // Award fishing experience
        long experience = 50;
        plugin.getPlayerManager().addExperience(player, SkillType.FISHING, experience);
        
        int level = profile.getSkillLevel(SkillType.FISHING);
        
        // Treasure Hunter - chance for rare items
        double treasureChance = Math.min(level * 0.001, 0.15);
        if (random.nextDouble() < treasureChance) {
            dropFishingTreasure(player);
        }
        
        // Magic Hunter - chance for enchanted books
        double magicChance = Math.min(level * 0.0005, 0.08);
        if (random.nextDouble() < magicChance) {
            dropEnchantedBook(player);
        }
    }
    
    private void dropFishingTreasure(Player player) {
        ItemStack[] treasures = {
            new ItemStack(Material.DIAMOND, 1),
            new ItemStack(Material.EMERALD, 1),
            new ItemStack(Material.GOLD_INGOT, 2),
            new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1),
            new ItemStack(Material.NAME_TAG, 1),
            new ItemStack(Material.SADDLE, 1)
        };
        
        ItemStack treasure = treasures[random.nextInt(treasures.length)];
        player.getWorld().dropItemNaturally(player.getLocation(), treasure);
        player.sendMessage("§6⚡ §7You caught a treasure: §e" + treasure.getType().name() + "§7!");
    }
    
    private void dropEnchantedBook(Player player) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        player.getWorld().dropItemNaturally(player.getLocation(), book);
        player.sendMessage("§6⚡ §7You caught an enchanted book!");
    }
}