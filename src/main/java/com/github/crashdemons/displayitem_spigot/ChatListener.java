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
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ChatListener implements Listener {
    ChatFormatter itemreplacer = new ChatFormatter();
    ItemSpamPreventer spampreventer = null;
    EventPriority listenerpriority;
    
    public ChatListener(){
        reloadConfig();
    }
    
    private final void reloadPriority(){
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
    
    public final void reloadConfig(){
        int records = DisplayItem.plugin.getConfig().getInt("displayitem.spamdetectionbuffer");
        int threshold = DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold");
        spampreventer = new ItemSpamPreventer(records,threshold);
        reloadPriority();
    }
    
    //bad approach but what can I say...
    @EventHandler(priority=EventPriority.LOWEST,ignoreCancelled=true)
    public void onChatLowest(AsyncPlayerChatEvent event){
        if(listenerpriority==EventPriority.LOWEST) onChat(event);
    }
    @EventHandler(priority=EventPriority.LOW,ignoreCancelled=true)
    public void onChatLow(AsyncPlayerChatEvent event){
        if(listenerpriority==EventPriority.LOW) onChat(event);
    }
    @EventHandler(priority=EventPriority.NORMAL,ignoreCancelled=true)
    public void onChatNormal(AsyncPlayerChatEvent event){
        if(listenerpriority==EventPriority.NORMAL) onChat(event);
    }
    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)
    public void onChatHigh(AsyncPlayerChatEvent event){
        if(listenerpriority==EventPriority.HIGH) onChat(event);
    }
    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void onChatHighest(AsyncPlayerChatEvent event){
        if(listenerpriority==EventPriority.HIGHEST) onChat(event);
    }
    
    
    //@EventHandler(priority = EventPriority.LOWEST,ignoreCancelled=true)
    public void onChat(AsyncPlayerChatEvent event){
        if(event instanceof ReplacedChatEvent) return;
        Player player = event.getPlayer();
        String format = event.getFormat();
        String message = event.getMessage();
        
        String replacestr=DisplayItem.plugin.getConfig().getString("displayitem.replacement");
        int start = message.indexOf(replacestr);
        if(start==-1) return;
        if(!player.hasPermission("displayitem.replace")) return;
        
        if(!player.hasPermission("displayitem.bypasscooldown")){
            if(spampreventer.recordEvent(event).isSpam()){
                String errormessage = DisplayItem.plugin.getConfig().getString("displayitem.messages.cooldown");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', errormessage));
                return;
            }
        }
        
        event.setCancelled(true);
        
        boolean color = player.hasPermission("displayitem.colorname");
        
        
        BaseComponent[] componentsChat = itemreplacer.chatInsertItem(event.getMessage(),format, event.getPlayer(),color,true);
        BaseComponent[] componentsMessage = itemreplacer.chatInsertItem(event.getMessage(),format, event.getPlayer(),color,false);
        
        
        ReplacedChatEvent replacementEvent = new ReplacedChatEvent(event);
        
        String legacyMessage = "";
        for(BaseComponent component : componentsMessage){
            legacyMessage+=component.toLegacyText();
        }
        //DisplayItem.plugin.getLogger().info("debug: <"+legacyMessage+">");
        replacementEvent.setMessage(legacyMessage);
        replacementEvent.setMessageComponents(componentsChat);
        Bukkit.getServer().getPluginManager().callEvent(replacementEvent);
        if(replacementEvent.isCancelled()) return;
        
        for(Player p : event.getRecipients()){
            p.spigot().sendMessage(componentsChat);
        }
        
        
        
    }
}
