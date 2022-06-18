/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.libraries.sainttx;

import com.github.crashdemons.displayitem_spigot.compatibility.CompatibilityUnavailableException;
import com.github.crashdemons.displayitem_spigot.compatibility.Version;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author sainttx
 */
public class ItemConverter {
    
    private static final HashMap<int[],String> saveMethods= new HashMap<>();
    
    static{
        saveMethods.put(new int[]{1,18,0}, "b");  //261:267:net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag) -> b
        saveMethods.put(new int[]{1,18,1}, "b");  //260:266:net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag) -> b
        saveMethods.put(new int[]{1,18,2}, "b");
        saveMethods.put(new int[]{1,19,0}, "b");
        saveMethods.put(new int[]{1,19,1}, "b");
        saveMethods.put(new int[]{1,19,2}, "b");
        saveMethods.put(new int[]{1,19,3}, "b");
        saveMethods.put(new int[]{1,19,4}, "b");
        Version.init();
    }
    
    private ItemConverter(){

    }
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
    
    private static String getVersionedItemSaveMethod(){
        for(Map.Entry<int[],String> versionedMethod : saveMethods.entrySet()){
            int[] ver = versionedMethod.getKey();
            String method = versionedMethod.getValue();
            if(Version.checkEquals(ver[0], ver[1], ver[2])) return method;
        }
        return null;
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
        if(saveNmsItemStackMethod==null){
            String saveNmsItemStackMethod_name = getVersionedItemSaveMethod();
            if(saveNmsItemStackMethod_name==null) throw new CompatibilityUnavailableException("This server version is not supported and the 'save' method mapping is unknown.");
            saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, saveNmsItemStackMethod_name, nbtTagCompoundClazz);
        }
        
        if(saveNmsItemStackMethod==null) throw new IllegalStateException("Cannot find ItemStack.save method (for converting nbt to json)");

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
