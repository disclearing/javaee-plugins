package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.UHC;
import me.javaee.uhc.handlers.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class DoubleOresListener implements Listener {
    @EventHandler
    public void onEntityDie(EntityDeathEvent event) {
        if (Scenario.getByName("DoubleOres").isEnabled()) {
            if (event.getEntity() instanceof Cow) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Material.COOKED_BEEF, 3));
                event.getDrops().add(new ItemStack(Material.LEATHER));
            } else if (event.getEntity() instanceof Chicken) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Material.COOKED_CHICKEN, 3));
                event.getDrops().add(new ItemStack(Material.FEATHER));
            } else if (event.getEntity() instanceof Pig) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Material.GRILLED_PORK, 3));
            } else if (event.getEntity() instanceof Horse) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Material.LEATHER));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMineBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (Scenario.getByName("DoubleOres").isEnabled()) {
            if (block.getType().name().toLowerCase().contains("ore")) {
                Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
                    UHC.getInstance().getMineManager().handleDatabaseMine(event);
                    UHC.getInstance().getMineManager().handleAlertsMine(event);
                });
            }

            if (block.getType() == Material.GRAVEL) {
                if (getRandomValue(random, 0, 100, 1) <= 50) {
                    event.getBlock().getDrops().clear();
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.FLINT));
                }
            }

            if (block.getType() == Material.GOLD_ORE) {
                event.setCancelled(true);

                block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_INGOT, 2));
                createExpOrb(block.getLocation(), 3);
                if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
                    short dur = event.getPlayer().getItemInHand().getDurability();
                    if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
                        player.setItemInHand(null);
                        player.updateInventory();
                    } else {
                        player.getItemInHand().setDurability(dur);
                    }
                }
            } else if (block.getType() == Material.DIAMOND_ORE) {
                event.setCancelled(true);

                block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.DIAMOND, 2));
                createExpOrb(block.getLocation(), 3);
                if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
                    short dur = event.getPlayer().getItemInHand().getDurability();
                    if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
                        player.setItemInHand(null);
                        player.updateInventory();
                    } else {
                        player.getItemInHand().setDurability(dur);
                    }
                }
            } else if (block.getType() == Material.IRON_ORE) {
                event.setCancelled(true);

                block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT, 2));
                createExpOrb(block.getLocation(), 3);
                if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
                    short dur = event.getPlayer().getItemInHand().getDurability();
                    if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
                        player.setItemInHand(null);
                        player.updateInventory();
                    } else {
                        player.getItemInHand().setDurability(dur);
                    }
                }
            }
        }
    }

    public void createExpOrb(Location location, int amount) {
        ExperienceOrb experienceOrb = (ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
        experienceOrb.setExperience(amount);
    }

    private final Random random = new Random();
    private double getRandomValue(Random random, int lowerBound, int upperBound, int decimalPlaces) {
        if (lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0) {
            throw new IllegalArgumentException("Illegal Argument Exception");
        }
        double dbl = (random == null ? new Random() : random).nextDouble() * (upperBound - lowerBound) + lowerBound;

        return Double.parseDouble(String.format("%." + decimalPlaces + "f", new Object[]{Double.valueOf(dbl)}).replace(",", "."));
    }
}
