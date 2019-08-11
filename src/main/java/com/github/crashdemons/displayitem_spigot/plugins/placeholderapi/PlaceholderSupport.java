/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.plugins.placeholderapi;

import com.github.crashdemons.displayitem_spigot.plugins.CompatiblePlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class PlaceholderSupport extends CompatiblePlugin {
    public PlaceholderSupport(Plugin parentPlugin){
        super(parentPlugin,"PlaceholderAPI");
    }
    public String replaceAll(Player player, String message){
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
