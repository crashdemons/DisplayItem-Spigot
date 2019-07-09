/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import com.sainttx.util.HoverComponentManager;
import com.sainttx.util.ItemJsonLengthException;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ChatFormatter{

    private static String capFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static String getItemName(ItemStack is) {
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
            if (meta.hasLocalizedName()) {
                return meta.getLocalizedName();
            }
        }
        Material mat = is.getType();
        String matname = mat.name().toLowerCase().replace('_', ' ');
        return capFirst(matname);
    }

    private static String getPlayerName(Player p) {
        String name = p.getName();
        String custname = p.getDisplayName();
        if (custname != null) {
            name = custname;
        }
        return name;
    }
    
    private String formatItem(String itemformat, String itemname, String amount){
            itemformat = itemformat.replaceAll("%amount%", amount);
            itemformat = itemformat.replaceAll("%item%", itemname);
            return itemformat;
    }

    public BaseComponent[] chatInsertItem(String message, String chatformat, Player player, boolean colorize) {
        ItemStack item = player.getInventory().getItemInMainHand();
        
        int jsonLimit =  DisplayItem.plugin.getConfig().getInt("displayitem.jsonlimit");
        String itemformat = ChatColor.translateAlternateColorCodes('&', DisplayItem.plugin.getConfig().getString("displayitem.itemformat"));
        String itemname = "";
        String amount = "";
        BaseComponent[] itemComponent;
        
        boolean canDisplayItem = true;
        if(item==null) canDisplayItem=false;
        else if(item.getType()==Material.AIR) canDisplayItem=false;
        
        if (canDisplayItem) {
            itemname = getItemName(item);
            if(!colorize) itemname = ChatColor.stripColor(itemname);
            amount=Integer.toString(item.getAmount());
            itemformat = formatItem(itemformat, itemname, amount);
            try{
                itemComponent = new BaseComponent[]{HoverComponentManager.getTooltipComponent(player, itemformat, item, jsonLimit)};
            }catch(ItemJsonLengthException ex){
                DisplayItem.plugin.getLogger().warning(ex.getMessage());
                itemformat = ChatColor.translateAlternateColorCodes('&', DisplayItem.plugin.getConfig().getString("displayitem.itemtoolongformat"));
                itemformat = formatItem(itemformat, itemname, amount);
                itemComponent = TextComponent.fromLegacyText(itemformat);
            }

        } else {
            itemname="Air";
            amount="1";
            itemformat = formatItem(itemformat, itemname, amount);
            itemComponent = TextComponent.fromLegacyText(itemformat);
        }

        String replacestr = DisplayItem.plugin.getConfig().getString("displayitem.replacement");

        int start = message.indexOf(replacestr);
        String foreword = String.format(chatformat, getPlayerName(player), message.substring(0, start));
        String postword = "";
        try {
            postword = message.substring(start + replacestr.length());
        } catch (Exception e) {
            //leave string blank
        }
        BaseComponent[] foreText = TextComponent.fromLegacyText(foreword);
        BaseComponent[] postText = TextComponent.fromLegacyText(postword);

        return new ComponentBuilder("").append(foreText).append(itemComponent).append(postText).create();
    }
}
