/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sainttx.util;

import static com.sainttx.util.ItemConverter.convertItemStackToJson;
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
    */
    public static void sendItemTooltipMessage(Player player, String message, ItemStack item) {
        String itemJson = convertItemStackToJson(item);

        // Prepare a BaseComponent array with the itemJson as a text component
        BaseComponent[] hoverEventComponents = new BaseComponent[]{
                new TextComponent(itemJson) // The only element of the hover events basecomponents is the item json
        };

        // Create the hover event
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

        /* And now we create the text component (this is the actual text that the player sees)
         * and set it's hover event to the item event */
        TextComponent component = new TextComponent(message);
        component.setHoverEvent(event);

        // Finally, send the message to the player
        player.spigot().sendMessage(component);
    }
    public static TextComponent getTooltipComponent(Player player, String message, ItemStack item) {
        String itemJson = convertItemStackToJson(item);

        // Prepare a BaseComponent array with the itemJson as a text component
        BaseComponent[] hoverEventComponents = new BaseComponent[]{
                new TextComponent(itemJson) // The only element of the hover events basecomponents is the item json
        };

        // Create the hover event
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

        /* And now we create the text component (this is the actual text that the player sees)
         * and set it's hover event to the item event */
        TextComponent component = new TextComponent(message);
        component.setHoverEvent(event);
        return component;
    }
}
