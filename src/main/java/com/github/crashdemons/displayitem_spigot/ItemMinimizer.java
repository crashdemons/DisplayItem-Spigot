/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class ItemMinimizer {

    private ItemMinimizer() {
    }

    public static ItemStack minimize(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.getType() == Material.AIR || !stack.hasItemMeta()) {
            return stack;
        }

        if(stack.getType().name().toUpperCase().endsWith("SHULKER_BOX")) return minimizeShulker(stack);
        
        
        switch (stack.getType()) {
            case WRITTEN_BOOK:
            case WRITABLE_BOOK:
                return minimizeBook(stack);
            default:
                return stack;
        }
    }

    private static ItemStack minimizeBook(ItemStack stack) {
        ItemStack copiedStack = new ItemStack(stack);
        ItemMeta meta = copiedStack.getItemMeta();
        if (!(meta instanceof BookMeta)) {
            return stack;
        }
        BookMeta book = (BookMeta) meta;
        if (!book.hasPages()) {
            return stack;
        }

        book.setPages(minimizeBookPages(book.getPages()));
        copiedStack.setItemMeta(book);
        

        return copiedStack;
    }

    private static ItemStack minimizeShulker(ItemStack stack) {
        ItemStack copiedItem = new ItemStack(stack);
        ItemMeta meta = stack.getItemMeta();
        if (!(meta instanceof BlockStateMeta)) {
            return stack;
        }
        BlockStateMeta block = (BlockStateMeta) meta;
        BlockState state = block.getBlockState();
        if (!(state instanceof ShulkerBox)) {
            return stack;
        }

        ShulkerBox box = (ShulkerBox) state;
        Inventory inv = box.getInventory();
                
        inv = minimizeShulkerInventory(inv);
        
        block.setBlockState(state);
        
        copiedItem.setItemMeta(block);

        return copiedItem;

    }

    private static List<String> minimizeBookPages(List<String> pages) {//change all pages to blank
        return pages.stream().map((page) -> "").collect(Collectors.toList());
    }

    private static final int NUM_VISIBLE_SHULKER_ITEMS = 6;
    private static final Material UNSEEN_ITEM_PLACEHOLDER = Material.ICE;
    
    private static Inventory minimizeShulkerInventory(Inventory inv) {
        
        for(int i = 0 ; i < inv.getSize() ; i++) {
            ItemStack item = inv.getItem(i);
            
            if(i<NUM_VISIBLE_SHULKER_ITEMS) item = minimizeNamedShulkerItem(item);//replace any items seen on the hover list (6) with versions that are ONLY named
            else item = minimizeUnseenShulkerItem(item);//replace any items in the box but not seen with placeholders
            
            inv.setItem(i, item);
        }
        
        
        return inv;
    }

    
    private static ItemStack minimizeNamedShulkerItem(ItemStack stack){//create a name-only copy of an item, if possible
        if(stack==null) return stack;
        if(stack.getType()==Material.AIR) return stack;
        ItemStack replacement = new ItemStack(stack.getType());
        replacement.setAmount(stack.getAmount());
        
        if(stack.hasItemMeta()){
            ItemMeta replacementMeta = Bukkit.getItemFactory().getItemMeta(stack.getType());
            ItemMeta oldMeta = stack.getItemMeta();
            if(oldMeta.hasDisplayName()) replacementMeta.setDisplayName(oldMeta.getDisplayName());
            replacement.setItemMeta(replacementMeta);
        }
        return replacement;
    }
    private static ItemStack minimizeUnseenShulkerItem(ItemStack stack){//create a placeholder item with the same amount
        ItemStack placeholder = new ItemStack(UNSEEN_ITEM_PLACEHOLDER);
        placeholder.setAmount(stack.getAmount());
        return placeholder;
    }
}
