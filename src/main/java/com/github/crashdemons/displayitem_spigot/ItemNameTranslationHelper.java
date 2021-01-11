/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import net.md_5.bungee.chat.TranslationRegistry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ItemNameTranslationHelper {

    private ItemNameTranslationHelper() {
    }

    public static boolean hasClass(String classname) {
        try {
            Class<?> providerClass = Class.forName(classname);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getMaterialName(ItemStack item) {
        return getMaterialName(item.getType());
    }

    public static String getMaterialName(Material mat) {
        if (hasClass("net.md_5.bungee.chat.TranslationRegistry")) {
            try {
                String matName = mat.name().toLowerCase();//NOTE: bukkit name may not always match internal minecraft name!
                String trans = TranslationRegistry.INSTANCE.translate("item.minecraft." + matName);
                return trans;
            } catch (Exception e) {
                return null;
            }
            //BaseComponent[] components = ComponentSerializer.parse("{\"translate\":\"block.minecraft."+matName+"\"}");
            //return BaseComponent.toPlainText(components);
        } else {
            //DisplayItem.plugin.getLogger().warning("BungeeChat TranslationRegistry support missing! please update your server!");
            return null;
        }
    }
}
