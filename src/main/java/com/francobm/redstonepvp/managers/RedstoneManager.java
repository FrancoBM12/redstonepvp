package com.francobm.redstonepvp.managers;

import com.francobm.redstonepvp.RedstonePvP;
import com.francobm.redstonepvp.cache.*;
import com.francobm.redstonepvp.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RedstoneManager {
    private final RedstonePvP plugin = RedstonePvP.getInstance();
    private BukkitTask combat;

    public void runTask(){
        combat = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    PlayerCache playerCache = PlayerCache.getPlayerCache(player);
                    Combat.checkCombat(playerCache);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void stopTask(){
        if(combat == null) return;
        combat.cancel();
    }

    public void daily(Player player){
        PlayerCache playerCache = PlayerCache.getPlayerCache(player);
        if(playerCache.isClaimKit("daily-rewards")){
            playerCache.getOfflinePlayer().getPlayer().getInventory().addItem(Items.getItem("daily-rewards").getKit());
            playerCache.getOfflinePlayer().getPlayer().sendMessage(plugin.getMessages().getString("claim.daily.success").replace("%prefix%", plugin.prefix));
            return;
        }
        playerCache.getOfflinePlayer().getPlayer().sendMessage(plugin.getMessages().getString("claim.daily.error").replace("%prefix%", plugin.prefix).replace("%time%", playerCache.getFormatCountdown("daily-rewards")));
    }

    public void weekly(Player player){
        PlayerCache playerCache = PlayerCache.getPlayerCache(player);
        if(playerCache.isClaimKit("weekly-rewards")){
            playerCache.getOfflinePlayer().getPlayer().getInventory().addItem(Items.getItem("weekly-rewards").getKit());
            playerCache.getOfflinePlayer().getPlayer().sendMessage(plugin.getMessages().getString("claim.weekly.success").replace("%prefix%", plugin.prefix));
            return;
        }
        playerCache.getOfflinePlayer().getPlayer().sendMessage(plugin.getMessages().getString("claim.weekly.error").replace("%prefix%", plugin.prefix).replace("%time%", playerCache.getFormatCountdown("weekly-rewards")));
    }

    public void clearKit(Player player, String id){
        if(!player.hasPermission("redstonepvp.*")){
            player.sendMessage(plugin.getMessages().getString("no-permission").replace("%prefix%", plugin.prefix));
            return;
        }
        PlayerCache playerCache = PlayerCache.getPlayerCache(player);
        playerCache.clearKit(id);
        playerCache.getOfflinePlayer().getPlayer().sendMessage(plugin.getMessages().getString("kit.clear").replace("%prefix%", plugin.prefix).replace("%name%", id));
    }

    public void clearDaily(Player player){
        if(!player.hasPermission("redstonepvp.*")){
            player.sendMessage(plugin.getMessages().getString("no-permission").replace("%prefix%", plugin.prefix));
            return;
        }
        PlayerCache playerCache = PlayerCache.getPlayerCache(player);
        playerCache.clearKit("daily-rewards");
        playerCache.getOfflinePlayer().getPlayer().sendMessage(plugin.getMessages().getString("claim.daily.clear").replace("%prefix%", plugin.prefix));
    }

    public void clearWeekly(Player player){
        if(!player.hasPermission("redstonepvp.*")){
            player.sendMessage(plugin.getMessages().getString("no-permission").replace("%prefix%", plugin.prefix));
            return;
        }
        PlayerCache playerCache = PlayerCache.getPlayerCache(player);
        playerCache.clearKit("weekly-rewards");
        playerCache.getOfflinePlayer().getPlayer().sendMessage(plugin.getMessages().getString("claim.weekly.clear").replace("%prefix%", plugin.prefix));
    }

    public void reload(CommandSender sender){
        if(!sender.hasPermission("redstonepvp.*")){
            sender.sendMessage(plugin.getMessages().getString("no-permission").replace("%prefix%", plugin.prefix));
            return;
        }
        plugin.getConfig().reload();
        plugin.getMessages().reload();
        Items.loadItems();
        ItemPotion.loadItems();
        Menus.loadMenus();
        stopTask();
        runTask();
        plugin.prefix = plugin.getMessages().getString("prefix");
        sender.sendMessage(plugin.getMessages().getString("reload").replace("%prefix%", plugin.prefix));
    }

    public void help(CommandSender sender){
        if(!sender.hasPermission("redstonepvp.*")){
            for(String msg : plugin.getMessages().getStringList("help.users")){
                sender.sendMessage(msg);
            }
            return;
        }
        for(String msg : plugin.getMessages().getStringList("help.admins")){
            sender.sendMessage(msg);
        }
    }

    public void giveKit(Player player, Items items){
        if(items == null) {
            player.sendMessage(plugin.getMessages().getString("kit.error").replace("%prefix%", plugin.prefix));
            return;
        }
        if(!player.hasPermission("redstonepvp.kits." + items.getId())){
            player.sendMessage(plugin.getMessages().getString("kit.no-permission").replace("%prefix%", plugin.prefix).replace("%name", items.getId()));
            return;
        }
        PlayerCache playerCache = PlayerCache.getPlayerCache(player);
        if(playerCache.isClaimKit(items.getId())){
            player.getInventory().addItem(items.getKit());
            player.sendMessage(plugin.getMessages().getString("item-frame.interact").replace("%prefix%", plugin.prefix));
            return;
        }
        player.sendMessage(plugin.getMessages().getString("item-frame.countdown").replace("%prefix%", plugin.prefix).replace("%time%", playerCache.getFormatCountdown(items.getId())));
    }

    public void giveKit(Player player, String id){
        Items items = Items.getItem(id);
        if(items == null) {
            player.sendMessage(plugin.getMessages().getString("kit.error").replace("%prefix%", plugin.prefix));
            return;
        }
        if(!player.hasPermission("redstonepvp.kits." + id)){
            player.sendMessage(plugin.getMessages().getString("kit.no-permission").replace("%prefix%", plugin.prefix).replace("%name%", id));
            return;
        }
        PlayerCache playerCache = PlayerCache.getPlayerCache(player);
        if(playerCache.isClaimKit(items.getId())){
            player.getInventory().addItem(items.getKit());
            player.sendMessage(plugin.getMessages().getString("kit.success").replace("%prefix%", plugin.prefix).replace("%name%", id));
            return;
        }
        player.sendMessage(plugin.getMessages().getString("kit.countdown").replace("%prefix%", plugin.prefix).replace("%time%", playerCache.getFormatCountdown(id)));
    }

    public void openTrash(Player player){
        new Menus("", plugin.getConfig().getString("trash.title").replace("%prefix%", plugin.prefix), plugin.getConfig().getInt("trash.size")).openMenu(player);
    }

    public void openMenu(Player player, String id){
        Menus menus = Menus.getMenu(id);
        if(menus == null){
            player.sendMessage(plugin.getMessages().getString("menu.error").replace("%prefix%", plugin.prefix));
            return;
        }
        menus.openMenu(player);
        player.sendMessage(plugin.getMessages().getString("menu.success").replace("%prefix%", plugin.prefix).replace("%name%", id));
    }

    public void buyItem(Player player, ItemStack costItem, int costAmount, ItemStack... buyItem){
        int itemSlot = player.getInventory().first(costItem.getType());
        if(itemSlot == -1){
            player.sendMessage(plugin.getMessages().getString("cost.error").replace("%prefix%", plugin.prefix).replace("%amount%", String.valueOf(costAmount)).replace("%item%", costItem.getType().name()));
            player.closeInventory();
            return;
        }
        ItemStack itemStack = player.getInventory().getItem(itemSlot);
        if(itemStack == null) {
            player.sendMessage(plugin.getMessages().getString("cost.error").replace("%prefix%", plugin.prefix).replace("%amount%", String.valueOf(costAmount)).replace("%item%", costItem.getType().name()));
            player.closeInventory();
            return;
        }

        if(itemStack.getAmount() < costAmount){
            player.sendMessage(plugin.getMessages().getString("cost.error").replace("%prefix%", plugin.prefix).replace("%amount%", String.valueOf(costAmount)).replace("%item%", costItem.getType().name()));
            player.closeInventory();
            return;
        }
        ItemStack cost = costItem.clone();
        cost.setAmount(costAmount);
        player.getInventory().removeItem(cost);
        player.getInventory().addItem(buyItem.clone());
        player.sendMessage(plugin.getMessages().getString("cost.success").replace("%prefix%", plugin.prefix));
    }

    public void repairItem(Player player, ItemStack costItem, int costAmount, ItemStack repairItem){
        int itemSlot = player.getInventory().first(costItem.getType());
        if(itemSlot == -1){
            player.sendMessage(plugin.getMessages().getString("anvil.error").replace("%prefix%", plugin.prefix).replace("%amount%", String.valueOf(costAmount)).replace("%item%", costItem.getType().name()));
            player.closeInventory();
            return;
        }
        ItemStack itemStack = player.getInventory().getItem(itemSlot);
        if(itemStack == null) {
            player.sendMessage(plugin.getMessages().getString("anvil.error").replace("%prefix%", plugin.prefix).replace("%amount%", String.valueOf(costAmount)).replace("%item%", costItem.getType().name()));
            player.closeInventory();
            return;
        }
        int amount = 0;
        for(ItemStack item : player.getInventory().getContents()){
            if(item == null || item.getType() == XMaterial.AIR.parseMaterial()) continue;
            if(item.getType() != costItem.getType()) continue;
            amount += item.getAmount();
        }
        if(amount < costAmount){
            player.sendMessage(plugin.getMessages().getString("anvil.error").replace("%prefix%", plugin.prefix).replace("%amount%", String.valueOf(costAmount)).replace("%item%", costItem.getType().name()));
            player.closeInventory();
            return;
        }
        ItemStack cost = costItem.clone();
        cost.setAmount(costAmount);
        player.getInventory().removeItem(cost);
        repairItem.setDurability((short) 0);
        player.sendMessage(plugin.getMessages().getString("anvil.success").replace("%prefix%", plugin.prefix));
    }

}
