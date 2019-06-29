/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import com.sainttx.util.HoverComponentManager;
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
public class Chat_bukkit{

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

    public BaseComponent[] replaceItem(Player p, String message, String chatformat, boolean color) {
        org.bukkit.inventory.ItemStack is = p.getItemInHand();

        String item = ChatColor.translateAlternateColorCodes('&', DisplayItem.instance.getConfig().getString("displayitem.itemformat"));
        BaseComponent[] itemComponent;
        if (is != null && !is.getType().equals(Material.AIR)) {
            String itemname = getItemName(is);
            if(!color) itemname = ChatColor.stripColor(itemname);
            item = item.replace("%item%", itemname);
            item = item.replace("%amount%", String.valueOf(is.getAmount()));

            itemComponent = new BaseComponent[]{HoverComponentManager.getTooltipComponent(p, item, is)};

        } else {
            item = item.replace("%item%", "Air");
            item = item.replace("%amount%", "1");
            itemComponent = TextComponent.fromLegacyText(item);
        }

        String replacestr = DisplayItem.instance.getConfig().getString("displayitem.replacement");

        int start = message.indexOf(replacestr);
        String foreword = String.format(chatformat, getPlayerName(p), message.substring(0, start));
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
