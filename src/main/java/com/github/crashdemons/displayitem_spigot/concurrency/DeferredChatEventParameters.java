/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.concurrency;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class DeferredChatEventParameters {
    public final UUID playerId;
    public final String message;
    public final String format;
    
    public final Set<UUID> recipients = new HashSet<>();
    
    public DeferredChatEventParameters(AsyncPlayerChatEvent event){
        playerId = event.getPlayer().getUniqueId();
        message=event.getMessage();
        format=event.getFormat();
        
        for(Player p : event.getRecipients()){
            recipients.add(p.getUniqueId());
        }
    }
    
    public Player getPlayer(){
        return Bukkit.getPlayer(playerId);
    }
    public Set<Player> getRecipients(){
        Set<Player> results = new HashSet<>();
        
        for(UUID id : recipients){
            results.add(Bukkit.getPlayer(id));
        }
        return results;
    }
}
