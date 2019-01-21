package me.javaee.uhc.listeners.misc;

import me.javaee.uhc.UHC;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.team.UHCTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ScatterListeners implements Listener {

    @EventHandler
    public void onDismountCart(VehicleExitEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(BlockBreakEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(BlockPlaceEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            if (UHC.getInstance().getGameManager().getAlivePlayers().contains(event.getPlayer().getUniqueId())) {
                UHC.getInstance().getGameManager().getAlivePlayers().remove(event.getPlayer().getUniqueId());
            }

            if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 2) {
                if (UHCTeam.getByUUID(event.getPlayer().getUniqueId()) != null) {
                    if (UHCTeam.getByUUID(event.getPlayer().getUniqueId()).getPlayerList().size() == 1) {
                        UHC.getInstance().getTeams().remove(UHCTeam.getByUUID(event.getPlayer().getUniqueId()));
                    } else {
                        UHCTeam.getByUUID(event.getPlayer().getUniqueId()).getPlayerList().remove(event.getPlayer().getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(PlayerDropItemEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(PlayerItemConsumeEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            event.setCancelled(true);
        }
    }
}
