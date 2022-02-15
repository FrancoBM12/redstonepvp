package com.francobm.redstonepvp.commands;

import com.francobm.redstonepvp.RedstonePvP;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Command implements CommandExecutor {
    private final RedstonePvP plugin = RedstonePvP.getInstance();

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull org.bukkit.command.Command command, @Nonnull String label, @Nonnull String[] args) {
        if(sender instanceof ConsoleCommandSender){
            if(args.length >= 1){
                switch (args[0].toLowerCase()){
                    case "help":
                        plugin.getRedstoneManager().help(sender);
                        return true;
                    case "reload":
                        plugin.getRedstoneManager().reload(sender);
                        return true;
                }
            }
            plugin.getRedstoneManager().help(sender);
            return true;
        }
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length >= 1){
                switch (args[0].toLowerCase()){
                    case "help":
                        plugin.getRedstoneManager().help(player);
                        return true;
                    case "reload":
                        plugin.getRedstoneManager().reload(player);
                        return true;
                    case "open":
                        if(args.length < 2){
                            plugin.getRedstoneManager().help(player);
                            return true;
                        }
                        plugin.getRedstoneManager().openMenu(player, args[1]);
                        return true;
                    case "daily":
                        if(args.length >= 2){
                            if(args[1].equalsIgnoreCase("clear")){
                                plugin.getRedstoneManager().clearDaily(player);
                                return true;
                            }
                        }
                        plugin.getRedstoneManager().daily(player);
                        return true;
                    case "weekly":
                        if(args.length >= 2){
                            if(args[1].equalsIgnoreCase("clear")){
                                plugin.getRedstoneManager().clearWeekly(player);
                                return true;
                            }
                        }
                        plugin.getRedstoneManager().weekly(player);
                        return true;
                    case "kit":
                        if(args.length < 2){
                            plugin.getRedstoneManager().help(player);
                            return true;
                        }
                        if(args.length == 3){
                            if(args[2].equalsIgnoreCase("clear")){
                                plugin.getRedstoneManager().clearKit(player, args[1]);
                            }
                            return true;
                        }
                        plugin.getRedstoneManager().giveKit(player, args[1]);
                        return true;
                }
            }
            plugin.getRedstoneManager().help(player);
            return true;
        }
        return true;
    }
}
