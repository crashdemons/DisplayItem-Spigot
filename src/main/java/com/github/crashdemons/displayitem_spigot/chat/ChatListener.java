/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.chat;

import com.github.crashdemons.displayitem_spigot.DisplayItem;
import com.github.crashdemons.displayitem_spigot.events.ChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ChatListener implements Listener {

    EventPriority listenerpriority;
    ChatEventExecutor executor = null;
    
    public ChatListener(){
    }
    
    public void registerEvents(){
        executor = new ChatEventExecutor(this,listenerpriority);
        Bukkit.getServer().getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, listenerpriority, executor, DisplayItem.plugin, true);
        DisplayItem.plugin.getLogger().info("Registered listener");
    }
    
    public void unregisterEvents(){
        HandlerList.unregisterAll(this);
        executor=null;
        DisplayItem.plugin.getLogger().info("Unregistered listener");
    }
    
    public void reloadEvents(){
        unregisterEvents();
        registerEvents();
    }
    
    @Nullable
    public ChatEventExecutor getExecutor(){
        return executor;
    }
    
    public boolean forceEvent(ChatEvent e){
        if(executor==null) return false;
        executor.onChat(e);
        return true;
    }
    
    private void reloadPriority(){
        String priorityString = DisplayItem.plugin.getConfig().getString("displayitem.listenerpriority");
        try{
            listenerpriority = EventPriority.valueOf(priorityString.toUpperCase());
            if(listenerpriority==EventPriority.MONITOR) throw new IllegalArgumentException("Monitor priority is not allowed for modification events");
        }catch(Exception e){
            DisplayItem.plugin.getLogger().info("Invalid priority value: "+priorityString);
            listenerpriority = EventPriority.NORMAL;
        }
        DisplayItem.plugin.getLogger().info("Listener priority: "+listenerpriority.name());
    }
    
    public void reload(){
        reloadPriority();
        reloadEvents();
    }
    
    
    
    
    
    

}
