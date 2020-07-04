/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sainttx.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        System.out.println("DI-HoverManager message-string "+message);//TODO: DEBUG
        BaseComponent[] messageComponents = TextComponent.fromLegacyText(message);
        return getTooltipComponent(messageComponents,item,jsonLengthLimit);
    }
    public static BaseComponent[] getTooltipComponent(BaseComponent[] messageComponents, ItemStack item, int jsonLengthLimit) throws ItemJsonLengthException {
        
        /*System.out.println("DI-HoverManager message-component "+messageComponents.toString());//TODO: DEBUG
        System.out.println("DI-HoverManager message-component-legacy "+messageComponents.toLegacyText());//TODO: DEBUG
        System.out.println("DI-HoverManager message-component-plain "+messageComponents.toPlainText());//TODO: DEBUG*/
        
        String itemJson = ItemConverter.convertItemStackToJson(item);
        if(itemJson.length()>jsonLengthLimit){
            throw new ItemJsonLengthException("Item JSON exceeded plugin limit of "+jsonLengthLimit+" ("+itemJson.length()+")",itemJson.length(),jsonLengthLimit);
        }

        // Prepare a BaseComponent array with the itemJson as a text component
        BaseComponent[] hoverEventComponents = new BaseComponent[]{
                new TextComponent(itemJson) // The only element of the hover events basecomponents is the item json
        };

        // Create the hover event
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

        /* set the hover event to the item event for the text-component that the player will see*/
        for(BaseComponent messageComponent : messageComponents){
            messageComponent.setHoverEvent(event);
        }
        
        return messageComponents;
    }
}
