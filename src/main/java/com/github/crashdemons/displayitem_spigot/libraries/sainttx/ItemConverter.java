/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.libraries.sainttx;

import java.lang.reflect.Method;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author sainttx
 */
public class ItemConverter {
    private ItemConverter(){}
    //https://www.spigotmc.org/threads/tut-item-tooltips-with-the-chatcomponent-api.65964/
    
    private static Class<?> nmsItemClass = null; //cached item class
    private static Class<?> nmsTagClass = null; //cached tag class
    
    private static Class<?> findNmsItemClass(){
        if(nmsItemClass!=null) return nmsItemClass;
        Class<?> nmsItemStackClazz = ReflectionUtil.getNmsVersionedClass("ItemStack");//1.16.5 and lower:  net.minecraft.server.[version].ItemStack
        if(nmsItemStackClazz==null) nmsItemStackClazz = ReflectionUtil.getNmClass("world.item","ItemStack");//1.17 first spigot release: net.minecraft.world.item.ItemStack (not versioned)
        if(nmsItemStackClazz==null) nmsItemStackClazz = ReflectionUtil.getNmClass("server","ItemStack"); //possible first form without version
        if(nmsItemStackClazz==null) nmsItemStackClazz = ReflectionUtil.getNmVersionedClass("world.item","ItemStack"); //possible second form with version
        nmsItemClass = nmsItemStackClazz;
        return nmsItemClass;
    }
    private static Class<?> findNmsTagClass(){
        if(nmsTagClass!=null) return nmsTagClass;
        Class<?> nbtTagCompoundClazz = ReflectionUtil.getNmsVersionedClass("NBTTagCompound");//1.16.5 and lower: net minecraft.server.[version].NBTTagCompound
        if(nbtTagCompoundClazz==null) nbtTagCompoundClazz = ReflectionUtil.getNmClass("nbt","NBTTagCompound"); //1.17 first spigot release: net.minecraft.nbt.NBTTagCompound
        if(nbtTagCompoundClazz==null) nbtTagCompoundClazz = ReflectionUtil.getNmClass("server","NBTTagCompound"); 
        if(nbtTagCompoundClazz==null) nbtTagCompoundClazz = ReflectionUtil.getNmVersionedClass("nbt","NBTTagCompound"); 
        nmsTagClass = nbtTagCompoundClazz;
        return nmsTagClass;
    }
    
    /**
    * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
    * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
    *
    * @param itemStack the item to convert
    * @return the Json string representation of the item
    */
    public static String convertItemStackToJson(ItemStack itemStack) {
        // ItemStack methods to get a net.minecraft.server.ItemStack object for serialization
        Class<?> craftItemStackClazz = ReflectionUtil.getObcVersionedClass("inventory.CraftItemStack");//1.16.5 and lower.  1.17 first spigot release is unchanged.
        if(craftItemStackClazz==null) throw new IllegalStateException("Cannot find OBC CraftItemStack class (for converting nbt to json)");
        Method asNMSCopyMethod = ReflectionUtil.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

        // NMS Method to serialize a net.minecraft.server.ItemStack to a valid Json string
        Class<?> nmsItemStackClazz = findNmsItemClass();
        if(nmsItemStackClazz==null) throw new IllegalStateException("Cannot find NM ItemStack class (for converting nbt to json)");
        
        
        Class<?> nbtTagCompoundClazz = findNmsTagClass();
        if(nbtTagCompoundClazz==null) throw new IllegalStateException("Cannot find NM NBTTagCompound class (for converting nbt to json)");
        Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
        Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
        Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
            return null;
        }

        // Return a string representation of the serialized object
        return itemAsJsonObject.toString();
    }
}
