/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.concurrency;

import java.util.Objects;
import org.bukkit.entity.Player;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ConcurrentPermissionQuery{
    private static volatile long num=0;
    public final long id;
    public final ConcurrentPermissionParameters params;
    public ConcurrentPermissionQuery(ConcurrentPermissionParameters params){
        this.params = params;
        synchronized(ConcurrentPermissionQuery.class){
            id=num;
            num++;
        }
    }
    public ConcurrentPermissionQuery(Player p, String perm){
        this(new ConcurrentPermissionParameters(p,perm));
    } 

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 31 * hash + Objects.hashCode(this.params);
        return hash;
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
        final ConcurrentPermissionQuery other = (ConcurrentPermissionQuery) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.params, other.params)) {
            return false;
        }
        return true;
    }
    
    
}
