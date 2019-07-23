/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.concurrency;

import com.github.crashdemons.displayitem_spigot.DisplayItem;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class DeferredMessenger {
    private DeferredMessenger(){}
    public static void sendAsync(Player p, String message){
        UUID id = p.getUniqueId();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DisplayItem.plugin, new Runnable() {
            public void run() {
                Player player = Bukkit.getPlayer(id);
                if(player!=null)
                    player.sendMessage(message);
            }
        }, 1L);
    }
}
