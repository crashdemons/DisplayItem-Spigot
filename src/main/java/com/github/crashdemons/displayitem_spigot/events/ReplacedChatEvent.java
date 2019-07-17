/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.events;

import java.util.Set;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ReplacedChatEvent extends AsyncPlayerChatEvent{
    
    private BaseComponent[] components = null;
    
    public ReplacedChatEvent(boolean async, Player who, String message, Set<Player> players){
        super(async,who,message,players);
    }
    
    public void setMessageComponents(BaseComponent... components){
        this.components=components;
    }
    
    public BaseComponent[] getMessageComponents(){
        return components;
    }
    
    
    public ReplacedChatEvent(AsyncPlayerChatEvent parentEvent){
        super(parentEvent.isAsynchronous(),parentEvent.getPlayer(), parentEvent.getMessage(), parentEvent.getRecipients());
        this.setFormat(parentEvent.getFormat());
    }
}
