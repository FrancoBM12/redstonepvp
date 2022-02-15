package com.francobm.redstonepvp.packets;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TitlePacket {

    public void sendFullTitle(Player player, String title, String subtitle){
        sendTitlePacket(player, title, subtitle, 10, 20, 10);
    }
    public void sendTitle(Player player, String message) {
        sendTitlePacket(player, message, "", 10, 20, 10);
    }

    public void sendSubtitle(Player player, String message) {
        sendTitlePacket(player, "", message, 10, 20, 10);
    }

    public void sendActionBar(Player player, String message) {
        sendActionBarPacket(player, message);
    }

    private void sendActionBarPacket(Player player, String message){
        if(!lastVersion()) {
            try {
                Object actionBarChat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
                Constructor<?> actionBarConstructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
                Object packetActionBar = actionBarConstructor.newInstance(actionBarChat, (byte) 2);

                sendPacket(player, packetActionBar);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    }

    private void sendTitlePacket(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        if(!lastVersion()) {
            try {
                Object playOutTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                Object packetTitle = titleConstructor.newInstance(playOutTitle, chatTitle);

                Object playOutSubTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                Object chatSubTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + subTitle + "\"}");
                Constructor<?> subTitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                Object packetSubTitle = subTitleConstructor.newInstance(playOutSubTitle, chatSubTitle);

                Object PlayOutTimes = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                Constructor<?> timesConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                Object packetTimes = timesConstructor.newInstance(PlayOutTimes, null, fadeIn, stay, fadeOut);

                sendPacket(player, packetTitle);
                sendPacket(player, packetSubTitle);
                sendPacket(player, packetTimes);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else{
            player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
        }
    }

    private void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet") /* Packet class */).invoke(playerConnection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public Class<?> getNMSClass(String name){
        //import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean lastVersion(){
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return version.contains("1_17") || version.contains("1_18");
    }
}
