package com.francobm.redstonepvp.cache;

import com.francobm.redstonepvp.RedstonePvP;
import com.francobm.redstonepvp.files.FileCreator;
import com.francobm.redstonepvp.utils.XMaterial;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Items {
    public static Map<String, Items> items = new HashMap<>();
    private final String id;
    private final ItemStack[] kit;
    private final long countdown;

    public Items(boolean log, String id, ItemStack[] kit, long countdown){
        this.id = id;
        this.kit = kit;
        this.countdown = countdown;
        if(log) {
            RedstonePvP.getInstance().getLogger().info("Item '" + id + "' registered.");
        }
    }

    public Items(String id){
        this.id = id;
        this.kit = null;
        this.countdown = 0;
    }

    public static Items getItemByItemStack(ItemStack itemStack){
        if(itemStack == null) return null;
        ItemStack i = itemStack.clone();
        i.setDurability((short) 0);
        for(Items items : items.values()){
            for(ItemStack item : items.getKit()){
                if(!item.isSimilar(i)) continue;
                return items;
            }
        }
        return null;
    }

    public static Items getItem(String id){
        return items.get(id);
    }

    public static void loadItems(){
        items.clear();
        FileCreator config = RedstonePvP.getInstance().getConfig();
        for(String key : config.getConfigurationSection("items").getKeys(false)){
            ItemStack[] kit = ItemGive(config, key);
            long countdown = config.getLong("items." + key + ".countdown") * 1000;
            items.put(key, new Items(true, key, kit, countdown));
        }
    }

    private static ItemStack[] ItemGive(FileCreator config, String key){
        List<ItemStack> items = new ArrayList<>();
        for(String number : config.getConfigurationSection("items." + key).getKeys(false)){
            if(!config.contains("items." + key + "." + number + ".item")) continue;
            ItemStack itemStack;
            String name = config.getString("items." + key + "." + number + ".item.display");
            String material = config.getString("items." + key + "." + number + ".item.material");
            int amount = config.getInt("items." + key + "." + number + ".item.amount");
            List<String> lore = config.getStringList("items." + key + "." + number + ".item.lore");
            List<String> enchant = new ArrayList<>();

            if(config.contains("items." + key + "." + number + ".item.enchants")){
                enchant = config.getStringList("items." + key + "." + number + ".item.enchants");
            }

            if(material == null) {
                RedstonePvP.getInstance().getLogger().warning("Item '" + key + "' Material not found!");
                continue;
            }
            try{
                itemStack = XMaterial.valueOf(material.toUpperCase()).parseItem();
            }catch (IllegalArgumentException exception){
                RedstonePvP.getInstance().getLogger().warning("Item '" + key + "' Material '" + material + "' not found!");
                continue;
            }
            if(!enchant.isEmpty()) {
                for (String ench : enchant) {
                    String[] split = ench.split(":");
                    if (split.length < 2) continue;
                    Enchantment enchantment = Enchantment.getByName(split[0]);
                    if (enchantment == null) {
                        RedstonePvP.getInstance().getLogger().warning("Enchant '" + split[0] + "' in Item '" + material + "' in Kit '" + key + "' Not Found skipping...");
                        continue;
                    }
                    itemStack.addUnsafeEnchantment(enchantment, Integer.parseInt(split[1]));
                }
            }
            itemStack.setAmount(amount);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.setDisplayName(name);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            items.add(itemStack);
        }
        return items.toArray(new ItemStack[0]);
    }

    public String getId() {
        return id;
    }

    public ItemStack[] getKit() {
        return kit;
    }

    public long getCountdown() {
        return countdown;
    }
}
