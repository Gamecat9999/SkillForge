package com.skillforge.utils;

import com.skillforge.models.SkillType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static void sendLevelUpMessage(Player player, SkillType skill, int level) {
        String message = ChatColor.GREEN + "✦ " + ChatColor.GRAY + "You reached " +
                        ChatColor.YELLOW + skill.getDisplayName() + ChatColor.GRAY + " level " +
                        ChatColor.YELLOW + level + ChatColor.GRAY + "! " + ChatColor.GREEN + "✦";
        player.sendMessage(message);

        // Send title
        player.sendTitle(
            ChatColor.GOLD + "LEVEL UP!",
            ChatColor.YELLOW + skill.getDisplayName() + " " + level,
            10, 40, 10
        );
    }

    public static void sendExperienceMessage(Player player, SkillType skill, long experience) {
        String message = ChatColor.AQUA + "+" + experience + " " + skill.getDisplayName() + " XP";
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public static void sendAbilityActivatedMessage(Player player, String abilityName, int duration) {
        String message = ChatColor.GOLD + "⚡ " + ChatColor.YELLOW + formatAbilityName(abilityName) +
                        ChatColor.GOLD + " activated for " + duration + " seconds! " + ChatColor.GOLD + "⚡";
        player.sendMessage(message);

        player.sendTitle(
            ChatColor.GOLD + "ABILITY ACTIVATED!",
            ChatColor.YELLOW + formatAbilityName(abilityName),
            5, 30, 5
        );
    }

    public static void sendAbilityUnlockedMessage(Player player, SkillType skill, String abilityName) {
        String message = ChatColor.GREEN + "✦ " + ChatColor.GRAY + "You unlocked the " +
                        ChatColor.YELLOW + formatAbilityName(abilityName) + ChatColor.GRAY +
                        " ability for " + ChatColor.YELLOW + skill.getDisplayName() + ChatColor.GRAY + "! " +
                        ChatColor.GREEN + "✦";
        player.sendMessage(message);
    }

    public static void sendAbilityReadyMessage(Player player, SkillType skill) {
        String message = ChatColor.GREEN + "⚡ " + ChatColor.GRAY + "Your " +
                        ChatColor.YELLOW + skill.getDisplayName() + ChatColor.GRAY +
                        " ability is ready! " + ChatColor.GREEN + "⚡";
        player.sendMessage(message);
    }

    private static String formatAbilityName(String abilityName) {
        String[] words = abilityName.replace("-", " ").replace("_", " ").split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1).toLowerCase())
                         .append(" ");
            }
        }
        return formatted.toString().trim();
    }

    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}