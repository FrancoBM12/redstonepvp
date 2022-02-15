package com.francobm.redstonepvp.commands;

import com.francobm.redstonepvp.RedstonePvP;
import com.francobm.redstonepvp.cache.Menus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TrashCommand implements CommandExecutor {
    private RedstonePvP plugin = RedstonePvP.getInstance();

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull  String label, @Nonnull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            plugin.getRedstoneManager().openTrash(player);
        }
        return true;
    }
}
