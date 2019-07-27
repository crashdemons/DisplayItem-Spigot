/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ChatLineFormatter extends MessageFormatter{

    private static final String MESSAGE_REPLACEMENT_PLACEHOLDER = "[[DI-REPL-MARKER]]";

    private static String getPlayerName(Player p) {
        String name = p.getName();
        String custname = p.getDisplayName();
        if (custname != null) {
            name = custname;
        }
        return name;
    }
    
    private String getChatLineFormat(String bukkitChatFormat){
        String chatLineFormat;
        boolean overrideChatLineFormat = DisplayItem.plugin.getConfig().getBoolean("displayitem.overridechatformat");
        if(overrideChatLineFormat){
            chatLineFormat = DisplayItem.plugin.getConfig().getString("displayitem.format");
        }else{
            chatLineFormat = String.format(bukkitChatFormat,"%displayname%", "%message%");//convert bukkit format string to DI format string;
        }
        return chatLineFormat;
    }
    
    private String formatChatLine(String chatLineFormat, String displayname, String message){
            chatLineFormat = chatLineFormat.replaceAll("%displayname%", displayname);
            chatLineFormat = chatLineFormat.replaceAll("%message%", message);
            return chatLineFormat;
    }
    
    
    public SplitChatMessage chatLineInsertItem(Player player, String bukkitChatFormat, String bukkitMessageText, boolean colorize) {
        BaseComponent[] messageInserted = messageInsertItem(player, bukkitMessageText, colorize).toComponents();
        
        String chatLineFormat = ChatColor.translateAlternateColorCodes('&',getChatLineFormat(bukkitChatFormat));
        
        String displayname = getPlayerName(player);
        //use a temporary placeholder so that we can tell if the chat format added text before and/or after the message, not just after!
        String chatLineX = formatChatLine(chatLineFormat, displayname, MESSAGE_REPLACEMENT_PLACEHOLDER);
        //split the message into before, message, after parts.
        SplitChatMessage chatLineSplit = SplitChatMessage.from(chatLineX, MESSAGE_REPLACEMENT_PLACEHOLDER);
        
        chatLineSplit.content = messageInserted;
        
        return chatLineSplit;
    }
}
