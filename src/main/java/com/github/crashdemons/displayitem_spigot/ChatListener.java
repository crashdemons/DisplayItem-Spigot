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
    
    public ChatListener(){
        int records = DisplayItem.plugin.getConfig().getInt("displayitem.spamdetectionbuffer");
        int threshold = DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold");
        spampreventer = new ItemSpamPreventer(records,threshold);
    }
    
    
    @EventHandler(priority = EventPriority.HIGHEST)
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
        
        
        BaseComponent[] components = itemreplacer.chatInsertItem(event.getMessage(),format, event.getPlayer(),color);
        
        ReplacedChatEvent replacementEvent = new ReplacedChatEvent(event);
        
        String legacyMessage = "";
        for(BaseComponent component : components){
            legacyMessage+=component.toLegacyText();
        }
        //DisplayItem.plugin.getLogger().info("debug: <"+legacyMessage+">");
        replacementEvent.setMessage(legacyMessage);
        replacementEvent.setMessageComponents(components);
        Bukkit.getServer().getPluginManager().callEvent(replacementEvent);
        if(replacementEvent.isCancelled()) return;
        
        for(Player p : event.getRecipients()){
            p.spigot().sendMessage(components);
        }
        
        
        
    }
}
