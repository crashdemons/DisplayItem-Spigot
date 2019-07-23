/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.concurrency;

import com.github.crashdemons.displayitem_spigot.DisplayItem;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ConcurrentPermissions {
    private static final ConcurrentHashMap<ConcurrentPermissionQuery,Boolean> permissions = new ConcurrentHashMap<>();
    
    private ConcurrentPermissions(){}
    
    private static Boolean getPermissionSync(ConcurrentPermissionParameters params){
        Player p = params.getPlayer();
        if(p==null) return false;
        return p.hasPermission(params.permission);
    }
    private static synchronized void resolvePermissionSync(ConcurrentPermissionQuery query){
        Boolean result = getPermissionSync(query.params);
        permissions.put(query, result);
    }
    private static void queryPermissionAsync(ConcurrentPermissionQuery query){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DisplayItem.plugin, new Runnable() {
            public void run() {
                resolvePermissionSync(query);
            }
        }, 1L);
    }
    
    public static boolean getPermissionAsync(Player p, String perm){
        ConcurrentPermissionQuery query = new ConcurrentPermissionQuery(p,perm);
        Boolean result = permissions.get(query);
        if(result==null) queryPermissionAsync(query);
        result = awaitPermission(query);
        return result;
    }
    
    private static boolean awaitPermission(ConcurrentPermissionQuery query){
        Boolean result = null;
        do{
            result=permissions.get(query);
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
                //Logger.getLogger(ConcurrentPermissions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while(result==null);
        permissions.remove(query);
        return result;
    }
   
    
    
    
}
