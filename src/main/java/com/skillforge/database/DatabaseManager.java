package com.skillforge.database;

import com.skillforge.SkillForge;
import com.skillforge.models.PlayerProfile;
import com.skillforge.models.SkillType;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private final SkillForge plugin;
    private Connection connection;
    
    public DatabaseManager(SkillForge plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() {
        try {
            setupDatabase();
            createTables();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
        }
    }
    
    private void setupDatabase() throws SQLException {
        String dbType = plugin.getConfig().getString("database.type", "sqlite");
        
        if (dbType.equalsIgnoreCase("sqlite")) {
            File dbFile = new File(plugin.getDataFolder(), "skillforge.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);
        } else if (dbType.equalsIgnoreCase("mysql")) {
            String host = plugin.getConfig().getString("database.host", "localhost");
            int port = plugin.getConfig().getInt("database.port", 3306);
            String database = plugin.getConfig().getString("database.database", "skillforge");
            String username = plugin.getConfig().getString("database.username", "root");
            String password = plugin.getConfig().getString("database.password", "");
            
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, username, password);
        }
    }
    
    private void createTables() throws SQLException {
        String createPlayersTable = """
            CREATE TABLE IF NOT EXISTS players (
                uuid VARCHAR(36) PRIMARY KEY,
                name VARCHAR(16) NOT NULL,
                power_level BIGINT DEFAULT 0,
                total_experience BIGINT DEFAULT 0,
                last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        String createSkillsTable = """
            CREATE TABLE IF NOT EXISTS player_skills (
                uuid VARCHAR(36),
                skill VARCHAR(20),
                level INT DEFAULT 1,
                experience BIGINT DEFAULT 0,
                PRIMARY KEY (uuid, skill),
                FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayersTable);
            stmt.execute(createSkillsTable);
        }
    }
    
    public PlayerProfile loadPlayerProfile(UUID playerId, String playerName) {
        PlayerProfile profile = new PlayerProfile(playerId, playerName);
        
        try {
            // Load basic player data
            String selectPlayer = "SELECT * FROM players WHERE uuid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(selectPlayer)) {
                stmt.setString(1, playerId.toString());
                ResultSet rs = stmt.executeQuery();
                
                if (!rs.next()) {
                    // New player, insert default data
                    insertNewPlayer(playerId, playerName);
                }
            }
            
            // Load skill data
            String selectSkills = "SELECT * FROM player_skills WHERE uuid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(selectSkills)) {
                stmt.setString(1, playerId.toString());
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    String skillName = rs.getString("skill");
                    int level = rs.getInt("level");
                    long experience = rs.getLong("experience");
                    
                    SkillType skill = SkillType.fromString(skillName);
                    if (skill != null) {
                        profile.setSkillLevel(skill, level);
                        profile.addSkillExperience(skill, experience);
                    }
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load player profile: " + e.getMessage());
        }
        
        return profile;
    }
    
    public void savePlayerProfile(PlayerProfile profile) {
        try {
            // Update player data
            String updatePlayer = """
                UPDATE players SET 
                name = ?, power_level = ?, total_experience = ?, last_seen = CURRENT_TIMESTAMP 
                WHERE uuid = ?
            """;
            try (PreparedStatement stmt = connection.prepareStatement(updatePlayer)) {
                stmt.setString(1, profile.getPlayerName());
                stmt.setLong(2, profile.getPowerLevel());
                stmt.setLong(3, profile.getTotalExperience());
                stmt.setString(4, profile.getPlayerId().toString());
                stmt.executeUpdate();
            }
            
            // Update skill data
            for (SkillType skill : SkillType.values()) {
                String updateSkill = """
                    INSERT OR REPLACE INTO player_skills (uuid, skill, level, experience) 
                    VALUES (?, ?, ?, ?)
                """;
                try (PreparedStatement stmt = connection.prepareStatement(updateSkill)) {
                    stmt.setString(1, profile.getPlayerId().toString());
                    stmt.setString(2, skill.name());
                    stmt.setInt(3, profile.getSkillLevel(skill));
                    stmt.setLong(4, profile.getSkillExperience(skill));
                    stmt.executeUpdate();
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save player profile: " + e.getMessage());
        }
    }
    
    private void insertNewPlayer(UUID playerId, String playerName) throws SQLException {
        String insertPlayer = "INSERT INTO players (uuid, name) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertPlayer)) {
            stmt.setString(1, playerId.toString());
            stmt.setString(2, playerName);
            stmt.executeUpdate();
        }
        
        // Insert default skill levels
        for (SkillType skill : SkillType.values()) {
            String insertSkill = "INSERT INTO player_skills (uuid, skill, level, experience) VALUES (?, ?, 1, 0)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSkill)) {
                stmt.setString(1, playerId.toString());
                stmt.setString(2, skill.name());
                stmt.executeUpdate();
            }
        }
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close database connection: " + e.getMessage());
        }
    }
}