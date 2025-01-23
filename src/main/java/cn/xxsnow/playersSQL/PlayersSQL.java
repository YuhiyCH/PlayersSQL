package cn.xxsnow.playersSQL;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayersSQL extends JavaPlugin {
    private DatabaseManager databaseManager;
    private static PlayersSQL instance;
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.getCommand("playerssql").setExecutor(new Commands());
        this.getServer().getPluginManager().registerEvents(new Listeners(), this);
        try {
            String address = this.getConfig().getString("database.address");
            String port = this.getConfig().getString("database.port");
            String database = this.getConfig().getString("database.database");
            String jdbcUrl = "jdbc:mysql://"+address+":"+port+"/"+database;
            String username = this.getConfig().getString("database.username");
            String password = this.getConfig().getString("database.password");
            databaseManager = new DatabaseManager();
            databaseManager.connectMySQL(jdbcUrl, username, password);
        } catch (Exception e){
            getLogger().warning("连接MySQL数据库失败!已关闭插件运行");
            getLogger().warning("Failed to connect to MySQL database! The plugin has been closed and running");
            getPluginLoader().disablePlugin(this);
            e.printStackTrace();
        }
        try {
            databaseManager.createDataTable();
        } catch (Exception e){
            getLogger().warning("创建MySQL数据库表失败!已关闭插件运行");
            getLogger().warning("Failed to create MySQL database table! The plugin has been closed and running");
            getPluginLoader().disablePlugin(this);
            e.printStackTrace();
        }
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API 没有找到!已关闭插件运行");
            getLogger().warning("NBT-API not found! The plugin has been closed and running");
            getPluginLoader().disablePlugin(this);
            return;
        }
        Task.runSaveTask();
        getLogger().info("PlayersSQL 已启动!");
        getLogger().info("PlayersSQL has enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager.isRunning()){
            databaseManager.close();
        }
        getLogger().info("PlayersSQL 已关闭!");
        getLogger().info("PlayersSQL has disabled!");
    }

    public static PlayersSQL getInstance() {
        return instance;
    }

}
