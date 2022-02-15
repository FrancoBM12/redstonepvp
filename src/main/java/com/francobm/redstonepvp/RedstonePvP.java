package com.francobm.redstonepvp;

import com.francobm.redstonepvp.cache.ItemPotion;
import com.francobm.redstonepvp.cache.Items;
import com.francobm.redstonepvp.cache.Menus;
import com.francobm.redstonepvp.commands.Command;
import com.francobm.redstonepvp.commands.TrashCommand;
import com.francobm.redstonepvp.database.SQL;
import com.francobm.redstonepvp.database.SQLite;
import com.francobm.redstonepvp.files.FileCreator;
import com.francobm.redstonepvp.listeners.PlayerListener;
import com.francobm.redstonepvp.managers.RedstoneManager;
import com.francobm.redstonepvp.packets.TitlePacket;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

public final class RedstonePvP extends JavaPlugin {
    private static RedstonePvP instance;
    private FileCreator config;
    private FileCreator messages;
    public String prefix;
    private RedstoneManager redstoneManager;
    private SQL sql;
    private TitlePacket titlePacket;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        config = new FileCreator(this, "config");
        messages = new FileCreator(this, "messages");
        prefix = messages.getString("prefix");
        redstoneManager = new RedstoneManager();
        redstoneManager.runTask();
        sql = new SQLite();
        titlePacket = new TitlePacket();
        Items.loadItems();
        ItemPotion.loadItems();
        Menus.loadMenus();
        registerCommands();
        registerListeners();
    }

    public void registerCommands(){
        getCommand("redstonepvp").setExecutor(new Command());
        getCommand("trash").setExecutor(new TrashCommand());
    }

    public void registerListeners(){
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        redstoneManager.stopTask();
        sql.disconnect();
    }

    public static RedstonePvP getInstance() {
        return instance;
    }

    @Override
    public @Nonnull
    FileCreator getConfig() {
        return config;
    }

    public FileCreator getMessages() {
        return messages;
    }

    public RedstoneManager getRedstoneManager() {
        return redstoneManager;
    }

    public SQL getSQL() {
        return sql;
    }

    public TitlePacket getTitlePacket() {
        return titlePacket;
    }
}
