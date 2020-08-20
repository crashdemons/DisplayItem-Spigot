/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class MacroReplacements {

    //public static final String version = "2.4.0";
    private static final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    // Define the maximum number of decimals (number of symbols #)
    private static final DecimalFormat df = new DecimalFormat("#.##", otherSymbols);
    private static final DecimalFormat dfS = new DecimalFormat("#", otherSymbols);

    private static final String[] supported = new String[]{
        "displayname",
        "username",
        "item",
        "itemdisplayname",
        "itemlocalizedname",
        "itemtype",
        "amount",
        "booktitle",
        "booktitleT",
        "booktitleU",
        "bookauthor",
        "bookpages",
        "message",
        "cooldown",
        "cooldownS",
        "cooldownms",
        "cooldownremainder",
        "cooldownremainderS",
        "cooldownremainderms",
        "amountE",
        "amountX",
        "Xamount",
        
        "repaircost",
        "repaircostE",
        "Rrepaircost",
        
        "replacement",
        "metareplacement",
    
    };
    private static final String[] paddingSuffixes = new String[]{
        "",
        "PL",
        "PR",};
    private static final String[] formatSuffixes = new String[]{
        "",
        "NF",};

    private MacroReplacements() {
    }

    private static class CachedDetails {

        public OfflinePlayer offPlayer = null;
        public Player player = null;
        public ItemStack item = null;
        public ItemMeta meta = null;
        public BookMeta book = null;

        public Player getPlayer(OfflinePlayer offplayer) {
            if (offplayer != null) {
                offPlayer = offplayer;
            }
            if (player == null && offplayer instanceof Player) {
//                DisplayItem.plugin.getLogger().info("Player cache hit!");//TXODO: remove debug
                player = (Player) offplayer;
            }
            return player;
        }

        public ItemStack getItem(OfflinePlayer offplayer) {
            if (item == null) {
                getPlayer(offplayer);
                if (player != null) {
                    item = player.getInventory().getItemInMainHand();
                }
                //if(item==null) item = new ItemStack(Material.AIR,1);
            }
            return item;
        }

        public ItemMeta getMeta(OfflinePlayer offplayer) {
            if (meta == null) {
                getItem(offplayer);
                if (item != null && item.hasItemMeta()) {
                    meta = item.getItemMeta();
                }
            }
            return meta;
        }

        public BookMeta getBook(OfflinePlayer offplayer) {
            if (book == null) {
                getMeta(offplayer);
                if (meta instanceof BookMeta) {
                    book = (BookMeta) meta;
                }
            }
            return book;
        }
    }
    
    
    private static int getRepairCost(ItemMeta meta){
        if(meta==null){ return -1; }
        if(!(meta instanceof Repairable)) { return -1; }
        Repairable rep = (Repairable) meta;
        if(!rep.hasRepairCost()) { return 0; }
        return rep.getRepairCost();
    }

    private static class MacroParameters {

        OfflinePlayer offPlayer;
        String message;
        String bookformat;
        boolean usebookname;
        long cooldownremainder;
        boolean colorize;
        CachedDetails details;

        public MacroParameters(OfflinePlayer offPlayer, String message, String bookformat, boolean usebookname, long cooldownremainder, boolean colorize, CachedDetails details) {
            this.offPlayer = offPlayer;
            this.message = message;
            this.bookformat = bookformat;
            this.usebookname = usebookname;
            this.colorize = colorize;
            this.details = details;
            this.cooldownremainder=cooldownremainder;
        }
    }
    /*
    private static String strDebug(String str){
        String out = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            out += (int) c;
            out += ".";
        }
        return out;
    }*/

    private static String camelCase(String str) {
        StringBuilder builder = new StringBuilder(str);
        // Flag to keep track if last visited character is a 
        // white space or not
        boolean isLastSpace = true;

        // Iterate String from beginning to end.
        for (int i = 0; i < builder.length(); i++) {
            char ch = builder.charAt(i);

            if (isLastSpace && ch >= 'a' && ch <= 'z') {
                // Character need to be converted to uppercase
                builder.setCharAt(i, (char) (ch + ('A' - 'a')));
                isLastSpace = false;
            } else if (ch != ' ') {
                isLastSpace = false;
            } else {
                isLastSpace = true;
            }
        }

        return builder.toString();
    }

    private static String getMaterialTypename(Material mat) {
        return camelCase(mat.name().toLowerCase().replace("_", " "));

    }

    private static String getItemName(CachedDetails details, String bookformat, boolean usebookname, boolean colorize) {
        ItemMeta meta = details.meta;
        BookMeta book = details.book;
        if (meta != null) {
            if (meta.hasDisplayName()) {
                

                //BaseComponent[] components = TextComponent.fromLegacyText(meta.getDisplayName());
                //String reformatted = TextUtils.toLegacyText(components, true);
                
                return meta.getDisplayName();
            }
            if (meta.hasLocalizedName()) {
                return meta.getLocalizedName();
            }
            if (usebookname && book != null) {
                String bookname = replaceAllCached(details.offPlayer, bookformat, "", "", false,  -1, colorize, details);
                //DisplayItem.plugin.getLogger().info("...getItemName usebookname: bookname="+bookname+"|"+strDebug(bookname)+" len="+bookname.length()+" bookformat="+bookformat);//TXODO: debug line
                if (!ChatColor.stripColor(bookname).isEmpty()) {
                    return bookname;
                }
            }
        }
        return getMaterialTypename(details.item.getType());
    }

    public static String replaceAll(OfflinePlayer player, String replaceIn, String messageValue, String bookformat, boolean usebookname, long cooldownremainder, boolean colorize) {
        CachedDetails details = new CachedDetails();
        return replaceAllCached(player, replaceIn, messageValue, bookformat, usebookname, cooldownremainder, colorize, details);
    }

    public static String requestMacroUncached(OfflinePlayer offPlayer, String message, String macroName, String bookformat, boolean usebookname, long cooldownremainder, boolean colorize) {
        MacroParameters params = new MacroParameters(offPlayer, message, bookformat, usebookname, cooldownremainder, colorize, new CachedDetails());
        return requestMacroCached(macroName, "", "", params);
    }

    private static boolean containsIgnoreCase(String haystack, String needle){
        return Pattern.compile(Pattern.quote(needle), Pattern.CASE_INSENSITIVE).matcher(haystack).find();
    }
    private static String replaceIgnoreCase(String haystack, String needle, String needlereplacement){
        return haystack.replaceAll("(?i)"+Pattern.quote(needle), needlereplacement);
    }
    
    
    private static String replaceAllCached(OfflinePlayer player, String replaceIn, String messageValue, String bookformat, boolean usebookname, long cooldownremainder, boolean colorize, CachedDetails details) {
//        DisplayItem.plugin.getLogger().info("...replaceCached replace "+replaceIn+" message="+messageValue);//TXODO: debug line
        if (DisplayItem.plugin.placeholders.isActive()) {
//            DisplayItem.plugin.getLogger().info("....applying placeholders "+replaceIn);//TXODO: debug line
            replaceIn = DisplayItem.plugin.placeholders.replaceAll(player, replaceIn);
        }

        MacroParameters params = new MacroParameters(player, messageValue, bookformat, usebookname, cooldownremainder, colorize, new CachedDetails());
        for (String formatSuffix : formatSuffixes) {
            for (String paddingSuffix : paddingSuffixes) {
                for (String macroName : supported) {
                    String macro = "%" + macroName + paddingSuffix + formatSuffix + "%";
                    if (!containsIgnoreCase(replaceIn,macro)) { // !replaceIn.contains(macro)) {
                        continue;
                    }

                    String replacement = requestMacroCached(macroName, paddingSuffix, formatSuffix, params);
                    if (replacement == null) {
                        //                       DisplayItem.plugin.getLogger().severe("Attempted to automatically replace unsupported macro: "+macroName);
                        continue;
                    }
                    replaceIn = replaceIgnoreCase(replaceIn, macro, replacement);//replaceIn.replace(macro, replacement);
                }
            }
        }
//        DisplayItem.plugin.getLogger().info("~~~replaceCached message "+replaceIn);//TXODO: debug line
        return replaceIn;
    }

    private static String requestMacroCached(String macroName, String paddingSuffix, String formatSuffix, MacroParameters params) {
        //       DisplayItem.plugin.getLogger().info("....reqMacroCached "+macroName+", message="+params.message);//TXODO: debug line
        boolean stripColors = formatSuffix.equals("NF");
        if (macroName.contains("item") && !params.colorize) {
            stripColors = true;
        }
        String replacement = requestMacroCachedPadded(macroName, paddingSuffix, formatSuffix, params);
        if (stripColors) {
            return ChatColor.stripColor(replacement);
        }
        return replacement;
    }

    private static String requestMacroCachedPadded(String macroName, String paddingSuffix, String formatSuffix, MacroParameters params) {
//        DisplayItem.plugin.getLogger().info(" fmt "+paddingSuffix+" from "+macroName);
        String resultBase = requestMacroCachedRaw(macroName, params);

//        DisplayItem.plugin.getLogger().info(" fmt  proc "+paddingSuffix+" from "+macroName+" to "+resultBase);
        switch (paddingSuffix) {
            case "PL":
                if (resultBase.isEmpty()) {
                    return resultBase;
                }
                return " " + resultBase;
            case "PR":
                if (resultBase.isEmpty()) {
                    return resultBase;
                }
                return resultBase + " ";
            case "":
                return resultBase;
        }

        return "";
    }

    private static String getBookTitle(BookMeta book, String defaultTitle) {
        String title = "";
        
        if (book != null && book.hasDisplayName()) return book.getDisplayName();
        
        if (book != null && book.hasTitle()) {
            title = book.getTitle();
        }
        if (title == null || title.isEmpty()) {
            title = defaultTitle;
        }
        return title;
    }

    private static String requestMacroCachedRaw(String macroName, MacroParameters params) {
//        DisplayItem.plugin.getLogger().info("......reqMacroRaw "+macroName+", message="+params.message);//TXODO: debug line
        Player player = params.details.getPlayer(params.offPlayer);
        ItemStack item = params.details.getItem(params.offPlayer);
        ItemMeta meta = params.details.getMeta(params.offPlayer);
        BookMeta book = params.details.getBook(params.offPlayer);
        switch (macroName) {
            case "displayname":
//                DisplayItem.plugin.getLogger().info("....... player="+(player!=null));
                //               DisplayItem.plugin.getLogger().info("....... player="+(player.getDisplayName()));
                if (player != null) {
                    return player.getDisplayName();
                }
                break;
            case "username":
                if (player != null) {
                    return player.getName();
                }
                break;
            case "item":
                if (item != null) {
                    return getItemName(params.details, params.bookformat, params.usebookname, params.colorize);
                } else {
                    return "Air";
                }
            //break;
            case "itemdisplayname":
                if (meta != null) {
                    if (!meta.hasDisplayName()) {
                        return "";
                    }
                    return meta.getDisplayName();
                } else if (item == null) {
                    return "Air";
                }
                break;
            case "itemlocalizedname":
                if (meta != null) {
                    if (!meta.hasLocalizedName()) {
                        return "";
                    }
                    return meta.getLocalizedName();
                } else if (item == null) {
                    return "Air";
                }
                break;
            case "itemtype":
                if (item != null) {
                    return getMaterialTypename(item.getType());
                }
                break;
            case "amount":
                if (item != null) {
                    int amount = item.getAmount();
                    if (amount == 0) {
                        return "1";
                    }
                    return "" + amount;
                } else {
                    return "1";
                }
            case "amountE":
                if (item != null) {
                    int amount = item.getAmount();
                    if (amount <= 1) {
                        return "";
                    }
                    return "" + amount;
                } else {
                    return "";
                }
            case "amountX":
                if (item != null) {
                    int amount = item.getAmount();
                    if (amount <= 1) {
                        return "";
                    }
                    return amount + "x";
                } else {
                    return "";
                }
            case "Xamount":
                if (item != null) {
                    int amount = item.getAmount();
                    if (amount <= 1) {
                        return "";
                    }
                    return "x" + amount;
                } else {
                    return "";
                }
            case "booktitle":
                return getBookTitle(book, "");
            case "booktitleT":
                return getBookTitle(book, getMaterialTypename(item.getType()));
            case "booktitleU":
                return getBookTitle(book, "Untitled");
            case "bookauthor":
                if (book != null && book.hasAuthor()) {
                    return book.getAuthor();
                }
                break;
            case "bookpages":
                if (book != null && book.hasPages()) {
                    return "" + book.getPages().size();
                }
                break;
            case "message":
//                DisplayItem.plugin.getLogger().info("....... message="+(params.message));
                return params.message;
            case "cooldown":
                double seconds = DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold") / 1000.0;
                return df.format(seconds);
            case "cooldownS":
                double secondsS = Math.ceil( DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold") / 1000.0 );
                return dfS.format(secondsS);
            case "cooldownms":
                return "" + DisplayItem.plugin.getConfig().getInt("displayitem.spamthreshold");
            case "cooldownremainder":
                if(params.cooldownremainder==-1) return "unknown";
                double cseconds = params.cooldownremainder / 1000.0;
                return df.format(cseconds);
            case "cooldownremainderS":
                if(params.cooldownremainder==-1) return "unknown";
                double csecondsS = Math.ceil( params.cooldownremainder / 1000.0 );
                return dfS.format(csecondsS);
            case "cooldownremainderms":
                if(params.cooldownremainder==-1) return "unknown";
                return ""+params.cooldownremainder;
            case "repaircost":
                if(meta==null) return "0";
                int rc = getRepairCost(meta);
                if(rc<0) return "0";
                return ""+rc;
            case "repaircostE":
                if(meta==null) return "";
                int rce = getRepairCost(meta);
                if(rce<0) return "";
                return ""+rce;
            case "Rrepaircost":
                if(meta==null) return "";
                int rcr = getRepairCost(meta);
                if(rcr<0) return "";
                return "RC:"+rcr;
            case "replacement":
                return DisplayItem.plugin.getConfig().getString("displayitem.replacement");
            case "metareplacement":
                return DisplayItem.plugin.getConfig().getString("displayitem.metareplacement");
            default:
                return null;
        }

        return "";
    }

}
