package me.javaee.uhc.listeners.misc;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.commands.ScreenshareCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ScreensharedListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        int toX = to.getBlockX();
        int toZ = to.getBlockZ();

        if (from.getX() != toX || from.getZ() != toZ) {
            if (ScreenshareCommand.screenShared.contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes("&7&m----------------------"));
                player.sendMessage(ChatColor.translateAlternateColorCodes("&cYou are frozen."));
                player.sendMessage(ChatColor.translateAlternateColorCodes("&cJoin &4ts.silexpvp.net"));
                player.sendMessage(ChatColor.translateAlternateColorCodes("&7&m----------------------"));
                event.setTo(from);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (ScreenshareCommand.screenShared.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes("&7&m----------------------"));
            player.sendMessage(ChatColor.translateAlternateColorCodes("&cYou are frozen."));
            player.sendMessage(ChatColor.translateAlternateColorCodes("&cJoin &4ts.silexpvp.net"));
            player.sendMessage(ChatColor.translateAlternateColorCodes("&7&m----------------------"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (ScreenshareCommand.screenShared.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes("&7&m----------------------"));
            player.sendMessage(ChatColor.translateAlternateColorCodes("&cYou are frozen."));
            player.sendMessage(ChatColor.translateAlternateColorCodes("&cJoin &4ts.silexpvp.net"));
            player.sendMessage(ChatColor.translateAlternateColorCodes("&7&m----------------------"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (ScreenshareCommand.screenShared.contains(player.getUniqueId())) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&4&l" + player.getName() + "&c has logged out while frozen"));
            ScreenshareCommand.screenShared.remove(player.getUniqueId());
        }

        if (UHC.getInstance().getGameManager().getModerators().contains(player)) {
            UHC.getInstance().getGameManager().getModerators().remove(player);
        }

        if (UHC.getInstance().getGameManager().getHelpers().contains(player)) {
            UHC.getInstance().getGameManager().getHelpers().remove(player);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            if (ScreenshareCommand.screenShared.contains(damaged.getUniqueId())) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + "That player is frozen.");
                return;
            }

            if (ScreenshareCommand.screenShared.contains(attacker.getUniqueId())) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + "You are frozen.");
            }
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (ScreenshareCommand.screenShared.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
