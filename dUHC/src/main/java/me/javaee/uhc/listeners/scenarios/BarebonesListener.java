package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.UHC;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilRepairEvent;
import org.bukkit.event.inventory.PrepareItemRepairAnvilEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class BarebonesListener implements Listener {
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (Scenario.getByName("BareBones").isEnabled()) {
            ItemStack item = event.getCurrentItem();

            if (item.getType() == Material.ENCHANTMENT_TABLE) {
                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You can't craft this. It is barebones!");
                event.setCancelled(true);
            } else if (item.getType() == Material.ANVIL) {
                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You can't craft this. It is barebones!");
                event.setCancelled(true);
            } else if (item.getType() == Material.GOLDEN_APPLE) {
                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You can't craft this. It is barebones!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDie(EntityDeathEvent event) {
        if (Scenario.getByName("BareBones").isEnabled()) {
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

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        if (Scenario.getByName("BareBones").isEnabled()) {
            Location location = event.getEntity().getLocation();

            if (!Scenario.getByName("TimeBomb").isEnabled()) {
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.DIAMOND, 1));
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.GOLDEN_APPLE, 2));
                location.getWorld().dropItem(location, new ItemBuilder(Material.GOLDEN_APPLE).setName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Golden Head").build());
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.ARROW, 32));
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.STRING, 2));
            }
        }
    }

    @EventHandler
    public void onMineBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (Scenario.getByName("BareBones").isEnabled()) {
            if (block.getType().toString().toLowerCase().contains("ORE")) {
                Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
                    UHC.getInstance().getMineManager().handleDatabaseMine(event);
                    UHC.getInstance().getMineManager().handleAlertsMine(event);
                });
            }

            if (block.getType() == Material.GOLD_ORE && (player.getItemInHand() != null && (player.getItemInHand().getType() == Material.DIAMOND_PICKAXE || player.getItemInHand().getType() == Material.IRON_PICKAXE))) {
                event.setCancelled(true);

                block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT));
                createExpOrb(block.getLocation(), 1);
                if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
                    short dur = event.getPlayer().getItemInHand().getDurability();
                    if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
                        player.setItemInHand(null);
                        player.updateInventory();
                    } else {
                        player.getItemInHand().setDurability(dur);
                    }
                }
            } else if (block.getType() == Material.DIAMOND_ORE && (player.getItemInHand() != null && (player.getItemInHand().getType() == Material.DIAMOND_PICKAXE || player.getItemInHand().getType() == Material.IRON_PICKAXE))) {
                event.setCancelled(true);

                block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT));
                createExpOrb(block.getLocation(), 1);
                if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
                    short dur = event.getPlayer().getItemInHand().getDurability();
                    if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
                        player.setItemInHand(null);
                        player.updateInventory();
                    } else {
                        player.getItemInHand().setDurability(dur);
                    }
                }
            } else if (block.getType() == Material.EMERALD_ORE && (player.getItemInHand() != null && (player.getItemInHand().getType() == Material.DIAMOND_PICKAXE || player.getItemInHand().getType() == Material.IRON_PICKAXE))) {
                event.setCancelled(true);

                block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT));
                createExpOrb(block.getLocation(), 1);
                if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
                    short dur = event.getPlayer().getItemInHand().getDurability();
                    if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
                        player.setItemInHand(null);
                        player.updateInventory();
                    } else {
                        player.getItemInHand().setDurability(dur);
                    }
                }
            } else if (block.getType() == Material.IRON_ORE && (player.getItemInHand() != null)) {
                event.setCancelled(true);

                block.setType(Material.AIR);
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT));
                createExpOrb(block.getLocation(), 1);
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

    @EventHandler
    public void onAnvil(PrepareAnvilRepairEvent event) {
        if (Scenario.getByName("BareBones").isEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (Scenario.getByName("BareBones").isEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnchant(PrepareItemEnchantEvent event) {
        if (Scenario.getByName("BareBones").isEnabled()) {
            event.setCancelled(true);
        }
    }

    public void createExpOrb(Location location, int amount) {
        ExperienceOrb experienceOrb = (ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
        experienceOrb.setExperience(amount);
    }
}
