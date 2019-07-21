/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import com.github.crashdemons.displayitem_spigot.antispam.ItemSpamPreventer;
import com.github.crashdemons.displayitem_spigot.events.ReplacedChatEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ChatListener implements Listener {

    EventPriority listenerpriority;
    
    public ChatListener(){
    }
    
    public void registerEvents(){
        Bukkit.getServer().getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, listenerpriority, new ChatEventExecutor(this,listenerpriority), DisplayItem.plugin, true);
        DisplayItem.plugin.getLogger().warning("Registered listener");
    }
    
    public void unregisterEvents(){
        HandlerList.unregisterAll(this);
        DisplayItem.plugin.getLogger().warning("Unregistered listener");
    }
    
    public void reloadEvents(){
        unregisterEvents();
        registerEvents();
    }
    
    private void reloadPriority(){
        String priorityString = DisplayItem.plugin.getConfig().getString("displayitem.listenerpriority");
        try{
            listenerpriority = EventPriority.valueOf(priorityString.toUpperCase());
            if(listenerpriority==EventPriority.MONITOR) throw new IllegalArgumentException("Monitor priority is not allowed for modification events");
        }catch(Exception e){
            DisplayItem.plugin.getLogger().warning("Invalid priority value: "+priorityString);
            listenerpriority = EventPriority.NORMAL;
        }
        DisplayItem.plugin.getLogger().warning("Listener priority: "+listenerpriority.name());
    }
    
    public void reload(){
        reloadPriority();
        reloadEvents();
    }
    
    
    
    
    
    

}
