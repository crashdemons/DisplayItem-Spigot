/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class TextUtils {
    private TextUtils(){}
    public static String toLegacyText(BaseComponent[] components, boolean format){
        String legacyMessage = "";
        for(BaseComponent component : components){ //TODO; consider putting in a nested BaseComponent for conversion.
            legacyMessage+=component.toLegacyText();
        }
        if(!format) return ChatColor.stripColor(legacyMessage);
        return legacyMessage;
    }
}
