package com.francobm.redstonepvp.listeners;

import com.francobm.redstonepvp.RedstonePvP;
import com.francobm.redstonepvp.cache.*;
import com.francobm.redstonepvp.utils.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class PlayerListener implements Listener {
    private final RedstonePvP plugin = RedstonePvP.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        plugin.getSQL().loadPlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        PlayerCache playerCache = PlayerCache.getPlayerCache(player);
        if(Combat.isCombat(playerCache)){
            player.setHealth(0);
        }
        plugin.getSQL().savePlayer(playerCache);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAttack(EntityDamageByEntityEvent event){
        if(event.isCancelled() || event.getDamage() == 0) return;
        if(event.getEntity() instanceof Player) {
            Player entity = (Player) event.getEntity();
            if(!entity.isOnline()) return;
            for (int i = 0; i < plugin.getConfig().getInt("redstone-dropping"); i++) {
                Item item = entity.getWorld().dropItem(entity.getLocation(), XMaterial.REDSTONE.parseItem());
                item.setPickupDelay(20);
            }
        }
        if(event.getDamager() instanceof Player){
            Player attacker = (Player) event.getDamager();
            if((event.getEntity() instanceof Player)) {
                Player victim = (Player) event.getEntity();
                PlayerCache victimCache = PlayerCache.getPlayerCache(victim);
                PlayerCache attackCache = PlayerCache.getPlayerCache(attacker);
                Combat.addCombat(victimCache);
                Combat.addCombat(attackCache);
            }
            if(event.getEntity() instanceof ItemFrame){
                if(!attacker.hasPermission("redstonepvp.*")) {
                    event.setCancelled(true);
                    return;
                }
                if(attacker.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    return;
                }
                ItemFrame itemFrame = (ItemFrame) event.getEntity();
                if(itemFrame.getItem().getType() == XMaterial.AIR.parseMaterial()) return;
                Items items = Items.getItemByItemStack(itemFrame.getItem());
                if(items == null) return;
                event.setCancelled(true);
                itemFrame.setItem(null);
                attacker.sendMessage(plugin.getMessages().getString("item-frame.remove").replace("%prefix%", plugin.prefix));
                return;
            }
            if(!(event.getEntity() instanceof LivingEntity)) return;
            ItemStack itemInHand = attacker.getInventory().getItemInHand();
            ItemPotion itemPotion = ItemPotion.getItemByItemStack(itemInHand);
            if(itemPotion == null){
                return;
            }
            itemPotion.getPotion().apply((LivingEntity) event.getEntity());
        }
    }

    @EventHandler
    public void onItemFrame(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if(!(event.getRightClicked() instanceof ItemFrame)) return;
        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
        if(player.hasPermission("redstonepvp.*")){
            if(player.getGameMode() == GameMode.CREATIVE){
                Items items = Items.getItemByItemStack(player.getInventory().getItemInHand());
                if(items == null) return;
                itemFrame.setItem(player.getInventory().getItemInHand().clone());
                player.getInventory().setItemInHand(null);
                player.sendMessage(plugin.getMessages().getString("item-frame.put").replace("%prefix%", plugin.prefix));
                return;
            }
        }
        if(itemFrame.getItem().getType() == XMaterial.AIR.parseMaterial()) return;
        Items items = Items.getItemByItemStack(itemFrame.getItem());
        if(items == null) return;
        plugin.getRedstoneManager().giveKit(player, items);
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        Item item = event.getItemDrop();
        if(item.getItemStack().getType() == XMaterial.AIR.parseMaterial()) return;
        Items items = Items.getItemByItemStack(item.getItemStack());
        if(items == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player victim = event.getEntity();
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while(iterator.hasNext()){
            ItemStack itemStack = iterator.next();
            Items items = Items.getItemByItemStack(itemStack);
            if(items == null){
                continue;
            }
            iterator.remove();
        }
        if(event.getEntity().getKiller() != null) {
            Player attacker = event.getEntity().getKiller();
            ItemStack itemStack = attacker.getInventory().getItemInHand();
            if(itemStack.getType() == XMaterial.AIR.parseMaterial()) return;
            if(itemStack.getItemMeta() == null) {
                event.setDeathMessage(plugin.getMessages().getString("death").replace("%prefix%", plugin.prefix).replace("%attacker%", attacker.getName()).replace("%victim%", victim.getName()).replace("%item%", itemStack.getType().name()));
                return;
            }
            if(!itemStack.getItemMeta().hasDisplayName()){
                event.setDeathMessage(plugin.getMessages().getString("death").replace("%prefix%", plugin.prefix).replace("%attacker%", attacker.getName()).replace("%victim%", victim.getName()).replace("%item%", itemStack.getType().name()));
                return;
            }
            event.setDeathMessage(plugin.getMessages().getString("death").replace("%prefix%", plugin.prefix).replace("%attacker%", attacker.getName()).replace("%victim%", victim.getName()).replace("%item%", itemStack.getItemMeta().getDisplayName()));
        }
    }

    @EventHandler
    public void onAnvil(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Block block = event.getClickedBlock();
        if(block == null) return;
        if(block.getType() != XMaterial.ANVIL.parseMaterial()) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(itemStack == null || itemStack.getType() == XMaterial.AIR.parseMaterial()) {
            player.sendMessage(plugin.getMessages().getString("anvil.not-have-item").replace("%prefix%", plugin.prefix));
            event.setCancelled(true);
            return;
        }
        if(!Enchantment.DURABILITY.canEnchantItem(itemStack)){
            player.sendMessage(plugin.getMessages().getString("anvil.not-is-item").replace("%prefix%", plugin.prefix));
            event.setCancelled(true);
            return;
        }
        String[] split = plugin.getConfig().getString("anvil-repair-cost").split("\\|");
        plugin.getRedstoneManager().repairItem(player, XMaterial.valueOf(split[1].toUpperCase()).parseItem(), Integer.parseInt(split[0]), itemStack);
        event.setCancelled(true);
    }

    /*
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        PlayerCache playerCache = PlayerCache.getPlayerCache(player);
        if(!Combat.isCombat(playerCache)) return;
        event.setCancelled(true);
        player.sendMessage(plugin.getMessages().getString("combat.teleport").replace("%prefix%", plugin.prefix));
    }*/

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        PlayerCache playerCache = PlayerCache.getPlayerCache(player);
        if(!Combat.isCombat(playerCache)) return;
        event.setCancelled(true);
        player.sendMessage(plugin.getMessages().getString("combat.command").replace("%prefix%", plugin.prefix));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof Menus){
            event.setCancelled(true);
            if(event.getCurrentItem() == null) return;
            if(event.getClickedInventory() == null) return;
            if(event.getClickedInventory().getType() == InventoryType.PLAYER) return;
            Menus menus = (Menus) holder;
            menus.handler(event);
        }
    }
}