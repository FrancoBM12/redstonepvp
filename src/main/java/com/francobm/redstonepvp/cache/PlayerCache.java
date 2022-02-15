package com.francobm.redstonepvp.cache;

import com.francobm.redstonepvp.RedstonePvP;
import com.francobm.redstonepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerCache {
    public static Map<UUID, PlayerCache> players = new HashMap<>();
    private final UUID uuid;
    private Map<String, Long> items;

    public PlayerCache(Player player){
        this.uuid = player.getUniqueId();
        items = new HashMap<>();
    }

    public PlayerCache(Player player, long dailyReward, long weeklyReward){
        this.uuid = player.getUniqueId();
    }

    public static PlayerCache getPlayerCache(Player player){
        if(!players.containsKey(player.getUniqueId())){
            PlayerCache playerCache = new PlayerCache(player);
            players.put(player.getUniqueId(), playerCache);
            return playerCache;
        }
        return players.get(player.getUniqueId());
    }

    public void loadItem(String item){
        if(item.isEmpty()) return;
        //"kit:123123123|"
        List<String> strings = new ArrayList<>();
        String[] split = item.split("\\|");
        for(String s : split){
            String[] itemSplit = s.split(":");
            addItem(itemSplit[0], Long.parseLong(itemSplit[1]));
        }
    }

    public String saveItem(){
        if(this.items.isEmpty()) return "";
        List<String> strings = new ArrayList<>();
        for(Map.Entry<String, Long> items : this.items.entrySet()){
            if(items == null) continue;
            strings.add(items.getKey() + ":" + items.getValue());
        }
        return String.join("|", strings);
    }

    public void addItems(){
        for(Items items : Items.items.values()){
            if(items == null) continue;
            if(this.items.containsKey(items.getId())) continue;
            this.items.put(items.getId(), items.getCountdown());
        }
    }

    public void addItem(String id, long countdown){
        this.items.put(id, countdown);
    }

    public static void removePlayer(PlayerCache playerCache){
        players.remove(playerCache.getUuid());
    }

    public String getFormatCountdown(String id){
        if(!items.containsKey(id)) return "NULL";
        return Utils.friendlyTimeDiff(new Date(System.currentTimeMillis()), new Date(items.get(id)));
    }

    public void clearKit(String id){
        if(!items.containsKey(id)) return;
        items.put(id, 0L);
    }

    public boolean isClaimKit(String id){
        Items items = Items.getItem(id);
        if(items == null) return false;
        if(!getOfflinePlayer().isOnline()) return false;
        long time = System.currentTimeMillis() + items.getCountdown();
        if(!this.items.containsKey(id)){
            addItem(id, time);
        }
        if(System.currentTimeMillis() >= this.items.get(id)){
            this.items.put(id, time);
            return true;
        }
        return false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(uuid);
    }

    public Map<String, Long> getItems() {
        return items;
    }
}
