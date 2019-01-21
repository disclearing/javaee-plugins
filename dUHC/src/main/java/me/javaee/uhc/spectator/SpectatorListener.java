package me.javaee.uhc.spectator;

import com.mysql.jdbc.StringUtils;
import com.sk89q.util.StringUtil;
import me.javaee.uhc.UHC;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class SpectatorListener implements Listener {
    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();

            if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractPlates(PlayerInteractEvent event) {
        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(event.getPlayer())) {
            if (event.getClickedBlock() != null) {
                if (event.getClickedBlock().getType() == Material.STONE_PLATE || event.getClickedBlock().getType() == Material.WOOD_PLATE || event.getClickedBlock().getType() == Material.CHEST) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamage2(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void itemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onConsome(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCrash(PlayerCommandPreprocessEvent event) {
        if (net.minecraft.util.org.apache.commons.lang3.StringUtils.containsIgnoreCase(event.getMessage(), "pex")) {
            if (!event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTag(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null) {
            if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                if (item.getType() == Material.NAME_TAG) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        if (UHC.getInstance().getSpectatorManager().getSpectators().contains((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(event.getEntity()) || UHC.getInstance().getSpectatorManager().getSpectators().contains(event.getMount())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(event.getPlayer())) {
            UHC.getInstance().getSpectatorManager().getSpectators().remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(event.getPlayer())) {
            if (event.getPlayer().getItemInHand().getType() != Material.AIR) {
                if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null) {
                    if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Random Teleporter")) {
                        event.setCancelled(true);

                        if (UHC.getInstance().getGameManager().getAlivePlayers().size() == 0) {
                            return;
                        }

                        ArrayList<Player> players = new ArrayList<>();
                        for (UUID player : UHC.getInstance().getGameManager().getAlivePlayers()) {
                            if (Bukkit.getPlayer(player) != null) {
                                if (Bukkit.getPlayer(player).getLocation().getBlockY() >= 35) {
                                    players.add(Bukkit.getPlayer(player));
                                }
                            }
                        }

                        if (players.size() <= 0) return;
                        int random = new Random().nextInt(players.size() - 1);
                        if (random>= players.size()) return;

                        Player randomPlayer = players.get(new Random().nextInt(players.size() - 1));

                        event.getPlayer().teleport(randomPlayer);
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes("&6You have been randomly teleported to &f" + randomPlayer.getName() + "&6."));
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Go to center")) {
                        event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0, 100, 0));
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Show Spectators")) {
                        Player player = event.getPlayer();

                        for (Player players : UHC.getInstance().getSpectatorManager().getSpectators()) {
                            if (players != null) {
                                player.showPlayer(players);
                            }
                        }

                        player.setItemInHand(new ItemBuilder(Material.INK_SACK).setDurability(10).setName(ChatColor.BLUE + "Hide Spectators").build());
                        player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &aenabled &espectators."));
                        ProfileUtils.getInstance().getProfile(player.getUniqueId()).setSpectators(true);
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Hide Spectators")) {
                        Player player = event.getPlayer();

                        for (Player players : UHC.getInstance().getSpectatorManager().getSpectators()) {
                            if (players != null) {
                                player.hidePlayer(players);
                            }
                        }

                        player.setItemInHand(new ItemBuilder(Material.INK_SACK).setDurability(8).setName(ChatColor.BLUE + "Show Spectators").build());
                        player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &cdisabled &espectators."));
                        ProfileUtils.getInstance().getProfile(player.getUniqueId()).setSpectators(false);
                    }
                }
            }
        }
    }
}
