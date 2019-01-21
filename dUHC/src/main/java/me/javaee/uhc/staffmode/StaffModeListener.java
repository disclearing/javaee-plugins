package me.javaee.uhc.staffmode;

import me.javaee.uhc.UHC;
import me.javaee.uhc.menu.menu.AlivePlayersMenu;
import me.javaee.uhc.menu.menu.AlivePlayersMenuY;
import me.javaee.uhc.menu.menu.NetherPlayersMenu;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.utils.CustomPlayerInventory;
import me.javaee.uhc.utils.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class StaffModeListener implements Listener {
    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();

            if (UHC.getInstance().getStaffModeManager().getStaffModeList().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage2(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (UHC.getInstance().getStaffModeManager().getStaffModeList().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (UHC.getInstance().getStaffModeManager().getStaffModeList().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getStaffModeManager().getStaffModeList().contains(player)) {
            if (UHC.getInstance().getGameManager().getHost() == player) {
                return;
            }

            if (player.hasPermission("staff.bypass.blockbreak")) return;

            if (event.getBlock().getType() == Material.STONE) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getGameManager().getHost() == player) {
            return;
        }

        if (UHC.getInstance().getStaffModeManager().getStaffModeList().contains(player)) {
            if (player.hasPermission("staff.bypass.blockbreak")) return;

            if (event.getBlock().getType() == Material.STONE || event.getBlock().getType() == Material.DIAMOND_ORE || event.getBlock().getType() == Material.GOLD_ORE) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getStaffModeManager().getStaffModeList().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onConsome(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getStaffModeManager().getStaffModeList().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (UHC.getInstance().getStaffModeManager().getStaffModeList().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getStaffModeManager().getStaffModeList().contains(player)) {
            if (event.getPlayer().getItemInHand().getType() == Material.BOOK) {
                if (event.getRightClicked() instanceof Player) {
                    Player interacted = (Player) event.getRightClicked();

                    player.openInventory(new CustomPlayerInventory(interacted).getBukkitInventory());
                }
            }
        }
    }

    @EventHandler
    public void onInvInteract(InventoryInteractEvent event) {
        if (!event.getWhoClicked().hasPermission("command.invsee.admin")) {
            if (event.getInventory().getName() != null && event.getInventory().getName().toLowerCase().contains("staff")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getInventory() != null) {
            if (event.getClickedInventory() != null) {
                if (event.getClickedInventory().getName().contains("Fake")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (UHC.getInstance().getGameManager().getModerators().contains(event.getPlayer()) || UHC.getInstance().getGameManager().getHelpers().contains(event.getPlayer()) || UHC.getInstance().getGameManager().getHost() == event.getPlayer()) {
            if (event.getPlayer().getItemInHand().getType() != Material.AIR) {
                if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null) {
                    if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Alive Players")) {
                        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            ArrayList<Player> players = new ArrayList<>();
                            for (UUID player : UHC.getInstance().getGameManager().getAlivePlayers()) {
                                if (Bukkit.getPlayer(player) != null) {
                                    players.add(Bukkit.getPlayer(player));
                                }
                            }
                            if (players.size() <= 0) return;

                            Player randomPlayer = players.get(new Random().nextInt(players.size()));

                            event.getPlayer().teleport(randomPlayer);
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes("&eYou have been teleported to &b" + randomPlayer.getName() + "&e."));
                        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            new AlivePlayersMenu(event.getPlayer()).open(event.getPlayer());
                        }
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Alive Players " + ChatColor.GRAY + "(Y: -35)")) {
                        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            ArrayList<Player> players = new ArrayList<>();
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getLocation().getBlockY() <= 35) {
                                    players.add(player);
                                }
                            }
                            if (players.size() <= 0) return;

                            Player randomPlayer = players.get(new Random().nextInt(players.size()));

                            event.getPlayer().teleport(randomPlayer);
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes("&eYou have been teleported to &b" + randomPlayer.getName() + "&e."));

                            event.setCancelled(true);
                        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            new AlivePlayersMenuY(event.getPlayer()).open(event.getPlayer());
                        }
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Alive Players " + ChatColor.GRAY + "(Nether)")) {
                        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            ArrayList<Player> players = new ArrayList<>();
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getWorld().getName().equalsIgnoreCase("world_nether")) {
                                    players.add(player);
                                }
                            }
                            if (players.size() <= 0) return;

                            Player randomPlayer = players.get(new Random().nextInt(players.size()));

                            event.getPlayer().teleport(randomPlayer);
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes("&eYou have been teleported to &b" + randomPlayer.getName() + "&e."));
                            event.getPlayer().setAllowFlight(true);

                            event.setCancelled(true);
                        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            new NetherPlayersMenu(event.getPlayer()).open(event.getPlayer());
                        }
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Go to center")) {
                        event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0, 100, 0));
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes("&eYou have been teleported to the center."));
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Miner Alerts")) {
                        Profile profile = ProfileUtils.getInstance().getProfile(event.getPlayer().getUniqueId());

                        if (profile.isAlerts()) {
                            profile.setAlerts(false);
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &cdisabled &ethe alerts."));
                        } else {
                            profile.setAlerts(true);
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &aenabled &ethe alerts."));
                        }
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
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Unvanish")) {
                        Player player = event.getPlayer();

                        for (Player online : Bukkit.getOnlinePlayers()) {
                            online.showPlayer(player);
                        }

                        player.setItemInHand(new ItemBuilder(Material.INK_SACK).setDurability(10).setName(ChatColor.BLUE + "Vanish").build());
                        player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou are no longer vanished."));
                        ProfileUtils.getInstance().getProfile(player.getUniqueId()).setVanish(false);
                    } else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Vanish")) {
                        Player player = event.getPlayer();

                        for (UUID alive : UHC.getInstance().getGameManager().getAlivePlayers()) {
                            if (Bukkit.getPlayer(alive) != null) {
                                Bukkit.getPlayer(alive).hidePlayer(player);
                            }
                        }
                        player.setItemInHand(new ItemBuilder(Material.INK_SACK).setDurability(8).setName(ChatColor.BLUE + "Unvanish").build());
                        player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou are now vanished."));
                        ProfileUtils.getInstance().getProfile(player.getUniqueId()).setVanish(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlaceEvent(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission("litebans.tempban")) {
            write(event.getPlayer(), "has placed " + WordUtils.capitalizeFully(event.getBlock().getType().name()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDropEvent(PlayerDropItemEvent event) {
        if (event.getPlayer().hasPermission("litebans.tempban")) {
            write(event.getPlayer(), "has dropped " + WordUtils.capitalizeFully(event.getItemDrop().getType().name()) + (event.getItemDrop().getItemStack().getItemMeta() != null ? " (name: " + event.getItemDrop().getItemStack().getItemMeta().getDisplayName()  + ")" : ""));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommandEvent(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("litebans.tempban")) {
            write(event.getPlayer(), "has executed '" + event.getMessage()  + "'. ");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClickInventory(InventoryClickEvent event) {
        if (event.getWhoClicked().hasPermission("litebans.tempban")) {
            if (event.getInventory() != null && event.getClickedInventory() != null) {
                if (event.getInventory().getName() != null) {
                    if (event.getInventory().getName().toLowerCase().contains("inventory")) {
                        write((Player) event.getWhoClicked(), "has clicked in '" + event.getInventory().getName() + "' added " + event.getCurrentItem().getAmount() + " " + WordUtils.capitalizeFully(event.getCurrentItem().getType().name()));
                    }
                }
            }
        }
    }

    public void write(Player player, String message) {
        try {
            String dir = "antiabuse/" + player.getName() + "/";
            new File(dir).mkdirs();
            File file = new File("antiabuse/" + player.getName() + "/" + player.getName() + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fstream = new FileWriter(file, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(dateManager(message));
            out.newLine();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String dateManager(String message) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return "(UHC #" + UHC.getInstance().getUhcNumber() + ") [" + format.format(date) + "] " + message;
    }
}
