/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.events;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ShareChatEvent implements ChatEvent {
    Set<Player> recipients;
    Player player;
    String format;
    String message;
    boolean cancelled;
    
    public ShareChatEvent(Player sender, String format, String message, Set<Player> recipients){
        this.player=sender;
        this.format=format;
        this.message=message;
        this.recipients=recipients;
    }
    
    public ShareChatEvent(Player sender, String format, String message, Player recipient){
        this(sender,format,message,new HashSet<>(Collections.singleton(recipient)));
    }
    
    public ShareChatEvent(Player sender, String format, String message){
        this(sender,format,message,new HashSet<>(ImmutableList.copyOf(Bukkit.getOnlinePlayers())));
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
