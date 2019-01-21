package me.javaee.uhc.listeners;

import me.javaee.uhc.UHC;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.events.GameStartEvent;
import me.javaee.uhc.events.TeamJoinEvent;
import me.javaee.uhc.events.TeamLeaveEvent;
import me.javaee.uhc.team.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.github.paperspigot.event.server.ServerShutdownEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class TeamListeners implements Listener {
    @EventHandler
    public void onJoinTeam(TeamJoinEvent event) {
        Player player = event.getPlayer();
        UHCTeam team = event.getTeam();

        for (UUID players : team.getPlayerList()) {
            if (Bukkit.getPlayer(players) != null) {
                player.showPlayer(Bukkit.getPlayer(players));
                Bukkit.getPlayer(players).showPlayer(player);
            }
        }
    }

    @EventHandler
    public void onLeaveTeam(TeamLeaveEvent event) {
        Player player = event.getPlayer();
        UHCTeam team = event.getTeam();

        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            for (UUID players : team.getPlayerList()) {
                if (Bukkit.getPlayer(players) != null) {
                    if (!UHC.getInstance().getPracticeManager().getPracticePlayers().contains(player)) {
                        player.hidePlayer(Bukkit.getPlayer(players));
                        Bukkit.getPlayer(players).hidePlayer(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerPostPortalEvent event) {
        Location to = event.getTo();
        Block block = to.getBlock();
        // What if the portal material is behind (4 checks)
        Block block2 = to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1);
        if (block2.getType() == Material.PORTAL) {
            // break blocks left and right
            to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ() - 1).setType(Material.AIR);

            // These are the blocks above
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);

            // These are the 2x2 on either side of the portal
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);

            // These are the corner blocks
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);

            // Nether fences
            for (int i = 0; i < 4; i++) {
                // Nether brick fences 2x2
                to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ()).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ()).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);

                // Corner nether fences
                to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);
            }

            return;
        }
        block2 = to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + 1);
        if (block2.getType() == Material.PORTAL) {
            // break blocks left and right
            to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ() + 1).setType(Material.AIR);

            // These are the blocks above
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);

            // These are the 2x2 on either side of the portal
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ()).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);

            // These are the corner blocks
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);

            // Nether fences
            for (int i = 0; i < 4; i++) {
                // Nether brick fences 2x2
                to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ()).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ()).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);

                // Corner nether fences
                to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
            }
            return;
        }

        block2 = to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ());
        if (block2.getType() == Material.PORTAL) {
            // break blocks left and right
            to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 2, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 2, block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 2, block.getZ() - 1).setType(Material.AIR);

            // These are the blocks above
            to.getWorld().getBlockAt(block.getX(), block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);

            // These are the 2x2 on either side of the portal
            to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);

            // These are the corner blocks
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 2, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);

            // Nether fences
            for (int i = 0; i < 4; i++) {
                // Nether brick fences 2x2
                to.getWorld().getBlockAt(block.getX(), block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX(), block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);

                // Corner nether fences
                to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 2, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
            }
            return;
        }
        block2 = to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ());
        if (block2.getType() == Material.PORTAL) {
            to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 2, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 2, block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 2, block.getZ() - 1).setType(Material.AIR);

            to.getWorld().getBlockAt(block.getX(), block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 3, block.getZ() - 1).setType(Material.OBSIDIAN);

            to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() + 2).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() - 1, block.getZ() - 2).setType(Material.OBSIDIAN);

            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ() + 1).setType(Material.OBSIDIAN);
            to.getWorld().getBlockAt(block.getX() + 2, block.getY() - 1, block.getZ() - 1).setType(Material.OBSIDIAN);

            for (int i = 0; i < 4; i++) {
                to.getWorld().getBlockAt(block.getX(), block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX(), block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() + 2).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() + 1, block.getY() + i, block.getZ() - 2).setType(Material.NETHER_FENCE);

                to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() - 1, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ() + 1).setType(Material.NETHER_FENCE);
                to.getWorld().getBlockAt(block.getX() + 2, block.getY() + i, block.getZ() - 1).setType(Material.NETHER_FENCE);
            }

            return;
        }

        if (yawToFace(to.getYaw(), false) == BlockFace.NORTH) {
            to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() - 1).setType(Material.AIR);
        } else if (yawToFace(to.getYaw(), false) == BlockFace.EAST) {
            to.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
        } else if (yawToFace(to.getYaw(), false) == BlockFace.SOUTH) {
            to.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + 1).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ() + 1).setType(Material.AIR);
        } else if (yawToFace(to.getYaw(), false) == BlockFace.WEST) {
            to.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ()).setType(Material.AIR);
            to.getWorld().getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ()).setType(Material.AIR);
        }

    }

    public static final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static final BlockFace[] radial = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};

    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }

    @EventHandler
    public void onSwitch(PlayerChangedWorldEvent event) {
        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(event.getPlayer()) || UHC.getInstance().getGameManager().getModerators().contains(event.getPlayer()) || UHC.getInstance().getGameManager().getHost() == event.getPlayer()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(UHC.getInstance(), () -> event.getPlayer().setAllowFlight(true), 5L);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UHCTeam team = UHCTeam.getByUUID(event.getPlayer().getUniqueId());

        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            if (team != null) {
                if (team.getLeader() == player.getUniqueId()) {
                    UHC.getInstance().getTeams().remove(team);
                }

                if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 50) {
                    team.getPlayerList().remove(player.getUniqueId());
                } else {
                    if (team.getPlayerList().size() <= 1) {
                        UHC.getInstance().getTeams().remove(team);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().contains("/stop")) {
            event.getPlayer().sendMessage(ChatColor.RED + "Usa el comando /forcestop!");
            event.setCancelled(true);
        } else if (event.getMessage().toLowerCase().contains("/forcestop") && UHC.getInstance().getGameManager().getHost() == event.getPlayer()) {
            Bukkit.shutdown();
        }
    }

    @EventHandler
    public void onStartGame(GameStartEvent event) {
        if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 2) {
            for (UHCTeam team : UHC.getInstance().getTeams()) {
                if (team.getPlayerList().size() <= 0) {
                    UHC.getInstance().getTeams().remove(team);
                }

                if (team.getDtr() < 1) {
                    UHC.getInstance().getTeams().remove(team);
                }

                if (team.getPlayerList().size() == 1) {
                    team.setDtr(1);
                }
            }
        }

        Bukkit.getScheduler().runTaskTimer(UHC.getInstance(), () -> {
            if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 2) {
                UHC.getInstance().getGameManager().getAlivePlayers().forEach(uuid -> {
                    if (Bukkit.getPlayer(uuid) != null) {
                        Player player = Bukkit.getPlayer(uuid);

                        if (UHCTeam.getByUUID(player.getUniqueId()) == null) {
                            UHCTeam team = new UHCTeam(player.getUniqueId());
                            team.setDtr(1);

                            UHC.getInstance().getTeams().add(team);
                        }
                    }
                });
            }
        }, 20 * 10L, 20 * 5L);
    }

    @EventHandler
    public void onEntityDamage(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!UHC.getInstance().getConfigurator().getBooleanOption("ENDERPEARLDAMAGE").getValue()) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                event.setCancelled(true);

                player.teleport(event.getTo());
            }
        }
    }
}
