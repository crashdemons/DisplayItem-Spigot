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
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class MessageFormatter{

    private static String capFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static String camelCase(String str)
    {
        StringBuilder builder = new StringBuilder(str);
        // Flag to keep track if last visited character is a 
        // white space or not
        boolean isLastSpace = true;

        // Iterate String from beginning to end.
        for(int i = 0; i < builder.length(); i++)
        {
                char ch = builder.charAt(i);

                if(isLastSpace && ch >= 'a' && ch <='z')
                {
                        // Character need to be converted to uppercase
                        builder.setCharAt(i, (char)(ch + ('A' - 'a') ));
                        isLastSpace = false;
                }else if (ch != ' ')
                        isLastSpace = false;
                else
                        isLastSpace = true;
        }

        return builder.toString();
    }
    
    private static String getMaterialTypename(Material mat){
        return camelCase(mat.name().toLowerCase().replaceAll("_", " "));
        
    }
    
    private static String formatBookName(String bookformat, ItemStack is){
        if(!is.getType().equals(Material.WRITTEN_BOOK)) return null;
        ItemMeta meta = is.getItemMeta();
        if(meta instanceof BookMeta){
            BookMeta bookmeta = (BookMeta) meta;
            if(bookmeta.hasTitle() && bookmeta.hasTitle()){
                bookformat = bookformat.replaceAll("%booktitle%", bookmeta.getTitle());
                bookformat = bookformat.replaceAll("%bookauthor%", bookmeta.getAuthor());
            }
        }
        return null;
    }
    
    
    private static String getItemName(ItemStack is, String bookformat, boolean usebookname) {
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
            if (meta.hasLocalizedName()) {
                return meta.getLocalizedName();
            }
            if(usebookname && meta instanceof BookMeta){
                String bookname = formatBookName(bookformat,is);
                if(bookname!=null) return bookname;
            }
        }
        return getMaterialTypename(is.getType());
    }
    
    private String formatItemLabel(String itemformat, String itemname, String amount, String itemtype){
            itemformat = itemformat.replaceAll("%amount%", amount);
            itemformat = itemformat.replaceAll("%item%", itemname);
            itemformat = itemformat.replaceAll("%itemtype%",itemtype);
            return itemformat;
    }
    
    
    private BaseComponent[] formatItemComponents(ItemStack item, boolean colorize){
        String itemformat = ChatColor.translateAlternateColorCodes('&', DisplayItem.plugin.getConfig().getString("displayitem.itemformat"));
        String itemname = "";
        String itemtype = "";
        String amount = "";
        BaseComponent[] itemComponent;
        
        boolean canDisplayItem = true;
        if(item==null) canDisplayItem=false;
        else if(item.getType()==Material.AIR) canDisplayItem=false;
        
        
        if (canDisplayItem) {
            
            boolean usebookname = DisplayItem.plugin.getConfig().getBoolean("displayitem.usebooknameformat");
            String bookformat = DisplayItem.plugin.getConfig().getString("displayitem.booknameformat");
            
            itemname = getItemName(item,bookformat,usebookname);
            if(!colorize) itemname = ChatColor.stripColor(itemname);
            amount=Integer.toString(item.getAmount());
            itemtype = getMaterialTypename(item.getType());
            itemformat = formatItemLabel(itemformat, itemname, amount, itemtype);
            int jsonLimit =  DisplayItem.plugin.getConfig().getInt("displayitem.jsonlimit");
            try{
                itemComponent = new BaseComponent[]{HoverComponentManager.getTooltipComponent(itemformat, item, jsonLimit)};
            }catch(ItemJsonLengthException ex){
                DisplayItem.plugin.getLogger().warning(ex.getMessage());
                itemformat = ChatColor.translateAlternateColorCodes('&', DisplayItem.plugin.getConfig().getString("displayitem.itemtoolongformat"));
                itemformat = formatItemLabel(itemformat, itemname, amount, itemtype);
                itemComponent = TextComponent.fromLegacyText(itemformat);
            }

        } else {
            itemname="Air";
            amount="1";
            itemformat = formatItemLabel(itemformat, itemname, amount, itemtype);
            itemComponent = TextComponent.fromLegacyText(itemformat);
        }
        return itemComponent;
    }
    
    public SplitChatMessage messageInsertItem(Player player, String messageText, boolean colorize){
        String replacestr = DisplayItem.plugin.getConfig().getString("displayitem.replacement");
        SplitChatMessage bukkitTextSplit = SplitChatMessage.from(messageText, replacestr);
        
        ItemStack item = player.getInventory().getItemInMainHand();
        BaseComponent[] itemComponent=formatItemComponents(item,colorize);
        
        bukkitTextSplit.content = itemComponent;
        
        return bukkitTextSplit;
    }
}
