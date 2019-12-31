package com.github.crashdemons.displayitem_spigot;

import com.github.crashdemons.displayitem_spigot.plugins.placeholderapi.PlaceholderSupport;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DisplayItem extends JavaPlugin{

    public static DisplayItem plugin=null;
    private ChatListener listener=null;
    public final PlaceholderSupport placeholders;
    //private final DiscordSrvCompatibility discordSrv;
    
    public DisplayItem(){
        placeholders =  new PlaceholderSupport(this);
        //discordSrv = new DiscordSrvCompatibility(this);
    }

    public void reload(boolean message, CommandSender sender) {
        reloadConfig();
        if(listener!=null) listener.reload();
        if (message) {
            if (sender == null) {
                getLogger().info("reloaded");
            } else {
                sender.sendMessage(ChatColor.GOLD + "DisplayItem reloaded");
            }
        }
        placeholders.setSupportEnabled(getConfig().getBoolean("displayitem.integrations.placeholderapi"));
    }

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        listener = new ChatListener();
        reload(false, null);
        placeholders.activate(getConfig().getBoolean("displayitem.integrations.placeholderapi"));
        getLogger().info("enabled");
    }
    
    @Override
    public void onDisable(){
        getLogger().info("disabled");
    }

    
    private boolean onCommandReload(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length!=0) return false;
        if (sender.hasPermission("displayitem.reload")) reload(true, sender);
        else sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
        
        return true;
    }
    private boolean onCommandCalibrate(CommandSender sender, Command cmd, String label, String[] args){
        return true;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("displayitem")) return onCommandReload(sender, cmd, label, args);
        if (cmd.getName().equalsIgnoreCase("displayitemcalibrate")) return onCommandCalibrate(sender, cmd, label, args);

        return true;
    }

}
