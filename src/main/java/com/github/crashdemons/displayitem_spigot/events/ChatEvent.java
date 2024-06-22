/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.events;

import java.util.Set;
import org.bukkit.entity.Player;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public interface ChatEvent {
    public Player getPlayer();
    public String getFormat();
    public String getMessage();
    public Set<Player> getRecipients();
    public boolean isCancelled();
    public void setCancelled(boolean cancel);
    public void setFormat(String format);
    public void setMessage(String message);
    public boolean isAsynchronous();
}
