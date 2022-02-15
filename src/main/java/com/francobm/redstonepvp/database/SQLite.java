package com.francobm.redstonepvp.database;

import com.francobm.redstonepvp.cache.PlayerCache;
import com.francobm.redstonepvp.utils.XMaterial;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLite extends SQL {

    @Override
    public void connect() {
        try {
            plugin.getLogger().info("Connecting the database with SQLite...");
            File FileSQL = new File(plugin.getDataFolder(), "redstonepvp.db");
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + FileSQL);
            plugin.getLogger().info("SQLite connection established");
        } catch (Exception e) {
            e.printStackTrace();
            this.plugin.getPluginLoader().disablePlugin(this.plugin);
        }
    }

    @Override
    public void disconnect() {
        if(isConnected()){
            try {
                connection.close();
                plugin.getLogger().info("SQLite connection closed");
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @Override
    public void createTable() {
        if(isConnected()) {
            try {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS redstone_players (id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR(255), Player VARCHAR(255), Kits VARCHAR(255))").executeUpdate();
                plugin.getLogger().info("SQLite table created successfully!");
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @Override
    public void loadPlayer(Player player){
        loadPlayerInfo(player);
    }

    @Override
    public void savePlayer(PlayerCache playerCache){
        savePlayerInfo(playerCache);
    }

    private void savePlayerInfo(PlayerCache player){
        try{
            if(!checkPlayerInfo(player)){
                String query = "INSERT INTO redstone_players (id, UUID, Player, Kits) VALUES(NULL, ?, ?, ?);";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, player.getUuid().toString());
                statement.setString(2, player.getOfflinePlayer().getName());
                statement.setString(3, player.saveItem());
                statement.executeUpdate();
                return;
            }
            String query = "UPDATE redstone_players SET Player = ?, Kits = ? WHERE UUID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, player.getOfflinePlayer().getName());
            statement.setString(2, player.saveItem());
            statement.setString(3, player.getUuid().toString());
            statement.executeUpdate();
        }catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        PlayerCache.removePlayer(player);
    }

    private void loadPlayerInfo(Player player){
        String queryBuilder = "SELECT * FROM redstone_players WHERE UUID = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            PlayerCache playerCache = PlayerCache.getPlayerCache(player);
            if(resultSet == null){
                return;
            }
            if(resultSet.next()){
                String kits = resultSet.getString("Kits");
                playerCache.loadItem(kits);
            }
            playerCache.addItems();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    private boolean checkPlayerInfo(PlayerCache playerCache){
        String queryBuilder = "SELECT * FROM redstone_players WHERE UUID = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, playerCache.getUuid().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet != null && resultSet.next()){
                return true;
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }

}
