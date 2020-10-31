package com.github.crashdemons.displayitem_spigot;

import com.github.crashdemons.displayitem_spigot.chat.ChatListener;
import com.github.crashdemons.displayitem_spigot.calibrator.Calibrator;
import com.github.crashdemons.displayitem_spigot.events.ShareCommandEvent;
import com.github.crashdemons.displayitem_spigot.plugins.placeholderapi.PlaceholderSupport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class DisplayItem extends JavaPlugin{

    public Calibrator calibrator;
    public static DisplayItem plugin=null;
    private ChatListener listener=null;
    public final PlaceholderSupport placeholders;
    //private final DiscordSrvCompatibility discordSrv;
    
    public DisplayItem(){
        placeholders =  new PlaceholderSupport(this);
        calibrator = new Calibrator(this);
        //discordSrv = new DiscordSrvCompatibility(this);
    }
    
    public void reloadListener(){
        if(listener!=null) listener.reload();
    }

    public void reload(boolean message, CommandSender sender) {
        reloadConfig();
        reloadListener();
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

    private void syncOperation(Runnable task, long tickDelay){
        getServer().getScheduler().scheduleSyncDelayedTask(this, task, tickDelay);
    }
    
    private void shareItem(Player sender, Player target){
        ShareCommandEvent event;
        if(target==null){
            event = new ShareCommandEvent(sender);
        }else{
            event = new ShareCommandEvent(sender,target);
        }
        syncOperation(()->{
            listener.forceEvent(event);
        },1L);
    }
    
    private boolean onCommandShare(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length>1) return false;
        if (!sender.hasPermission("displayitem.share")){ 
            sender.sendMessage(ChatColor.RED+"You don't have permission to use this command.");
            return true;
        }
        
        
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED+"This command can only be ran by a player.");
            return true;
        }
        Player player = (Player) sender;
        
        
        boolean canTarget = sender.hasPermission("displayitem.share.other");
        boolean canPublish = sender.hasPermission("displayitem.share.all");
        if (!canTarget && !canPublish){ 
            sender.sendMessage(ChatColor.RED+"You don't have permission to share items with anyone.");
            return true;
        }
        
        
        boolean hasTarget = args.length==1;
        boolean willPublish = args.length==0;//same as !hasTarget given our preconditions
        if(hasTarget){
            if(!canTarget){
                sender.sendMessage(ChatColor.RED+"You don't have permission to share items with someone, try just /shareitem.");
                return true;
            }
            String targetUser = args[0];
            Player target = Bukkit.getPlayer(targetUser);
            
            if(target!=null){
                if(!player.canSee(target)) target=null;//if the target can't be seen, pretend they couldn't be found.
            }
            
            
            if (target==null){ 
                sender.sendMessage(ChatColor.RED+"Can't find the user '"+targetUser+"' online.");
                return true;
            }
            shareItem(player,target);//share with user
            sender.sendMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"*You shared your item with "+ChatColor.WHITE+targetUser+ChatColor.GRAY+".");
        }
        if(willPublish){
            if(!canPublish){
                sender.sendMessage(ChatColor.RED+"You don't have permission to share items with everyone, try /shareitem <user>.");
                return true;
            }
            shareItem(player,null);//share with everyone.
        }
        
        
        
        
        return true;
    }
    
    private boolean onCommandReload(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length!=0) return false;
        if (sender.hasPermission("displayitem.reload")) reload(true, sender);
        else  sender.sendMessage(ChatColor.RED+"You don't have permission to do that."); 
        
        return true;
    }
    
    
    
    
    private boolean onCommandCalibrate(CommandSender sender, Command cmd, String label, String[] args){//TODO: Sync
        if(args.length>1) return false;
        if (!sender.hasPermission("displayitem.calibrate")){ sender.sendMessage(ChatColor.RED+"You don't have permission to do that."); return true; }
        
        if(!(sender instanceof Player)){ sender.sendMessage(ChatColor.RED+"This command must be ran as a player."); return true; }
        
        String argument = "";
        if(args.length!=0) argument = args[0];
        calibrator.calibrate((Player) sender, argument);
        
        
        //determine support for ???
        //sendmodifiedchatevent
        
        
        
        return true;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("displayitem")) return onCommandReload(sender, cmd, label, args);
        if (cmd.getName().equalsIgnoreCase("displayitemcalibrate")) return onCommandCalibrate(sender, cmd, label, args);
        if (cmd.getName().equalsIgnoreCase("displayitemshare")) return onCommandShare(sender, cmd, label, args);

        return true;
    }

}
