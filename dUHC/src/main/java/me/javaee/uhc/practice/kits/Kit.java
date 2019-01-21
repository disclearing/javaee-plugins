package me.javaee.uhc.practice.kits;

import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class Kit {
    public void giveItems(Player player) {
        ItemStack sword = new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, 1).build();
        ItemStack helmet = new ItemBuilder(Material.IRON_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        ItemStack chest = new ItemBuilder(Material.IRON_CHESTPLATE).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        ItemStack pants = new ItemBuilder(Material.IRON_LEGGINGS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        ItemStack boots = new ItemBuilder(Material.IRON_BOOTS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        ItemStack rod = new ItemBuilder(Material.FISHING_ROD).build();
        ItemStack water = new ItemBuilder(Material.WATER_BUCKET).build();
        ItemStack bow = new ItemBuilder(Material.BOW).addEnchantment(Enchantment.ARROW_DAMAGE, 1).build();

        player.getInventory().addItem(sword);
        player.getInventory().addItem(rod);
        player.getInventory().setItem(2, bow);
        //player.getInventory().setItem(3, new ItemStack(Material.COBBLESTONE, 64));
        player.getInventory().setItem(4, water);
        player.getInventory().setItem(9, new ItemStack(Material.ARROW, 16));

        player.getInventory().setArmorContents(new ItemStack[] {boots, pants, chest, helmet});

        player.getInventory().setHeldItemSlot(0);
        player.updateInventory();
    }
}
