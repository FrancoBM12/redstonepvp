package com.francobm.redstonepvp.cache;

import com.francobm.redstonepvp.RedstonePvP;
import com.francobm.redstonepvp.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class Combat {
    public static Map<PlayerCache, Combat> combat = new HashMap<>();
    private final PlayerCache playerCache;
    private int countdown;

    public Combat(PlayerCache playerCache){
        this.playerCache = playerCache;
        this.countdown = 0;
    }

    public Combat(PlayerCache playerCache, int countdown){
        this.playerCache = playerCache;
        this.countdown = countdown;
    }

    public static void addCombat(PlayerCache playerCache){
        int countdown = RedstonePvP.getInstance().getConfig().getInt("combat-countdown");
        Combat c = new Combat(playerCache, countdown);
        combat.put(playerCache, c);
    }

    public static void removeCombat(PlayerCache playerCache){
        combat.remove(playerCache);
    }

    public static boolean isCombat(PlayerCache playerCache){
        return combat.containsKey(playerCache);
    }

    public static void checkCombat(PlayerCache playerCache){
        if(!playerCache.getOfflinePlayer().isOnline()) return;
        RedstonePvP plugin = RedstonePvP.getInstance();
        if(combat.containsKey(playerCache)){
            Combat c = combat.get(playerCache);
            plugin.getTitlePacket().sendActionBar(playerCache.getOfflinePlayer().getPlayer(), plugin.getMessages().getString("combat.in-combat").replace("%prefix%", plugin.prefix).replace("%time%", Utils.getTime(c.getCountdown())));
            c.decreaseCountdown();
            if(c.getCountdown() == 0){
                removeCombat(playerCache);
                plugin.getTitlePacket().sendActionBar(playerCache.getOfflinePlayer().getPlayer(), plugin.getMessages().getString("combat.out-combat").replace("%prefix%", plugin.prefix));
            }
        }
    }

    public int getCountdown() {
        return countdown;
    }

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    public void decreaseCountdown(){
        countdown--;
    }
}
