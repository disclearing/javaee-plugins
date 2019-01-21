package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.UHC;
import me.javaee.uhc.handlers.Scenario;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class VanillaPlusListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (Scenario.getByName("Vanilla+").isEnabled()) {
            if (block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2) {
                if (getRandomValue(random, 0, 100, 1) <= 3) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
                }
            } else if (block.getType() == Material.GRAVEL) {
                if (getRandomValue(random, 0, 100, 1) <= 3) {
                    event.getBlock().getDrops().clear();
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.FLINT));
                }
            }
        }
    }

    private final Random random = new Random();

    @EventHandler
    public void onDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);

        if (getRandomValue(random, 0, 100, 1) <= 3) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
        }
    }

    private double getRandomValue(Random random, int lowerBound, int upperBound, int decimalPlaces) {
        if (lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0) {
            throw new IllegalArgumentException("Illegal Argument Exception");
        }
        double dbl = (random == null ? new Random() : random).nextDouble() * (upperBound - lowerBound) + lowerBound;

        return Double.parseDouble(String.format("%." + decimalPlaces + "f", new Object[]{Double.valueOf(dbl)}).replace(",", "."));
    }
}
