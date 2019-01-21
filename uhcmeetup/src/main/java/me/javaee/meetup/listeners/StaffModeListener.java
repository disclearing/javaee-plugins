package me.javaee.meetup.listeners;

import me.javaee.meetup.Meetup;
import net.silexpvp.nightmare.util.ItemCreator;
import net.silexpvp.nightmare.util.nms.CustomPlayerInventory;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

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

            if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage2(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void itemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onConsome(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
            if (event.getPlayer().getItemInHand().getType() == Material.BOOK) {
                if (event.getRightClicked() instanceof Player) {
                    Player interacted = (Player) event.getRightClicked();

                    //player.openInventory(new CustomPlayerInventory(interacted).getBukkitInventory());
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
        Player player = event.getPlayer();

        if (event.getItem() == null) return;

        if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
            Material material = event.getItem().getType();

            if (material == Material.SKULL_ITEM) {
                Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.GOLD + "Online Staff");

                Bukkit.getOnlinePlayers().forEach(online -> {
                    if (online.hasPermission("litebans.tempban"))
                        inventory.addItem(new ItemCreator(Material.SKULL_ITEM).setDurability(3).setDisplayName("&6" + online.getName()).create());
                });

                player.openInventory(inventory);
            } else if (material == Material.INK_SACK) {
                int durability = event.getItem().getDurability();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();

                if (durability == 8) {
                    Bukkit.getOnlinePlayers().forEach(online -> online.showPlayer(player));

                    player.getInventory().setItem(slot, new ItemCreator(Material.INK_SACK).setDurability(10).setDisplayName("&6You are not vanished").create());
                } else if (durability == 10) {
                    Bukkit.getOnlinePlayers().forEach(online -> {
                        online.hidePlayer(player);
                        player.showPlayer(online);

                        if (online.hasPermission("litebans.tempban")) {
                            online.showPlayer(player);
                        }
                    });

                    player.getInventory().setItem(slot, new ItemCreator(Material.INK_SACK).setDurability(8).setDisplayName("&6You are vanished").create());
                }
            }
        }
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getInventory() != null && event.getCurrentItem() != null) {
            if (event.getInventory().getName() != null) {
                if (event.getInventory().getName().equalsIgnoreCase(ChatColor.GOLD + "Online Staff")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getGameManager().getModerators().contains(player)) {
            if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.BOOK && event.getRightClicked() instanceof Player) {
                //player.openInventory(new CustomPlayerInventory((Player) event.getRightClicked()).getBukkitInventory());
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
            write(event.getPlayer(), "has dropped " + WordUtils.capitalizeFully(event.getItemDrop().getType().name()) + (event.getItemDrop().getItemStack().getItemMeta() != null ? " (name: " + event.getItemDrop().getItemStack().getItemMeta().getDisplayName() + ")" : ""));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommandEvent(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("litebans.tempban")) {
            write(event.getPlayer(), "has executed '" + event.getMessage() + "'. ");
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
            File file = new File("antiabuse/" +
                    player.getName() + "/" + player.getName() + ".txt");
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
        return "[" + format.format(date) + "] " + message;
    }
}
