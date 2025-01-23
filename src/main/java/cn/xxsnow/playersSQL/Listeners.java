package cn.xxsnow.playersSQL;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Listeners implements Listener {
    private boolean issync;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        double oldhealth = player.getHealth();
        int oldfoollevel = player.getFoodLevel();
        int oldlevel = player.getLevel();
        int oldexp = player.getTotalExperience();
        ItemStack[] oldinv = player.getInventory().getContents();
        ItemStack[] oldenderchetinv = player.getEnderChest().getContents();

        player.getInventory().clear();
        player.getEnderChest().clear();
        if (PlayersSQL.getInstance().getConfig().getBoolean("title.enable")) {
            player.sendTitle(PlayersSQL.getInstance().getConfig().getString("messages.inSyncTitle"),
                    PlayersSQL.getInstance().getConfig().getString("messages.inSyncSubTitle"), 10, 30, 20);
        }
        issync = true;
        try {
        PlayersSQL.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(PlayersSQL.getInstance(), new Runnable() {
            @Override
            public void run() {
                DatabaseManager.loadPlayerData(player);
                if (PlayersSQL.getInstance().getConfig().getBoolean("title.enable")) {
                player.sendTitle(PlayersSQL.getInstance().getConfig().getString("messages.syncTitle"),
                        PlayersSQL.getInstance().getConfig().getString("messages.syncSubTitle"), 10, 30, 20);
                issync = false;
                }
            }
        }, 20L*PlayersSQL.getInstance().getConfig().getInt("task.syncdelaytime"));
        } catch (Exception e){
            if (PlayersSQL.getInstance().getConfig().getBoolean("title.enable")) {
                player.sendTitle(PlayersSQL.getInstance().getConfig().getString("messages.failSyncTitle"),
                        PlayersSQL.getInstance().getConfig().getString("messages.failSyncSubTitle"), 10, 30, 20);
            }
            player.sendMessage(PlayersSQL.getInstance().getConfig().getString("messages.failSync"));
        } finally {
            player.setHealth(oldhealth);
            player.setFoodLevel(oldfoollevel);
            player.setLevel(oldlevel);
            player.setTotalExperience(oldexp);
            for (int i =0; i < oldinv.length; i++){
                player.getInventory().setItem(i,oldinv[i]);
            }
            for (int i =0; i < oldenderchetinv.length; i++){
                player.getEnderChest().setItem(i,oldinv[i]);
            }
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DatabaseManager.savePlayerData(player);
    }
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        DatabaseManager.savePlayerData(player);
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (issync) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onClickInv(InventoryClickEvent event){
        Inventory inventory = event.getClickedInventory();

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName("§c§l帮助(Help)");
        ArrayList<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("§6§l槽位37§f为玩家装备上的靴子");
        lores.add("§f(§6§lSlot 37 §fis the boot on the player's equipment§f)");
        lores.add("§6§l槽位38§f为玩家装备上的裤子");
        lores.add("§f(§6§lSlot 38 §fis the pants on the player's equipment§f)");
        lores.add("§6§l槽位39§f为玩家装备上的盔甲");
        lores.add("§f(§6§lSlot 39 §fis the armor on the player's equipment§f)");
        lores.add("§6§l槽位40§f为玩家装备上的头盔");
        lores.add("§f(§6§lSlot 40 §fis the helmet on the player's equipment§f)");
        lores.add("§6§l槽位41§f为玩家副手上的物品");
        lores.add("§f(§6§lSlot 41 §fis the item on the player's assistant§f)");
        bookMeta.setLore(lores);
        book.setItemMeta(bookMeta);

        if (inventory != null && inventory.contains(book)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onDragInv(InventoryDragEvent event){
        Inventory inventory = event.getInventory();

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName("§c§l帮助(Help)");
        ArrayList<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("§6§l槽位37§f为玩家装备上的靴子");
        lores.add("§f(§6§lSlot 37 §fis the boot on the player's equipment§f)");
        lores.add("§6§l槽位38§f为玩家装备上的裤子");
        lores.add("§f(§6§lSlot 38 §fis the pants on the player's equipment§f)");
        lores.add("§6§l槽位39§f为玩家装备上的盔甲");
        lores.add("§f(§6§lSlot 39 §fis the armor on the player's equipment§f)");
        lores.add("§6§l槽位40§f为玩家装备上的头盔");
        lores.add("§f(§6§lSlot 40 §fis the helmet on the player's equipment§f)");
        lores.add("§6§l槽位41§f为玩家副手上的物品");
        lores.add("§f(§6§lSlot 41 §fis the item on the player's assistant§f)");
        bookMeta.setLore(lores);
        book.setItemMeta(bookMeta);

        if (inventory.contains(book)) {
            event.setCancelled(true);
        }
    }
}