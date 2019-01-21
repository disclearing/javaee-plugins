package me.javaee.uhc.listeners.misc;

import me.javaee.uhc.UHC;
import me.javaee.uhc.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.scheduler.BukkitRunnable;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */

public class DeathmatchListener implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("Deathmatch")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    block.getLocation().getBlock().setType(Material.AIR);
                }
            }.runTaskLater(UHC.getInstance(), 20L * 5L);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.getBlock().getLocation().getWorld().getName().equalsIgnoreCase("Deathmatch")) {
            if (event.getBlock().getType() == Material.STATIONARY_LAVA || event.getBlock().getType() == Material.STATIONARY_WATER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockFromToEvent event) {
        if (event.getBlock().getLocation().getWorld().getName().equalsIgnoreCase("Deathmatch")) {
            if (event.getBlock().getType() == Material.STATIONARY_LAVA || event.getBlock().getType() == Material.STATIONARY_WATER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getLocation().getWorld().getName().equalsIgnoreCase("Deathmatch")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWater(PlayerBucketEmptyEvent event) {
        Location l = event.getBlockClicked().getLocation();

        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("Deathmatch")) {
            if (l.add(0, 1, 1).getBlock().getType() == Material.STATIONARY_WATER || l.add(0, 0, 1).getBlock().getType() == Material.LAVA) {
                event.setCancelled(true);
            }

            if (l.add(1, 1, 0).getBlock().getType() == Material.STATIONARY_WATER || l.add(0, 0, 1).getBlock().getType() == Material.LAVA) {
                event.setCancelled(true);
            }

            if (l.add(0, 1, 1).getBlock().getType() == Material.STATIONARY_WATER || l.add(1, 0, 0).getBlock().getType() == Material.LAVA) {
                event.setCancelled(true);
            }

            if (l.add(1, 1, 0).getBlock().getType() == Material.STATIONARY_WATER || l.add(1, 0, 0).getBlock().getType() == Material.LAVA) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("Deathmatch")) {
            Player player = event.getPlayer();
            Location from = event.getFrom();
            Location to = event.getTo();

            int toX = to.getBlockX();
            int toY = to.getBlockY();
            int toZ = to.getBlockZ();

            if (from.getX() != toX || from.getY() != toY || from.getZ() != toZ) {
                if (toY > 101) {
                    event.setTo(event.getFrom());
                    player.sendMessage(ChatColor.RED + "You can't go that high in the deathmatch!");
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("Deathmatch")) {
            if (event.getBlock().getType() == Material.GRASS || event.getBlock().getType() == Material.DIRT || event.getBlock().getType() == Material.BEDROCK || event.getBlock().getType() == Material.GLASS) {
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onLoadChunk(EntitySpawnEvent event) {
        if (event.getLocation().getWorld().getName().equalsIgnoreCase("Deathmatch")) {
            if (event.getEntity() instanceof Item || event.getEntity() instanceof Villager) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
