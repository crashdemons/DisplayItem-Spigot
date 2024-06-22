/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.calibrator;

import com.github.crashdemons.displayitem_spigot.DisplayItem;
import java.util.Collections;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class Calibrator {
    private final Object lock = new Object();
    private CalibrationState state = CalibrationState.UNSTARTED;
    private DisplayItem plugin;
    public Calibrator(DisplayItem di){
        plugin = di;
    }
    
    private void sendTestMessage(Player sender, String extraInfo    ){
        boolean async = false;
        String replacement = plugin.getConfig().getString("displayitem.replacement");
        Set<Player> recipients = Collections.singleton(sender);
        Bukkit.getPluginManager().callEvent(
            new AsyncPlayerChatEvent(async, sender, "prefix " + replacement + " suffix ("+extraInfo+")",recipients)
        );
    }
    
    private void doPriorityTests(Player sender){
        plugin.getConfig().set("displayitem.sendmodifiedchatevent", false);
        for(EventPriority priority : EventPriority.values()){
            if(priority==EventPriority.MONITOR) continue;//monitor is not a valid priority for modification events.
            plugin.getConfig().set("displayitem.listenerpriority", priority.name());
            plugin.reloadListener();
            sendTestMessage(sender, priority.name().toUpperCase());
        }
    }
    private void doEventTests(Player sender){
        plugin.getConfig().set("displayitem.sendmodifiedchatevent", false);
        plugin.reloadListener();
        sendTestMessage(sender, "false");
        
        plugin.getConfig().set("displayitem.sendmodifiedchatevent", true);
        plugin.reloadListener();
        sendTestMessage(sender, "true");
        
    }
    
    
    private void setPriority(Player sender, String argument){
        try{
            EventPriority priority = EventPriority.valueOf(argument);
            plugin.getConfig().set("displayitem.listenerpriority", priority.name().toUpperCase());
            sender.sendMessage(ChatColor.GREEN+"DI listenerpriority is now: "+priority.name().toUpperCase());
            state = CalibrationState.PRIORITY_SET;
        }catch(IllegalArgumentException ex){
            sender.sendMessage(ChatColor.RED+"Invalid priority value. Must be one of: HIGHEST, HIGH, NORMAL, LOW, LOWEST.");
        }
        
    }
    private void setEvent(Player sender, String argument){
        try{
            boolean value;
            if(argument.equalsIgnoreCase("true")){ value = true; } // we don't use Boolean.valueOf because it considers all non-true inputs to be default False
            else if(argument.equalsIgnoreCase("false")){ value = false; }
            else throw new IllegalArgumentException("Invalid boolean value");
            
            plugin.getConfig().set("displayitem.sendmodifiedchatevent", value);
            sender.sendMessage(ChatColor.GREEN+"DI sendmodifiedchatevent is now: "+value);
            state = CalibrationState.EVENT_SET;
        }catch(IllegalArgumentException ex){
            sender.sendMessage(ChatColor.RED+"Invalid boolean value. Must be True or False.");
        }
    }
    
    public void calibrate(Player sender, String argument){
        synchronized(lock){
            switch(state){
                case PRIORITY_PROMPTED:
                    setPriority(sender, argument);
                    break;
                case EVENT_PROMPTED:
                    setEvent(sender, argument);
                    break;
            }
        }
        plugin.saveConfig();
        resumeCalibration(sender);
    }
    
    public void resumeCalibration(Player sender){
        synchronized(lock){
            switch(state){
                case FINISHED:
                case UNSTARTED:
                case PRIORITY_PROMPTED:
                    sender.sendMessage(ChatColor.GOLD+"DisplayItem calibrating 'listenerpriority':");
                    doPriorityTests(sender);
                    sender.sendMessage(ChatColor.YELLOW+"Choose the priority option that is formatted best and");
                    sender.sendMessage(ChatColor.YELLOW+"type "+ChatColor.AQUA+"/diset VALUE"+ChatColor.YELLOW+" where VALUE is the priority you chose.");
                    state = CalibrationState.PRIORITY_PROMPTED;
                    break;
                case PRIORITY_SET:
                case EVENT_PROMPTED:
                    sender.sendMessage(ChatColor.GOLD+"DisplayItem calibrating 'sendmodifiedchatevent':");
                    doEventTests(sender);
                    sender.sendMessage(ChatColor.YELLOW+"Choose the event state option that works the best and");
                    sender.sendMessage(ChatColor.YELLOW+"type "+ChatColor.AQUA+"/diset VALUE"+ChatColor.YELLOW+" where VALUE is the state you chose.");
                    state = CalibrationState.EVENT_PROMPTED;
                    break;
                case EVENT_SET:
                    sender.sendMessage(ChatColor.GOLD+"DisplayItem calibration finished.");
                    state = CalibrationState.FINISHED;
            }
        }
    }
}
