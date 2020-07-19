/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.events;

import com.github.crashdemons.displayitem_spigot.DisplayItem;
import com.github.crashdemons.displayitem_spigot.MacroReplacements;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ShareCommandEvent implements ChatEvent {
    Set<Player> recipients;
    Player player;
    String format;
    String message;
    boolean cancelled;
    
    private static String getFormat(String name){
        return DisplayItem.plugin.getConfig().getString("displayitem.shareformat"+name);
    }
    
    
    public ShareCommandEvent(Player sender, Set<Player> recipients, String format, String message){
        //String format = DisplayItem.plugin.getConfig().getString("displayitem.shareformatbukkit");
        //String message = DisplayItem.plugin.getConfig().getString("displayitem.shareformatmessage");
        format = MacroReplacements.replaceAll(player, format, "%2$s", "", false, -1, false);
        message = MacroReplacements.replaceAll(player, message, "", "", false, -1, false);
        message = ChatColor.translateAlternateColorCodes('&', message);
        
        
        this.player=sender;
        this.format=format;
        this.message=message;
        this.recipients=recipients;
    }
    
    public ShareCommandEvent(Player sender, Player recipient){
        this(sender,new HashSet<>(Collections.singleton(recipient)),getFormat("bukkitprivate"),getFormat("messageprivate"));
    }
    
    public ShareCommandEvent(Player sender){
        this(sender,new HashSet<>(ImmutableList.copyOf(Bukkit.getOnlinePlayers())),getFormat("bukkit"),getFormat("message"));
    }
    
    public Player getPlayer(){
        return player;
    }
    public String getFormat(){
        return format;
    }
    public String getMessage(){
        return message;
    }
    public Set<Player> getRecipients(){
        return recipients;
    }
    public boolean isCancelled(){
        return cancelled;
    }
    public void setCancelled​(boolean cancel){
        cancelled=cancel;
    }
    public void setFormat​(String format){
        this.format=format;
    }
    public void setMessage​(String message){
        this.message=message;
    }
    public boolean isAsynchronous(){//TODO: verify? allow configuring it?
        return false;
    }
    
}
