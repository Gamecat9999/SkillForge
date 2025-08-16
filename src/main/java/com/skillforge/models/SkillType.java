package com.skillforge.models;

public enum SkillType {
    MINING("Mining", "⛏", "mine"),
    WOODCUTTING("Woodcutting", "🪓", "cut"),
    EXCAVATION("Excavation", "🏗", "dig"),
    HERBALISM("Herbalism", "🌿", "harvest"),
    ARCHERY("Archery", "🏹", "shoot"),
    SWORDS("Swords", "⚔", "slash"),
    AXES("Axes", "🪓", "cleave"),
    UNARMED("Unarmed", "👊", "punch"),
    TAMING("Taming", "🐺", "tame"),
    FISHING("Fishing", "🎣", "fish"),
    ACROBATICS("Acrobatics", "🤸", "dodge"),
    REPAIR("Repair", "🔧", "fix"),
    ALCHEMY("Alchemy", "⚗", "brew");
    
    private final String displayName;
    private final String icon;
    private final String verb;
    
    SkillType(String displayName, String icon, String verb) {
        this.displayName = displayName;
        this.icon = icon;
        this.verb = verb;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public String getVerb() {
        return verb;
    }
    
    public static SkillType fromString(String name) {
        for (SkillType skill : values()) {
            if (skill.name().equalsIgnoreCase(name) || skill.displayName.equalsIgnoreCase(name)) {
                return skill;
            }
        }
        return null;
    }
}