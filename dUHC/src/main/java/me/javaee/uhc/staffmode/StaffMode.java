package me.javaee.uhc.staffmode;

import lombok.Getter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class StaffMode {
    @Getter private ArrayList<Player> staffModeList = new ArrayList<>();

    public void vanishFromAll(Player player) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players != null) {
                players.showPlayer(player);
                players.hidePlayer(player);
            }
        }
    }

    public void setStaffMode(Player player) {
        if (!staffModeList.contains(player)) {
            if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                UHC.getInstance().getSpectatorManager().getSpectators().remove(player);
            }

            staffModeList.add(player);

            player.sendMessage(ChatColor.translateAlternateColorCodes("&6You have now staff mode."));

            player.spigot().setCollidesWithEntities(false);
            player.setHealth(20D);
            player.setFoodLevel(20);
            player.setSaturation(20F);
            player.setAllowFlight(true);
            player.setGameMode(GameMode.CREATIVE);
            player.getInventory().clear();
            player.setCanPickupItems(true);

            player.getInventory().setItem(0, new ItemBuilder(Material.WATCH).setName(ChatColor.BLUE + "Alive Players").build());
            player.getInventory().setItem(1, new ItemBuilder(Material.DIAMOND_PICKAXE).setName(ChatColor.BLUE + "Alive Players " + ChatColor.GRAY + "(Y: -35)").build());
            player.getInventory().setItem(2, new ItemBuilder(Material.getMaterial(405)).setName(ChatColor.BLUE + "Alive Players " + ChatColor.GRAY + "(Nether)").build());
            player.getInventory().setItem(3, new ItemBuilder(Material.INK_SACK).setDurability(8).setName(ChatColor.BLUE + "Show Spectators").build());
            player.getInventory().setItem(7, new ItemBuilder(Material.BOOK).setName(ChatColor.BLUE + "Inventory Viewer").build());
            player.getInventory().setItem(6, new ItemBuilder(Material.COMPASS).setName(ChatColor.BLUE + "Go to center").build());
            player.getInventory().setItem(8, new ItemBuilder(Material.NETHER_STAR).setName(ChatColor.BLUE + "Miner Alerts").build());
            player.getInventory().setItem(5, new ItemBuilder(Material.INK_SACK).setDurability(8).setName(ChatColor.BLUE + "Unvanish").build());
            player.getInventory().setItem(4, new ItemBuilder(Material.CARPET).setDurability(14).setName(ChatColor.BLUE + "Better Vision").build());
        }
    }
}
