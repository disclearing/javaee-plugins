package me.javaee.meetup.spectator;

import me.javaee.meetup.Meetup;
import me.javaee.meetup.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.ArrayList;
import java.util.Random;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class SpectatorListener implements Listener {
    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage2(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void itemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void grabItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onConsome(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTag(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null) {
            if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
                if (item.getType() == Material.NAME_TAG) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(event.getEntity()) || Meetup.getPlugin().getSpectatorManager().getSpectators().contains(event.getMount())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent event) {
        if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(event.getPlayer())) {
            Meetup.getPlugin().getSpectatorManager().getSpectators().remove(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void DamageEvent(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Arrow)) {
            Arrow arrow = (Arrow) event.getDamager();
            if ((arrow.getShooter() instanceof Player)) {
                Player shooter = (Player) arrow.getShooter();

                Damageable damageable = (Damageable) event.getEntity();
                if ((damageable instanceof Player)) {
                    Player victim = (Player) damageable;
                    double victimHealth = damageable.getHealth();
                    int damage = (int) event.getFinalDamage();

                    if (!damageable.isDead()) {
                        int health = (int) (victimHealth - damage);

                        if (health > 0) {
                            shooter.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6" + victim.getName() + " &eis now at &c" + health + "❤"));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(event.getPlayer())) {
            if (event.getPlayer().getItemInHand().getType() != Material.AIR) {
                if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null) {
                    if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Random Teleporter")) {
                        event.setCancelled(true);

                        if (Meetup.getPlugin().getGameManager().getAlivePlayers().size() == 0) {
                            return;
                        }

                        ArrayList<Player> players = new ArrayList<>();
                        players.addAll(Meetup.getPlugin().getGameManager().getAlivePlayers());

                        if (players.size() <= 0) return;
                        Player randomPlayer = players.get(new Random().nextInt(players.size()));

                        event.getPlayer().teleport(randomPlayer);
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6You have been randomly teleported to &f" + randomPlayer.getName() + "&6."));
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Go to center")) {
                        event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0, 100, 0));
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Show Spectators")) {
                        Player player = event.getPlayer();

                        for (Player players : Bukkit.getOnlinePlayers()) {
                            if (players.getInventory().getHelmet().getType() == Material.LEATHER_HELMET || players.getInventory().getHelmet().getType() == Material.LEATHER_BOOTS) {
                                player.showPlayer(players);
                            }
                        }

                        player.setItemInHand(new ItemBuilder(Material.INK_SACK).setDurability(10).setName(ChatColor.BLUE + "Hide Spectators").build());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have &aenabled &espectators."));
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Hide Spectators")) {
                        Player player = event.getPlayer();

                        for (Player players : Bukkit.getOnlinePlayers()) {
                            if (players.getInventory().getHelmet().getType() == Material.LEATHER_HELMET || players.getInventory().getHelmet().getType() == Material.LEATHER_BOOTS) {
                                player.hidePlayer(players);
                            }
                        }

                        player.setItemInHand(new ItemBuilder(Material.INK_SACK).setDurability(8).setName(ChatColor.BLUE + "Show Spectators").build());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have &cdisabled &espectators."));
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Back to lobby")) {
                        event.getPlayer().kickPlayer("To lobby!");
                    }
                }
            }
        }
    }
}
