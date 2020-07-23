/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot;

import java.util.List;
import java.util.stream.Collectors;
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

        switch (stack.getType()) {
            case WRITTEN_BOOK:
            case WRITABLE_BOOK:
                return minimizeBook(stack);
            case SHULKER_BOX:
                return minimizeShulker(stack);
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

    private static List<String> minimizeBookPages(List<String> pages) {
        return pages.stream().map((page) -> "").collect(Collectors.toList());
    }

    private static Inventory minimizeShulkerInventory(Inventory inv) {
        return null;
    }

}
