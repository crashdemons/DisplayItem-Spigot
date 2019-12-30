/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import com.sainttx.util.HoverComponentManager;
import com.sainttx.util.ItemJsonLengthException;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class MessageFormatter{

    private BaseComponent[] formatItemComponents(Player player, ItemStack item, boolean colorize){
        String itemformat = ChatColor.translateAlternateColorCodes('&', DisplayItem.plugin.getConfig().getString("displayitem.itemformat"));
        boolean usebookname = DisplayItem.plugin.getConfig().getBoolean("displayitem.usebooknameformat");
        String bookformat = ChatColor.translateAlternateColorCodes('&', DisplayItem.plugin.getConfig().getString("displayitem.booknameformat"));
        String itemlabel = MacroReplacements.replaceAll(player, itemformat, "", bookformat, usebookname, -1, colorize);
        
        
   
        BaseComponent[] itemComponent;
        
        boolean canDisplayItem = true;
        if(item==null) canDisplayItem=false;
        else if(item.getType()==Material.AIR) canDisplayItem=false;
        
        
        if (canDisplayItem) {
            int jsonLimit =  DisplayItem.plugin.getConfig().getInt("displayitem.jsonlimit");
            try{
                itemComponent = new BaseComponent[]{HoverComponentManager.getTooltipComponent(itemlabel, item, jsonLimit)};
            }catch(ItemJsonLengthException ex){
                DisplayItem.plugin.getLogger().warning(ex.getMessage());
                itemformat = ChatColor.translateAlternateColorCodes('&', DisplayItem.plugin.getConfig().getString("displayitem.itemtoolongformat"));
                itemlabel = MacroReplacements.replaceAll(player, itemformat, "", bookformat, usebookname, -1, colorize);
                itemComponent = TextComponent.fromLegacyText(itemlabel);
            }

        } else {
            itemComponent = TextComponent.fromLegacyText(itemlabel);
        }
        return itemComponent;
    }
    
    public SplitChatMessage messageInsertItem(Player player, String messageText, boolean colorize){
        String metareplacestr = DisplayItem.plugin.getConfig().getString("displayitem.metareplacement");
        String replacestr = DisplayItem.plugin.getConfig().getString("displayitem.replacement");
        SplitChatMessage bukkitTextSplit = SplitChatMessage.fromWithExternalReplacement(messageText, replacestr,   metareplacestr,replacestr);
        
        ItemStack item = player.getInventory().getItemInMainHand();
        BaseComponent[] itemComponent=formatItemComponents(player,item,colorize);
        
        bukkitTextSplit.content = itemComponent;
        
        return bukkitTextSplit;
    }
}
