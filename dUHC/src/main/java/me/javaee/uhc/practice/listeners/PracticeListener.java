package me.javaee.uhc.practice.listeners;

import me.javaee.uhc.UHC;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class PracticeListener implements Listener {
    @EventHandler
    public void onWaterBucketPlaceEvent(PlayerBucketEmptyEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() != GameState.WAITING) {
            return;
        }

        if (!UHC.getInstance().getPracticeManager().getPracticePlayers().contains(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        event.getPlayer().updateInventory();
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        if (event.getPlayer().getItemInHand().getType() == Material.WATER_BUCKET) {
            block.setType(Material.STATIONARY_WATER);
        }

        runTaskNextTick(() -> block.setType(Material.AIR));
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() != GameState.WAITING) {
            return;
        }

        if (!UHC.getInstance().getPracticeManager().getPracticePlayers().contains(event.getEntity().getPlayer())) {
            return;
        }

        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);
        if (event.getEntity().getKiller() != null) {
            event.getEntity().getKiller().sendMessage(ChatColor.GREEN + "You killed " + event.getEntity().getName());
            UHC.getInstance().getPracticeManager().getKillStreak().put(event.getEntity().getKiller(), UHC.getInstance().getPracticeManager().getKillStreak().get(event.getEntity().getKiller()) + 1);

            event.getEntity().sendMessage(ChatColor.RED + "You were killed by " + event.getEntity().getKiller().getName());
            UHC.getInstance().getPracticeManager().getKillStreak().put(event.getEntity(), 0);
            event.getEntity().getKiller().getInventory().addItem(new ItemBuilder(Material.GOLDEN_APPLE).setName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Golden Head").build());
            event.getEntity().getKiller().getInventory().addItem(new ItemStack(Material.ARROW, 6));

            killStreak(event.getEntity().getKiller());
        } else {
            event.getEntity().sendMessage(ChatColor.RED + "You died!");
            UHC.getInstance().getPracticeManager().getKillStreak().put(event.getEntity(), 0);
        }

        autoRespawn(event);
    }

    private void autoRespawn(PlayerDeathEvent e) {
        new BukkitRunnable(){
            public void run() {
                try {
                    Object nmsPlayer = e.getEntity().getClass().getMethod("getHandle", new Class[0]).invoke((Object)e.getEntity(), new Object[0]);
                    Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
                    Class EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");
                    Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    Object mcserver = minecraftServer.get(con);
                    Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList", new Class[0]).invoke(mcserver, new Object[0]);
                    Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer, Integer.TYPE, Boolean.TYPE);
                    moveToWorld.invoke(playerlist, nmsPlayer, 0, false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater(UHC.getInstance(), 5);
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() != GameState.WAITING) {
            return;
        }
        if (!UHC.getInstance().getPracticeManager().getPracticePlayers().contains(event.getPlayer())) {
            return;
        }

        event.setRespawnLocation(UHC.getInstance().getPracticeManager().getScatterLocation());
        UHC.getInstance().getPracticeManager().getKit().giveItems(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() != GameState.WAITING) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (!UHC.getInstance().getPracticeManager().getPracticePlayers().contains(player)) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (player.getFallDistance() > 25F) {
                event.setCancelled(true);
                return;
            }
        }

        event.setCancelled(false);
    }

    private static void runTaskNextTick(Runnable run) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(UHC.getInstance(), run, 1);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (UHC.getInstance().getPracticeManager().getPracticePlayers().contains(event.getPlayer())) {
            UHC.getInstance().getPracticeManager().getPracticePlayers().remove(event.getPlayer());
        }
    }

    public void killStreak(Player player) {
        Map<Player, Integer> killStreak = UHC.getInstance().getPracticeManager().getKillStreak();

        if (killStreak.get(player) == 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2));
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &bRegeneration II &efor &b5 seconds&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&b" + player.getName() + " &ehas gained 5 seconds of &bRegeneration II&e."));
        } else if (killStreak.get(player) == 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 2));
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &bSpeed I &efor &b10 seconds&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&b" + player.getName() + " &ehas gained 10 seconds of &bSpeed II&e."));
        } else if (killStreak.get(player) == 6) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 1));
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &bStrength &efor &b5 seconds&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&b" + player.getName() + " &ehas gained 5 seconds of &bStrength II&e."));
        } else if (killStreak.get(player) == 8) {
            player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, 1).build());
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have now a &bDiamond Sword&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&b" + player.getName() + " &ehas gained a &bDiamond Sword&e."));
        } else if (killStreak.get(player) == 10) {
            player.getInventory().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have now a &bDiamond Chest&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&b" + player.getName() + " &ehas gained a &bDiamond Chestplate&e."));
        } else if (killStreak.get(player) == 12) {
            player.getInventory().setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have now &bDiamond Pants&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&b" + player.getName() + " &ehas gained &bDiamond Pants&e."));
        } else if (killStreak.get(player) == 14) {
            player.getInventory().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have now &bDiamond Boots&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&b" + player.getName() + " &ehas gained &bDiamond Boots&e."));
        } else if (killStreak.get(player) == 16) {
            player.getInventory().setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have now &bDiamond Helmet&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&b" + player.getName() + " &ehas gained a &bDiamond Helmet&e."));
        }
    }
}
