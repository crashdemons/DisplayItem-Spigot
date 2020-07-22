/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.chat;

import com.github.crashdemons.displayitem_spigot.MacroReplacements;
import com.github.crashdemons.displayitem_spigot.DisplayItem;
import com.github.crashdemons.displayitem_spigot.antispam.ItemSpamPreventer;
import com.github.crashdemons.displayitem_spigot.antispam.SpamResult;
import com.github.crashdemons.displayitem_spigot.events.ChatEvent;
import com.github.crashdemons.displayitem_spigot.events.AsyncPlayerChatEventAdapter;
import com.github.crashdemons.displayitem_spigot.events.ReplacedChatEvent;
import com.github.crashdemons.displayitem_spigot.events.ShareCommandEvent;
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
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ChatEventExecutor implements EventExecutor {
    private final ChatLineFormatter itemreplacer = new ChatLineFormatter();
    private final ItemSpamPreventer spampreventer;
    private final ChatListener listener;
    private final EventPriority priority;
    private final boolean sendModifiedChatEvent;
    
    
    
    public ChatEventExecutor(ChatListener listener, EventPriority priority){
        this.listener=listener;
        this.priority=priority;
        int records = DisplayItem.plugin.getConfig().getInt("displayitem.spamdetectionbuffer");
        int threshold = DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold");
        spampreventer = new ItemSpamPreventer(records,threshold);
        sendModifiedChatEvent = DisplayItem.plugin.getConfig().getBoolean("displayitem.sendmodifiedchatevent");
    }
    
    @Override
    public void execute(Listener listener, Event originalEvent) throws EventException {
        if(!(originalEvent instanceof AsyncPlayerChatEvent)) return;
        AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) originalEvent;
        onChat(event);
    }
    
    private boolean checkSpam(Player player,String message){
        if(!player.hasPermission("displayitem.bypasscooldown")){


            boolean detectionsResetTimer = DisplayItem.plugin.getConfig().getBoolean("displayitem.spamdetectionsresetcooldown");
            boolean recordEventConditionally = !detectionsResetTimer;//detections reset the cooldown by recording everything (unconditionally)
            SpamResult spamResult = spampreventer.recordEvent(player,recordEventConditionally,false);
            if(spamResult.isSpam()){
                long cooldown = DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold");
                String errormessage = DisplayItem.plugin.getConfig().getString("displayitem.messages.cooldown");
                errormessage = ChatColor.translateAlternateColorCodes('&', errormessage);
                errormessage = MacroReplacements.replaceAll(player, errormessage, message, "", false, spamResult.getTimeUntil(cooldown), true);
                player.sendMessage(errormessage);
                //itemNeedsReplacing=false;
                return true;
            }
        }
        return false;
    }
    
    private SplitChatMessage replaceItem(Player player, String format, String message, boolean isShareCommand){
        boolean color = player.hasPermission("displayitem.colorname");
        boolean overridechatformat = !isShareCommand && DisplayItem.plugin.getConfig().getBoolean("displayitem.overridechatformat");
        return itemreplacer.chatLineInsertItem(player, format, message, color, overridechatformat);
    }
    
    public void onChat(@NotNull AsyncPlayerChatEvent event){
        onChat(new AsyncPlayerChatEventAdapter(event));
    }
    
    public void onChat(@NotNull ChatEvent event){
        if(event instanceof ReplacedChatEvent) return;
        boolean isShareCommand = event instanceof ShareCommandEvent;
        
        Player player = event.getPlayer();
        String format = event.getFormat();
        String message = event.getMessage();
        
        String replacestr=DisplayItem.plugin.getConfig().getString("displayitem.replacement");
        String metareplacestr=DisplayItem.plugin.getConfig().getString("displayitem.metareplacement");
        
        
        boolean itemNeedsReplacing = message.contains(replacestr);
        if(!isShareCommand && !player.hasPermission("displayitem.replace")) itemNeedsReplacing=false;
        
        
        if(itemNeedsReplacing && checkSpam(player,message)) itemNeedsReplacing=false;

        if(itemNeedsReplacing){
            event.setCancelled(true);
            

            SplitChatMessage chatLineSplit = replaceItem(player, format, message,isShareCommand);


            ReplacedChatEvent replacementEvent = new ReplacedChatEvent(event);

            String legacyMessage = "";
            for(BaseComponent component : chatLineSplit.content){
                legacyMessage+=component.toLegacyText();
            }
            //DisplayItem.plugin.getLogger().info("debug: <"+legacyMessage+">");
            replacementEvent.setMessage(legacyMessage);
            replacementEvent.setMessageComponents(chatLineSplit.content);

            if(sendModifiedChatEvent){
                Bukkit.getServer().getPluginManager().callEvent(replacementEvent);
                if(replacementEvent.isCancelled()) return;
            }

            for(Player p : event.getRecipients()){
                p.spigot().sendMessage(chatLineSplit.toComponents());
            }
        }
        //make sure we replace [I]->[i] on the original event, regardless of it being cancelled.
        if((!metareplacestr.isEmpty()) && message.contains(metareplacestr)){
            message=message.replace(metareplacestr, replacestr);
            event.setMessage(message);
        }
    }
    
}
