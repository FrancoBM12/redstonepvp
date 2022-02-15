package com.francobm.redstonepvp.cache;

import com.francobm.redstonepvp.RedstonePvP;
import com.francobm.redstonepvp.files.FileCreator;
import com.francobm.redstonepvp.utils.XMaterial;
import com.francobm.redstonepvp.utils.XPotion;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menus implements InventoryHolder {
    public static Map<String, Menus> menus = new HashMap<>();
    private final String id;
    private final Map<Integer, ItemMenu> itemMenus;
    private final Inventory inventory;

    public Menus(String id, String title, int size){
        this.id = id;
        itemMenus = new HashMap<>();
        inventory = Bukkit.createInventory(null, 9*size, title);
        setItems();
    }

    public Menus(String id, String title, int size, Map<Integer, ItemMenu> itemMenus){
        this.id = id;
        inventory = Bukkit.createInventory(this, 9*size, title);
        this.itemMenus = itemMenus;
        setItems();
    }

    public static Menus getMenu(String id){
        return menus.get(id);
    }

    private void setItems(){
        for(ItemMenu itemMenu : itemMenus.values()){
            inventory.setItem(itemMenu.getSlot(), itemMenu.getItemStack());
        }
    }

    public void openMenu(Player player){
        player.openInventory(inventory);
    }

    public static void loadMenus(){
        menus.clear();
        FileCreator config = RedstonePvP.getInstance().getConfig();
        for(String key : config.getConfigurationSection("menus").getKeys(false)){
            Map<Integer, ItemMenu> itemMenus = loadItemMenus(config, key);
            String title = config.getString("menus." + key + ".title");
            int size = config.getInt("menus." + key + ".size");
            menus.put(key, new Menus(key, title, size, itemMenus));
        }
    }

    private static Map<Integer, ItemMenu> loadItemMenus(FileCreator config, String key){
        Map<Integer, ItemMenu> itemMenus = new HashMap<>();
        for(String number : config.getConfigurationSection("menus." + key).getKeys(false)){
            if(!config.contains("menus." + key + "." + number + ".item.display")) continue;
            String display = "";
            ItemStack itemStack = null;
            List<String> lore = null;
            HashMap<Enchantment, Integer> enchant = new HashMap<>();
            ItemStack[] giveItems = null;
            int slot = 0;
            short data = 0;
            String cost = "";
            if(config.contains("menus." + key + "." + number + ".item.display")){
                display = config.getString("menus." + key + "." + number + ".item.display");
            }
            if(config.contains("menus." + key + "." + number + ".item.data")){
                data = (short) config.getInt("menus." + key + "." + number + ".item.data");
            }
            if(config.contains("menus." + key + "." + number + ".item.material")){
                String material = config.getString("menus." + key + "." + number + ".item.material");
                if(material.split(":").length < 2) {
                    try {
                        itemStack = XMaterial.valueOf(material.toUpperCase()).parseItem();
                    } catch (IllegalArgumentException exception) {
                        RedstonePvP.getInstance().getLogger().warning("Material '" + material + "' in Number '" + number + "' Not Found parsing to ItemPotion...");
                    }
                }else{
                    try{
                        int amplifier = Integer.parseInt(material.split(":")[1]);
                        int duration = Integer.parseInt(material.split(":")[2]);
                        PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(material.split(":")[0]), duration, amplifier, true, true);
                        ItemStack item = XMaterial.POTION.parseItem();
                        item.setDurability(data);
                        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                        potionMeta.addCustomEffect(potionEffect, true);
                        item.setItemMeta(potionMeta);
                        itemStack = item.clone();
                    }catch (IllegalArgumentException exception){
                        RedstonePvP.getInstance().getLogger().warning("Potion '" + material + "' in Number '" + number + "' Not Found skipping...");
                        continue;
                    }
                }
            }
            if(config.contains("menus." + key + "." + number + ".item.lore")){
                lore = config.getStringList("menus." + key + "." + number + ".item.lore");
            }
            if(config.contains("menus." + key + "." + number + ".item.enchants")){
                List<String> enchants = config.getStringList("menus." + key + "." + number + ".item.enchants");
                if(!enchants.isEmpty()) {
                    for (String ench : enchants) {
                        String[] split = ench.split(":");
                        if (split.length < 2) continue;
                        Enchantment enchantment = Enchantment.getByName(split[0]);
                        if (enchantment == null) {
                            RedstonePvP.getInstance().getLogger().warning("Enchant '" + split[0] + "' in Number '" + number + "' in Menu '" + key + "' Not Found skipping...");
                            continue;
                        }
                        enchant.put(enchantment, Integer.parseInt(split[1]));
                    }
                }
            }
            if(config.contains("menus." + key + "." + number + ".slot")){
                slot = config.getInt("menus." + key + "." + number + ".slot");
            }
            if(config.contains("menus." + key + "." + number + ".cost")){
                cost = config.getString("menus." + key + "." + number + ".cost");
            }
            if(config.contains("menus." + key + "." + number + ".give-items")){
                List<String> gItems = config.getStringList("menus." + key + "." + number + ".give-items");
                giveItems = new ItemStack[gItems.size()];
                for(int i = 0; i < gItems.size(); i++){
                    String[] split = gItems.get(i).split(";");
                    if(split.length < 2){
                        RedstonePvP.getInstance().getLogger().warning("Give Item '" + gItems.get(i) + "' in Menu '" + key + "' in Number '" + number + "' Not Found skipping...");
                        continue;
                    }
                    String material = split[0];
                    int amount = Integer.parseInt(split[1]);
                    if(split[0].equalsIgnoreCase("POTION")){
                        giveItems[i] = itemStack.clone();
                        giveItems[i].setAmount(amount);
                        giveItems[i].addUnsafeEnchantments(enchant);
                        continue;
                    }

                    try{
                        ItemStack is = XMaterial.valueOf(material.toUpperCase()).parseItem();
                        is.setAmount(amount);
                        is.addUnsafeEnchantments(enchant);
                        ItemMeta im = is.getItemMeta();
                        im.setDisplayName(display);
                        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        im.setLore(lore);
                        is.setItemMeta(im);
                        giveItems[i] = is;
                    }catch (IllegalArgumentException exception){
                        RedstonePvP.getInstance().getLogger().warning("Give Item '" + gItems.get(i) + "' Material '" + material + "' in Number '" + number + "' Not Found Parsing to ItemPotion...");
                        ItemPotion itemPotion = ItemPotion.getItemById(material);
                        if(itemPotion == null){
                            RedstonePvP.getInstance().getLogger().warning("Give Item '" + gItems.get(i) + "' Material '" + material + "' in Number '" + number + "' Not is ItemPotion skipping...");
                            continue;
                        }
                        ItemStack is = itemPotion.getItemStack().clone();
                        is.setAmount(amount);
                        giveItems[i] = is;
                    }
                }
            }
            if(itemStack == null){
                RedstonePvP.getInstance().getLogger().warning("Item in Menu '" + key + "' in Number '" + number + "' Not Found skipping...");
                continue;
            }
            itemStack.addUnsafeEnchantments(enchant);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.setDisplayName(display);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            itemMenus.put(slot, new ItemMenu(number, slot, itemStack, giveItems, cost));
        }
        return itemMenus;
    }

    public void handler(InventoryClickEvent event){
        RedstonePvP plugin = RedstonePvP.getInstance();
        Player player = (Player) event.getWhoClicked();
        if(!itemMenus.containsKey(event.getSlot())) return;
        ItemMenu itemMenu = itemMenus.get(event.getSlot());
        plugin.getRedstoneManager().buyItem(player, itemMenu.getItemCost(), itemMenu.getAmountCost(), itemMenu.getGiveItems());
    }

    @Nonnull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public String getId() {
        return id;
    }

    public Map<Integer, ItemMenu> getItemMenus() {
        return itemMenus;
    }
}
