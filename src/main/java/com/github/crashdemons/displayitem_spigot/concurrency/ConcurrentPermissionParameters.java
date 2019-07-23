/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.concurrency;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ConcurrentPermissionParameters {
    public final UUID playerId;
    public final String permission;
    public ConcurrentPermissionParameters(UUID id, String perm){
        playerId=id;
        permission=perm;
    }
    public ConcurrentPermissionParameters(Player player, String perm){
        playerId=player.getUniqueId();
        permission=perm;
    }
    
    public Player getPlayer(){
        return Bukkit.getPlayer(playerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConcurrentPermissionParameters other = (ConcurrentPermissionParameters) obj;
        if (!Objects.equals(this.permission, other.permission)) {
            return false;
        }
        if (!Objects.equals(this.playerId, other.playerId)) {
            return false;
        }
        return true;
    }
    


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.playerId);
        hash = 89 * hash + Objects.hashCode(this.permission);
        return hash;
    }
    
    
}
