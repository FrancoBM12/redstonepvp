package com.francobm.redstonepvp.cache;

import com.francobm.redstonepvp.utils.XMaterial;
import org.bukkit.inventory.ItemStack;

public class ItemMenu {
    private final String id;
    private final int slot;
    private final ItemStack itemStack;
    private final ItemStack[] giveItems;
    private final String cost;

    public ItemMenu(String id){
        this.id = id;
        slot = 0;
        itemStack = null;
        cost = "";
        giveItems = null;
    }

    public ItemMenu(String id, int slot, ItemStack itemStack, ItemStack[] giveItems, String cost){
        this.id = id;
        this.slot = slot;
        this.itemStack = itemStack;
        this.giveItems = giveItems;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getAmountCost(){
        String[] amount = cost.split("\\|");
        return Integer.parseInt(amount[0]);
    }

    public ItemStack getItemCost(){
        String[] amount = cost.split("\\|");
        return XMaterial.valueOf(amount[1].toUpperCase()).parseItem();
    }

    public String getCost() {
        return cost;
    }

    public ItemStack[] getGiveItems() {
        return giveItems;
    }
}
