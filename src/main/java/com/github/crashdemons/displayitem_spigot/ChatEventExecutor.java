/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import com.github.crashdemons.displayitem_spigot.concurrency.DeferredChatEventParameters;
import com.github.crashdemons.displayitem_spigot.antispam.ItemSpamPreventer;
import com.github.crashdemons.displayitem_spigot.concurrency.ConcurrentPermissions;
import com.github.crashdemons.displayitem_spigot.concurrency.DeferredMessenger;
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

    private final String replacementString;
    private final String cooldownMessage;

    public ChatEventExecutor(ChatListener listener, EventPriority priority) {
        this.listener = listener;
        this.priority = priority;
        int records = DisplayItem.plugin.getConfig().getInt("displayitem.spamdetectionbuffer");
        int threshold = DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold");
        spampreventer = new ItemSpamPreventer(records, threshold);

        this.replacementString = DisplayItem.plugin.getConfig().getString("displayitem.replacement");
        this.cooldownMessage = DisplayItem.plugin.getConfig().getString("displayitem.messages.cooldown");
    }

    @Override
    public void execute(Listener listener, Event originalEvent) throws EventException {
        if (!(originalEvent instanceof AsyncPlayerChatEvent)) {
            return;
        }
        AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) originalEvent;
        onChat(event);
    }

    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        DeferredChatEventParameters deferredParams = new DeferredChatEventParameters(event);//collect thread-safe values from event for processing
        if (event instanceof ReplacedChatEvent) {
            return;
        }
        int start = event.getMessage().indexOf(replacementString);
        if (start == -1) {
            return;
        }
        
        if(!ConcurrentPermissions.getPermissionAsync(player, "displayitem.replace")){
            return;
        }
        if(!ConcurrentPermissions.getPermissionAsync(player, "displayitem.bypasscooldown")){
            if (spampreventer.recordEventParams(deferredParams).isSpam()) {
                String errormessage = cooldownMessage;
                DeferredMessenger.sendAsync(player, ChatColor.translateAlternateColorCodes('&', errormessage));
                return;
            }
        }
        
        event.setCancelled(true);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DisplayItem.plugin, new Runnable() {
            public void run() {
                onChatDeferred(deferredParams);

            }
        }, 1L);
    }
    
    

    public void onChatDeferred(DeferredChatEventParameters params) {
        String message = params.message;
        String format = params.format;
        Player player = params.getPlayer();
        //if(player==null) return;

        boolean color = player.hasPermission("displayitem.colorname");

        SplitChatMessage chatLineSplit = itemreplacer.chatLineInsertItem(player, format, message, color);

        ReplacedChatEvent replacementEvent = new ReplacedChatEvent(params);

        String legacyMessage = "";
        for (BaseComponent component : chatLineSplit.content) {
            legacyMessage += component.toLegacyText();
        }
        //DisplayItem.plugin.getLogger().info("debug: <"+legacyMessage+">");
        replacementEvent.setMessage(legacyMessage);
        replacementEvent.setMessageComponents(chatLineSplit.content);
        Bukkit.getServer().getPluginManager().callEvent(replacementEvent);
        if (replacementEvent.isCancelled()) {
            return;
        }

        for (Player p : replacementEvent.getRecipients()) {
            p.spigot().sendMessage(chatLineSplit.toComponents());
        }
    }

}
