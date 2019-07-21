package com.github.crashdemons.displayitem_spigot;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DisplayItem extends JavaPlugin{

    public static DisplayItem plugin=null;
    private ChatListener listener=null;
    //private final DiscordSrvCompatibility discordSrv;
    
    public DisplayItem(){
        //discordSrv = new DiscordSrvCompatibility(this);
    }
    

    public void reload(boolean message, CommandSender sender) {
        reloadConfig();
        if (message) {
            if (sender == null) {
                getLogger().info("reloaded");
            } else {
                sender.sendMessage(ChatColor.GOLD + "DisplayItem reloaded");
            }
        }
    }

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        reload(false, null);
        listener = new ChatListener();
        Bukkit.getServer().getPluginManager().registerEvents(listener, this);
        if(getConfig().getBoolean("displayitem.discordsrv")){
            //discordSrv.enable();
        }
    }
    
    @Override
    public void onDisable(){
        //discordSrv.disable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("displayitem")) return true;//not the command we expected!
        if(args.length!=0) return false;
        if (sender.hasPermission("displayitem.reload")) DisplayItem.plugin.reload(true, sender);
        else sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
        if(listener!=null) listener.reloadConfig();
        
        return true;
    }

}
