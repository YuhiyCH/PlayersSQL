package cn.xxsnow.playersSQL;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Task {
    public static void runSaveTask(){
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : PlayersSQL.getInstance().getServer().getOnlinePlayers()) {
                    DatabaseManager.savePlayerData(player);
                }
            }
        }.runTaskTimer(PlayersSQL.getInstance(), 0, 20L *PlayersSQL.getInstance().getConfig().getInt("task.time"));
    }
}
