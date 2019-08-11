/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class MacroReplacements {
    public static final String version = "2.3.5";
    private static final String[] supported = new String[]{
        "displayname",
        "username",
        "item",
        "itemdisplayname",
        "itemlocalizedname",
        "itemtype",
        "amount",
        "booktitle",
        "bookauthor",
        "bookpages",
        "message",
        "cooldown",
        "cooldownms"
    };
    private MacroReplacements(){}
    
    
    
    private static class CachedDetails{
        public OfflinePlayer offPlayer = null;
        public Player player = null;
        public ItemStack item=null;
        public ItemMeta meta = null;
        public BookMeta book = null;
        public Player getPlayer(OfflinePlayer offplayer){
            if(offplayer!=null) offPlayer=offplayer;
            if(player==null && offplayer instanceof Player){
//                DisplayItem.plugin.getLogger().info("Player cache hit!");//TXODO: remove debug
                player = (Player) offplayer;
            }
            return player;
        }
        public ItemStack getItem(OfflinePlayer offplayer){
            if(item==null){
                getPlayer(offplayer);
                if(player!=null) item = player.getInventory().getItemInMainHand();
                //if(item==null) item = new ItemStack(Material.AIR,1);
            }
            return item;
        }
        public ItemMeta getMeta(OfflinePlayer offplayer){
            if(meta==null){
                getItem(offplayer);
                if(item!=null && item.hasItemMeta()) meta=item.getItemMeta();
            }
            return meta;
        }
        public BookMeta getBook(OfflinePlayer offplayer){
            if(book==null){
                getMeta(offplayer);
                if(meta instanceof BookMeta) book=(BookMeta) meta;
            }
            return book;
        }
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
        return camelCase(mat.name().toLowerCase().replace("_", " "));
        
    }
    
    private static String getItemName(CachedDetails details, String bookformat, boolean usebookname,boolean colorize) {
        //ItemMeta meta = is.getItemMeta();
        ItemMeta meta = details.meta;
        BookMeta book = details.book;
        if (meta != null) {
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
            if (meta.hasLocalizedName()) {
                return meta.getLocalizedName();
            }
            if(usebookname && book!=null){
                return replaceAllCached(details.offPlayer,bookformat,"","",false,colorize,details);
            }
        }
        return getMaterialTypename(details.item.getType());
    }
    
    public static String replaceAll(OfflinePlayer player, String replaceIn, String messageValue, String bookformat, boolean usebookname,boolean colorize){
        CachedDetails details = new CachedDetails();
        return replaceAllCached(player,  replaceIn, messageValue,  bookformat,  usebookname,colorize, details);
    }
    public static String requestMacroUncached(OfflinePlayer offPlayer, String message, String macroName, String bookformat,boolean colorize, boolean usebookname){
        return requestMacroCached(offPlayer,  message, macroName, bookformat, usebookname, colorize, new CachedDetails());
    }
    
    
    private static String replaceAllCached(OfflinePlayer player, String replaceIn, String messageValue, String bookformat, boolean usebookname,boolean colorize,CachedDetails details){
//        DisplayItem.plugin.getLogger().info("...replaceCached message "+replaceIn);//TXODO: debug line
        if(DisplayItem.plugin.placeholders.isActive()){
//            DisplayItem.plugin.getLogger().info("....applying placeholders "+replaceIn);//TXODO: debug line
            replaceIn = DisplayItem.plugin.placeholders.replaceAll(player, replaceIn);
        }
        for(String macroName : supported){
            String macro = "%"+macroName+"%";
            if(!replaceIn.contains(macro)) continue;
            
            String replacement=requestMacroCached(player, messageValue, macroName, bookformat, usebookname, colorize, details);
            if(replacement==null){
                DisplayItem.plugin.getLogger().severe("Attempted to automatically replace unsupported macro: "+macroName);
                continue;
            }
            replaceIn = replaceIn.replace(macro, replacement);
        }
        for(String macroName : supported){
            macroName+="NF";
            String macro = "%"+macroName+"%";
//            DisplayItem.plugin.getLogger().info("....checking macro "+macro + " vs "+replaceIn);//TXODO: debug line
            if(!replaceIn.contains(macro)) continue;
            
            String replacement=requestMacroCached(player, messageValue, macroName, bookformat, usebookname, false, details);
            if(replacement==null){
                DisplayItem.plugin.getLogger().severe("Attempted to automatically replace unsupported macro: "+macroName);
                continue;
            }
            replaceIn = replaceIn.replace(macro, replacement);
        }
//        DisplayItem.plugin.getLogger().info("~~~replaceCached message "+replaceIn);//TXODO: debug line
        return replaceIn;
    }
    

    
    private static String requestMacroCached(OfflinePlayer offPlayer, String message, String macroName, String bookformat, boolean usebookname,boolean colorize,CachedDetails details){
//        DisplayItem.plugin.getLogger().info("....reqMacroCached "+macroName+", message "+message);//TXODO: debug line
        boolean stripColors = macroName.toUpperCase().endsWith("NF");
        if(stripColors) macroName = macroName.substring(0, macroName.length() - 2);//remove "NF"
        if(macroName.contains("item") && !colorize) stripColors=true;
        if(stripColors){
            return requestMacroCachedStripped(offPlayer, message, macroName, bookformat, usebookname, colorize, details);
        }else{
            return requestMacroCachedRaw(offPlayer, message, macroName, bookformat, usebookname, colorize, details);
        }
    }
  
    private static String requestMacroCachedStripped(OfflinePlayer offPlayer, String message, String macroName, String bookformat, boolean usebookname,boolean colorize, CachedDetails details){
//        DisplayItem.plugin.getLogger().info(".....reqMacroStripped "+macroName+", message "+message);//TXODO: debug line
        String replacement = requestMacroCachedRaw(offPlayer, message, macroName, bookformat, usebookname, colorize, details);
        String stripped = ChatColor.stripColor(replacement);
        DisplayItem.plugin.getLogger().info(".....stripped "+stripped+" from "+replacement);
        return stripped;
    }
    
    private static String requestMacroCachedRaw(OfflinePlayer offPlayer, String message, String macroName, String bookformat, boolean usebookname,boolean colorize, CachedDetails details){
//        DisplayItem.plugin.getLogger().info("......reqMacroRaw "+macroName+", message "+message);//TXODO: debug line
        Player player = details.getPlayer(offPlayer);
        ItemStack item = details.getItem(offPlayer);
        ItemMeta meta = details.getMeta(offPlayer);
        BookMeta book = details.getBook(offPlayer);
        switch(macroName){
            case "displayname":
                if(player!=null) return player.getDisplayName();
                break;
            case "username":
                if(player!=null) return player.getName();
                break;
            case "item":
                if(item!=null) return getItemName(details, bookformat, usebookname, colorize);
                else return "Air";
                //break;
            case "itemdisplayname":
                if(meta!=null) return meta.getDisplayName();
                else if(item==null) return "Air";
                break;
            case "itemlocalizedname":
                if(meta!=null) return meta.getLocalizedName();
                else if(item==null) return "Air";
                break;
            case "itemtype":
                if(item!=null) return getMaterialTypename(item.getType());
                break;
            case "amount":
                if(item!=null){
                    int amount=item.getAmount();
                    if(amount==0) return "1";
                    return ""+amount;
                }
                else return "1";
            case "booktitle":
                if(book!=null && book.hasTitle()) return book.getTitle();
                break;
            case "bookauthor":
                if(book!=null && book.hasAuthor()) return book.getAuthor();
                break;
            case "bookpages":
                if(book!=null && book.hasPages()) return ""+book.getPages().size();
                break;
            case "message":
                return message;
            case "cooldown":
                double seconds = DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold")/1000.0;
                double roundOff = (double) Math.round(seconds * 100) / 100;
                return ""+roundOff;
            case "cooldownms":
                return ""+DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold");
            default:
                return null;
            
        }
        return "";
    }
    
 
}
