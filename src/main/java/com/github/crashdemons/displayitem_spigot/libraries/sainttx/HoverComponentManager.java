/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.libraries.sainttx;

import com.github.crashdemons.displayitem_spigot.libraries.bhupesh.JsonHelper;
import com.github.crashdemons.displayitem_spigot.libraries.crashdemons.ItemJsonLengthException;
import com.github.crashdemons.displayitem_spigot.libraries.ostlerdev.ComponentsShowItem;
import com.google.gson.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 *
 * @author sainttx
 */
public class HoverComponentManager {
    private HoverComponentManager(){}
    //https://www.spigotmc.org/threads/tut-item-tooltips-with-the-chatcomponent-api.65964/

    /**
    * Sends a message to a player with an item as it's tooltip
    *
    * @param player      the player
    * @param message  the message to send
    * @param item        the item to display in the tooltip
    * @param jsonLengthLimit upper limit on the json conversion of the item
    * @throws ItemJsonLengthException if the limit is exceeded
    */
    public static void sendItemTooltipMessage(Player player, String message, ItemStack item, int jsonLengthLimit) throws ItemJsonLengthException {
        // send the message to the player
        player.spigot().sendMessage(getTooltipComponent(message,item,jsonLengthLimit));
    }
    public static BaseComponent[] getTooltipComponent(String message, ItemStack item, int jsonLengthLimit) throws ItemJsonLengthException {
        /* And now we create the text component (this is the actual text that the player sees)
         * and set it's hover event to the item event */

        BaseComponent messageContainer = new TextComponent();

        BaseComponent[] messageComponents = TextComponent.fromLegacyText(message);

        for(BaseComponent messageComponent : messageComponents){
            messageContainer.addExtra(messageComponent);
        }

        return getTooltipComponent(messageContainer,item,jsonLengthLimit);
    }

    public static String fixNBTJson(String nbt){
        System.out.println("before: "+nbt);
        String nbt2 = nbt.replaceAll("(1)[bB]([^a-zA-Z0-9\"'])","true$2");
        nbt2 = nbt2.replaceAll("(0)[bB]([^a-zA-Z0-9\"'])","false$2");
        nbt2 = nbt2.replaceAll("([0-9]+)[bBsSlLfFdD]([^a-zA-Z0-9\"'])","$1$2");

        //[I;1,2,3]
        nbt2 = nbt2.replaceAll("\\[[BILD];([0-9])", "[$1");

        System.out.println("2: "+nbt2);

        Gson gson = JsonHelper.getMCJSONParser();
        JsonElement components = gson.fromJson(nbt2, JsonObject.class);//JsonParser.parseString(nbt);
        nbt2 = components.toString();
        System.out.println("3: "+nbt2);

        nbt2 = nbt2.replaceAll("\"(bold|italic|underlined|strikethrough|obfuscated|minecraft:enchantment_glint_override)\":1([^0-9\"'])","\"$1\":true$2");
        nbt2 = nbt2.replaceAll("\"(bold|italic|underlined|strikethrough|obfuscated|minecraft:enchantment_glint_override)\":0([^0-9\"'])","\"$1\":false$2");
        nbt2 = nbt2.replaceAll("\"(is|has)_([a-zA-Z_-]+)\":1([^0-9\"'])","\"$1_$2\":true$3");
        nbt2 = nbt2.replaceAll("\"(is|has)_([a-zA-Z_-]+)\":0([^0-9\"'])","\"$1_$2\":false$3");
        System.out.println("4: "+nbt2);
        return nbt2;
    }

    public static BaseComponent[] getTooltipComponent(BaseComponent messageComponent, ItemStack item, int jsonLengthLimit) throws ItemJsonLengthException {

        ItemMeta meta = item.getItemMeta();
        //if(meta==null) meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        //HoverEvent event1 = getHoverEvent(item, jsonLengthLimit, meta);

        String nbt = meta == null ? null : meta.getAsString();
        //System.out.println("DEBUG-DI: NBT "+nbt);
        //ItemTag bcTag = ItemTag.ofNbt(nbt);
        //System.out.println("DEBUG-DI: TAG "+bcTag.toString());
        //Item bcItem = new Item(item.getType().getKey().toString(), item.getAmount(),bcTag);

        String vanillaItemId=item.getType().getKey().toString();
        int count = item.getAmount();
        String itemJson="{\"id\":\""+vanillaItemId+"\",\"count\":"+count+",\"components\":"+nbt+"}";

        if(nbt!=null && itemJson.length()> jsonLengthLimit){
            throw new ItemJsonLengthException("Item JSON exceeded plugin limit of "+ jsonLengthLimit +" ("+itemJson.length()+")",itemJson.length(), jsonLengthLimit);
        }

        //2025: Trust bungeechat to serialize this properly now?? maybe??  (old note: when 1.21-R0.1 came out, it was improperly serializing components as "tag" property)
        ItemTag itemTag = ItemTag.ofNbt(nbt);
        //Item hoverItem = new Item(vanillaItemId, item.getAmount(), itemTag);
        //NOTE hover component "Item" STILL serializes "tag" instead of "components" as of 2026-02-28 in 1.21-R0.5 and this is the version included in spigot-1.21.11-R0.2

        //construct a replacement hover item object with components as a json element.

        Gson gson = JsonHelper.getMCJSONParser();
        String nbt2 = fixNBTJson(nbt);

        JsonObject components = gson.fromJson(nbt2, JsonObject.class);//JsonParser.parseString(nbt);

        //ensure PDC data not sent
        if(components.has("minecraft:custom_data")){
            components.remove("minecraft:custom_data");
        }
        if(components.has("minecraft:profile")){
            components.remove("minecraft:profile");
        }

        Bukkit.getLogger().info(components.toString());

        ComponentsShowItem hoverItem2 = new ComponentsShowItem(vanillaItemId, item.getAmount(), components);// components);
        //Bukkit.getLogger().info(hoverItem2.toString());

        /*Map<String, Object> fields = item.copy();
        Object metaField = fields.get("meta");
        Bukkit.getLogger().info(" item serialize "+metaField);*/

        // Create the hover event
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverItem2);

        // set the hover event to the item event for the text-component that the player will see
        messageComponent.setHoverEvent(event);



        return new BaseComponent[]{messageComponent};
    }


    //this should be the proper method of doing this, but it doesn't serialize properly on 1.21 yet (as of 2024-06-22)
    @NotNull
    private static HoverEvent getHoverEvent(ItemStack item, int jsonLengthLimit, ItemMeta meta) throws ItemJsonLengthException {
        String nbt = meta == null ? null : meta.getAsString();
        //String comp = meta.getAsComponentString();
        System.out.println("DEBUG-DI: NBT "+nbt);
        //System.out.println("DEBUG-DI: COMP "+comp);
        if(nbt!=null && nbt.length()> jsonLengthLimit){
            throw new ItemJsonLengthException("Item NBT exceeded plugin limit of "+ jsonLengthLimit +" ("+nbt.length()+")",nbt.length(), jsonLengthLimit);
        }

        ItemTag tag = ItemTag.ofNbt(nbt);
        System.out.println("DEBUG-DI: TAG "+tag.toString());
        Item itm = new Item(item.getType().getKey().toString(), item.getAmount(),tag);

        //ItemSerializer ser = new ItemSerializer();
        ///ser.serialize(


        return new HoverEvent(HoverEvent.Action.SHOW_ITEM, itm);
    }
}
