/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.events;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class AsyncPlayerChatEventAdapter implements ChatEvent {
    AsyncPlayerChatEvent event;
    public AsyncPlayerChatEventAdapter(AsyncPlayerChatEvent event){
        this.event=event;
    }
    
    public Player getPlayer(){
        return event.getPlayer();
    }
    public String getFormat(){
        return event.getFormat();
    }
    public String getMessage(){
        return event.getMessage();
    }
    public Set<Player> getRecipients(){
        return event.getRecipients();
    }
    public boolean isCancelled(){
        return event.isCancelled();
    }
    public void setCancelled​(boolean cancel){
        event.setCancelled(cancel);
    }
    public void setFormat​(String format){
        event.setFormat(format);
    }
    public void setMessage​(String message){
        event.setMessage(message);
    }
    public boolean isAsynchronous(){//probably yes...
        return event.isAsynchronous();
    }
}
