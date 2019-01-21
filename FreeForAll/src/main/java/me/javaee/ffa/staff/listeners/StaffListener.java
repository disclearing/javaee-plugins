package me.javaee.ffa.staff.listeners;

import me.javaee.ffa.FFA;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.profiles.status.PlayerStatus;
import me.javaee.ffa.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class StaffListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("litebans.tempban") && FFA.getPlugin().getProfileManager().getProfile(online).isVanished()) {
                player.hidePlayer(online);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null) {
            if (event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getDisplayName() != null) {
                ItemStack item = event.getItem();
                Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

                if (profile.getPlayerStatus() == PlayerStatus.STAFF) {
                    if (item.getItemMeta().getDisplayName().contains("Random")) {
                        if (Bukkit.getOnlinePlayers().size() <= 1) {
                            player.sendMessage(ChatColor.RED + "There are not enough players to teleport to.");
                            return;
                        }

                        Player toReturn = getPlayerToTeleportTo();
                        player.teleport(toReturn == player ? getPlayerToTeleportTo() : toReturn);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have been teleported to &c" + toReturn.getName() + "&e."));

                        event.setCancelled(true);
                    } else if (item.getItemMeta().getDisplayName().contains("You are vanished")) {
                        player.getInventory().setItemInHand(new ItemBuilder(Material.INK_SACK).setDurability(10).setDisplayName("&6You are not vanished").create());
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            online.showPlayer(player);
                            player.showPlayer(online);
                        }
                        profile.setVanished(false);

                        event.setCancelled(true);
                    } else if (item.getItemMeta().getDisplayName().contains("You are not vanished")) {
                        player.getInventory().setItemInHand(new ItemBuilder(Material.INK_SACK).setDurability(8).setDisplayName("&6You are vanished").create());
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            if (!online.hasPermission("litebans.tempban")) {
                                online.hidePlayer(player);
                            }
                            player.showPlayer(online);
                        }
                        profile.setVanished(true);

                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (player.getItemInHand() != null) {
            if (player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null) {
                ItemStack item = player.getItemInHand();
                Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

                if (profile.getPlayerStatus() == PlayerStatus.STAFF) {
                    if (item.getItemMeta().getDisplayName().contains("Inspector") && event.getRightClicked() instanceof Player) {
                        player.openInventory(((Player) event.getRightClicked()).getInventory());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getInventory() != null) {
            Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

            if (profile.getPlayerStatus() == PlayerStatus.STAFF) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

        if (profile.getPlayerStatus() == PlayerStatus.STAFF || profile.getPlayerStatus() == PlayerStatus.FROZEN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

        if (profile.getPlayerStatus() == PlayerStatus.STAFF || profile.getPlayerStatus() == PlayerStatus.FROZEN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

            if (profile.getPlayerStatus() == PlayerStatus.STAFF || profile.getPlayerStatus() == PlayerStatus.FROZEN) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

            if (profile.getPlayerStatus() == PlayerStatus.STAFF || profile.getPlayerStatus() == PlayerStatus.FROZEN) {
                event.setCancelled(true);
            }
        }

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

            if (profile.getPlayerStatus() == PlayerStatus.STAFF || profile.getPlayerStatus() == PlayerStatus.FROZEN) {
                event.setCancelled(true);
            }
        }
    }

    public Player getPlayerToTeleportTo() {
        return (Player) Bukkit.getOnlinePlayers().toArray()[new Random().nextInt(Bukkit.getOnlinePlayers().size())];
    }
}
