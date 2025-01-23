package cn.xxsnow.playersSQL;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.hasPermission("PlayersSQL.admin")) {
        if (args.length == 0) {
            sender.sendMessage("/playerssql seeinnew <player> - 查看现在玩家的背包数据(View the current player's backpack data)");
            sender.sendMessage("/playerssql seeinmysql <player> - 查看MySQL里玩家的背包数据(View player backpack data in MySQL)");
            sender.sendMessage("/playerssql save - 保存玩家数据(To save player data)");
            sender.sendMessage("/playerssql reload - 重载插件配置(To reload plugin config)");
            return false;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "reload":
                reloadConfigAndReconnect(sender);
                break;
            case "save":
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    DatabaseManager.savePlayerData(player);
                }
                break;
            case "seeinnew", "seeinmysql":
                if (!args[1].isEmpty()) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (args[0].equalsIgnoreCase("seeinnew")){
                        seePlayerInvInNew(sender, player);
                    } else if (args[0].equalsIgnoreCase("seeinmysql")) {
                        seePlayerInvInMySQL(sender, player);
                    }
                } else {
                    sender.sendMessage("/playerssql seeinnew <player> - 查看现在玩家的背包数据(View the current player's backpack data)");
                    sender.sendMessage("/playerssql seeinmysql <player> - 查看MySQL里玩家的背包数据(View player backpack data in MySQL)");
                    sender.sendMessage("/playerssql save - 保存玩家数据(To save player data)");
                    sender.sendMessage("/playerssql reload - 重载插件配置(To reload plugin config)");
                }
                break;
            default:
                sender.sendMessage("/playerssql seeinnew <player> - 查看现在玩家的背包数据(View the current player's backpack data)");
                sender.sendMessage("/playerssql seeinmysql <player> - 查看MySQL里玩家的背包数据(View player backpack data in MySQL)");
                sender.sendMessage("/playerssql save - 保存玩家数据(To save player data)");
                sender.sendMessage("/playerssql reload - 重载插件配置(To reload plugin config)");
                break;
        }
        } else {
            sender.sendMessage(PlayersSQL.getInstance().getConfig().getString("messages.noPermisson"));
        }
        return true;
    }

    private void seePlayerInvInNew(CommandSender sender, Player player){
        if (sender instanceof Player){
            String title = PlayersSQL.getInstance().getConfig().getString("inv.PlayerInvInNewTitle").replaceAll("%player%",player.getName());
            Inventory inventory = Bukkit.createInventory(null,45,title);
            ItemStack[] inv_items = player.getInventory().getContents();

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

            inventory.setItem(44,book);
            for (int i = 0; i < inv_items.length; i++) {
                inventory.setItem(i, inv_items[i]);
            }
            ((Player) sender).openInventory(inventory);
        } else {
            sender.sendMessage(PlayersSQL.getInstance().getConfig().getString("messages.noPlayer"));
        }
    }
    private void seePlayerInvInMySQL(CommandSender sender, Player player){
        if (sender instanceof Player){
            String inv_nbt_json = DatabaseManager.getPlayerInvData(player);
            String title = PlayersSQL.getInstance().getConfig().getString("inv.PlayerInvInMySQLTitle").replaceAll("%player%",player.getName());
            Inventory inventory = Bukkit.createInventory(null,45,title);
            ReadWriteNBT inv_nbt = NBT.parseNBT(inv_nbt_json);
            ItemStack[] inv_items = NBT.itemStackArrayFromNBT(inv_nbt);

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

            inventory.setItem(44,book);
            if (inv_items != null) {
                for (int i = 0; i < inv_items.length; i++) {
                    inventory.setItem(i, inv_items[i]);
                }
                ((Player) sender).openInventory(inventory);
            } else {
                sender.sendMessage(PlayersSQL.getInstance().getConfig().getString("messages.noPlayerInvData").replaceAll("%player%",player.getName()));
            }
        } else {
            sender.sendMessage(PlayersSQL.getInstance().getConfig().getString("messages.noPlayer"));
        }
    }
    private void reloadConfigAndReconnect(CommandSender sender) {
        PlayersSQL.getInstance().reloadConfig();
        DatabaseManager.createDataTable();
        sender.sendMessage(PlayersSQL.getInstance().getConfig().getString("messages.reloaded"));
    }
}