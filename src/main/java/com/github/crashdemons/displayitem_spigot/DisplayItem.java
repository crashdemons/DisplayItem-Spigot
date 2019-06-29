package com.github.crashdemons.displayitem_spigot;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DisplayItem
        extends JavaPlugin
        implements Listener {

    public static DisplayItem instance;
    //public boolean hasEssentials = false;

    public void reload(boolean message, CommandSender sender) {
        reloadConfig();
        if (message) {
            if (sender == null) {
                getLogger().info("DisplayItem reloaded config.");
            } else {
                sender.sendMessage(ChatColor.GOLD + "DisplayItem reloaded config.");
            }
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reload(false, null);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("displayitem")) {
            if (sender.hasPermission("displayitem.reload")) {
                DisplayItem.instance.reload(true, sender);
            }
            return true;
        }
        return false;
    }

}
