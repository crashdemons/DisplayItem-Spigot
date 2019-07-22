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
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ChatEventExecutor implements EventExecutor {
    private final ChatLineFormatter itemreplacer = new ChatLineFormatter();
    private final ItemSpamPreventer spampreventer;
    private final ChatListener listener;
    private final EventPriority priority;
    
    
    public ChatEventExecutor(ChatListener listener, EventPriority priority){
        this.listener=listener;
        this.priority=priority;
        int records = DisplayItem.plugin.getConfig().getInt("displayitem.spamdetectionbuffer");
        int threshold = DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold");
        spampreventer = new ItemSpamPreventer(records,threshold);
    }
    
    @Override
    public void execute(Listener listener, Event originalEvent) throws EventException {
        if(!(originalEvent instanceof AsyncPlayerChatEvent)) return;
        AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) originalEvent;
        onChat(event);
    }
    
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
        
        
        SplitChatMessage chatLineSplit = itemreplacer.chatLineInsertItem(event.getPlayer(), format, message, color);
        
        
        ReplacedChatEvent replacementEvent = new ReplacedChatEvent(event);
        
        String legacyMessage = "";
        for(BaseComponent component : chatLineSplit.content){
            legacyMessage+=component.toLegacyText();
        }
        //DisplayItem.plugin.getLogger().info("debug: <"+legacyMessage+">");
        replacementEvent.setMessage(legacyMessage);
        replacementEvent.setMessageComponents(chatLineSplit.content);
        Bukkit.getServer().getPluginManager().callEvent(replacementEvent);
        if(replacementEvent.isCancelled()) return;
        
        for(Player p : event.getRecipients()){
            p.spigot().sendMessage(chatLineSplit.toComponents());
        }
    }
    
}
