package cn.xxsnow.playersSQL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static HikariDataSource dataSource;
    public void connectMySQL(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
    }

    public boolean isRunning(){
        return dataSource.isRunning();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
            PlayersSQL.getInstance().getLogger().info("PlayersSQL 已关闭对MySQL数据库连接.");
            PlayersSQL.getInstance().getLogger().info("PlayersSQL closed connection to MySQL database.");
        }
    }

    public static boolean ishasDataTable(){
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?"
             )) {
            stmt.setString(1, PlayersSQL.getInstance().getConfig().getString("database.database"));
            stmt.setString(2, "PlayersSQL_data");
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println(PlayersSQL.getInstance().getConfig().getString("messages.failInInspectTable"));
        }
        return false;
    }
    public static void createDataTable(){
        String createDataTableSQL = "CREATE TABLE IF NOT EXISTS PlayersSQL_data (" +
                "uuid CHAR(36) PRIMARY KEY, " +
                "playername VARCHAR(255), " +
                "health DOUBLE NOT NULL, " +
                "food_level INT NOT NULL, " +
                "experience BIGINT NOT NULL, " +
                "level INT NOT NULL," +
                "inv TEXT NOT NULL," +
                "enderchestinv TEXT NOT NULL" +
                ")";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            if (!ishasDataTable()) {
                statement.execute(createDataTableSQL);
                PlayersSQL.getInstance().getLogger().info(PlayersSQL.getInstance().getConfig().getString("messages.successCreateTable"));
            }
        } catch (SQLException e) {
            PlayersSQL.getInstance().getLogger().warning(PlayersSQL.getInstance().getConfig().getString("messages.failCreateTable"));
            e.printStackTrace();

        }
    }
    public static String getPlayerInvData(Player player){
        UUID uuid = player.getUniqueId();
        String playername = player.getName();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT inv FROM PlayersSQL_data WHERE uuid=?"
             )) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("inv");
            } else {
                return null;
            }
        } catch (SQLException | IllegalArgumentException e) {
            PlayersSQL.getInstance().getLogger().warning(PlayersSQL.getInstance().getConfig().getString("messages.failInCheckInv").replaceAll("%player%", playername));
        }
        return null;
    }
    public static void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        String playername = player.getName();
        PlayerInventory inventory = player.getInventory();
        Inventory enderchest = player.getEnderChest();
        double health = player.getHealth();
        int foodLevel = player.getFoodLevel();
        int experience = player.getTotalExperience();
        int level = player.getLevel();
        ReadWriteNBT inv_nbt = NBT.itemStackArrayToNBT(inventory.getContents());
        String inv_nbt_json = inv_nbt.toString();

        ReadWriteNBT enderchestinv_nbt = NBT.itemStackArrayToNBT(enderchest.getContents());
        String enderchestinv_nbt_json = enderchestinv_nbt.toString();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO PlayersSQL_data (uuid, playername, health, food_level, experience, level, inv, enderchestinv) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE " +
                             "health=VALUES(health), food_level=VALUES(food_level), " +
                             "experience=VALUES(experience), level=VALUES(level), " +
                             "inv=VALUES(inv), enderchestinv=VALUES(enderchestinv)"
             )) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, playername);
            stmt.setDouble(3, health);
            stmt.setInt(4, foodLevel);
            stmt.setInt(5, experience);
            stmt.setInt(6, level);
            stmt.setString(7,inv_nbt_json);
            stmt.setString(8,enderchestinv_nbt_json);
            stmt.executeUpdate();
            if (PlayersSQL.getInstance().getConfig().getBoolean("debug.enable")) {
                PlayersSQL.getInstance().getLogger().info(PlayersSQL.getInstance().getConfig().getString("messages.debugInSave").replaceAll("%player%", playername));
            }
        } catch (SQLException | IllegalArgumentException e) {
            PlayersSQL.getInstance().getLogger().warning(PlayersSQL.getInstance().getConfig().getString("messages.failInSave").replaceAll("%player%", playername));
            e.printStackTrace();
        }
    }

    public static void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        String playername = player.getName();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT health, food_level, experience, level, inv, enderchestinv FROM PlayersSQL_data WHERE uuid=?"
             )) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                double health = resultSet.getDouble("health");
                int foodLevel = resultSet.getInt("food_level");
                int experience = resultSet.getInt("experience");
                int level = resultSet.getInt("level");
                String inv_nbt_json = resultSet.getString("inv");
                ReadWriteNBT inv_nbt = NBT.parseNBT(inv_nbt_json);
                ItemStack[] inv_items = NBT.itemStackArrayFromNBT(inv_nbt);

                String enderchestinv_nbt_json = resultSet.getString("enderchestinv");
                ReadWriteNBT enderchestinv_nbt = NBT.parseNBT(enderchestinv_nbt_json);
                ItemStack[] enderchestinv_items = NBT.itemStackArrayFromNBT(enderchestinv_nbt);

                player.setHealth(health);
                player.setFoodLevel(foodLevel);
                player.giveExp(experience);
                player.setLevel(level);

                if (inv_items != null) {
                    for (int i =0; i< inv_items.length;i++){
                        player.getInventory().setItem(i, inv_items[i]);
                    }
                }

                if (enderchestinv_items != null) {
                    for (int i =0; i< enderchestinv_items.length;i++){
                        player.getEnderChest().setItem(i, enderchestinv_items[i]);
                    }
                }
            }
            if (PlayersSQL.getInstance().getConfig().getBoolean("debug.enable")) {
                PlayersSQL.getInstance().getLogger().info(PlayersSQL.getInstance().getConfig().getString("messages.debugInLoad").replaceAll("%player%", playername));
            }
        } catch (SQLException | IllegalArgumentException e) {
            PlayersSQL.getInstance().getLogger().warning(PlayersSQL.getInstance().getConfig().getString("messages.failInLoad").replaceAll("%player%", playername));
            e.printStackTrace();
        }
    }
}