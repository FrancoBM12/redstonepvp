package com.francobm.redstonepvp.cache;

import com.francobm.redstonepvp.RedstonePvP;
import com.francobm.redstonepvp.files.FileCreator;
import com.francobm.redstonepvp.utils.XMaterial;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemPotion {
    public static Map<String, ItemPotion> itemsPotion = new HashMap<>();
    private final String id;
    private final ItemStack itemStack;
    private final PotionEffect potion;

    public ItemPotion(String id){
        this.id = id;
        itemStack = null;
        potion = null;
    }

    public ItemPotion(String id, ItemStack itemStack, PotionEffect potion){
        this.id = id;
        this.itemStack = itemStack;
        this.potion = potion;
    }

    public static ItemPotion getItemById(String id){
        return itemsPotion.get(id);
    }

    public static ItemPotion getItemByItemStack(ItemStack itemStack){
        for(ItemPotion itemPotion : itemsPotion.values()){
            if(!itemPotion.getItemStack().isSimilar(itemStack)) continue;
            return itemPotion;
        }
        return null;
    }

    public static void loadItems(){
        FileCreator config = RedstonePvP.getInstance().getConfig();
        itemsPotion.clear();
        for(String key : config.getConfigurationSection("items-potion").getKeys(false)){
            ItemStack itemStack;
            String name = config.getString("items-potion." + key + ".item.display");
            String material = config.getString("items-potion." + key + ".item.material");
            int amount = config.getInt("items-potion." + key + "." + ".item.amount");
            List<String> lore = config.getStringList("items-potion." + key + "." + ".item.lore");
            String[] potion = config.getString("items-potion." + key + ".item.potion").split(":");
            List<String> enchant = new ArrayList<>();
            PotionEffectType potionEffectType = PotionEffectType.getByName(potion[0]);
            if(potionEffectType == null){
                RedstonePvP.getInstance().getLogger().warning("Potion Type '" + potion[0] + "' in  '" + key + "' not found!");
                continue;
            }
            PotionEffect potionEffect = new PotionEffect(potionEffectType, Integer.parseInt(potion[2]), Integer.parseInt(potion[1]));

            if(config.contains("items-potion." + key + "." + ".item.enchants")){
                enchant = config.getStringList("items-potion." + key + "." + ".item.enchants");
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
            itemsPotion.put(key, new ItemPotion(key, itemStack, potionEffect));
        }
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public PotionEffect getPotion() {
        return potion;
    }

    public String getId() {
        return id;
    }
}
