/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import com.github.crashdemons.displayitem_spigot.antispam.ItemSpamPreventer;
import net.md_5.bungee.api.chat.BaseComponent;
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
    Chat_bukkit itemreplacer = new Chat_bukkit();
    ItemSpamPreventer spampreventer = null;
    
    public ChatListener(){
        int records = DisplayItem.instance.getConfig().getInt("displayitem.spamdetectionbuffer");
        int threshold = DisplayItem.instance.getConfig().getInt("displayitem.spamthreshold");
        spampreventer = new ItemSpamPreventer(records,threshold);
    }
    
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event){
        
        Player player = event.getPlayer();
        String format = event.getFormat();
        String message = event.getMessage();
        
        String replacestr=DisplayItem.instance.getConfig().getString("displayitem.replacement");
        int start = message.indexOf(replacestr);
        if(start==-1) return;
        if(!player.hasPermission("displayitem.replace")) return;
        
        if(!player.hasPermission("displayitem.bypasscooldown")){
            if(spampreventer.recordEvent(event).isSpam()){
                String errormessage = DisplayItem.instance.getConfig().getString("displayitem.messages.cooldown");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', errormessage));
                return;
            }
        }
        
        event.setCancelled(true);
        
        boolean color = player.hasPermission("displayitem.colorname");
        
        
        BaseComponent[] components = itemreplacer.replaceItem(event.getPlayer(), event.getMessage(),format,color);
        
        for(Player p : event.getRecipients()){
            p.spigot().sendMessage(components);
        }
        
        
        
    }
}
