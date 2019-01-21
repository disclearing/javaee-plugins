package me.javaee.uhc.listeners.misc;

import me.javaee.uhc.UHC;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.team.UHCTeam;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;
import java.util.Random;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class WaitingListener implements Listener {
    @EventHandler
    public void onSpawnEvent(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Player) return;

        if (event.getLocation().getWorld().getName().equalsIgnoreCase("lobby")) {
            event.setCancelled(true);
            return;
        }

        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            if (event.getEntity() instanceof Cow || event.getEntity() instanceof Chicken || event.getEntity() instanceof Horse)
                event.setCancelled(false);
            else event.setCancelled(true);
        } else if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            if (event.getEntity() instanceof Sheep || event.getEntity() instanceof Pig) {
                if (event.getLocation().getY() > 45) {
                    int random = new Random().nextInt(3) - 1; // 0 1 2

                    if (random == 0 || random == 1) {
                        event.setCancelled(true);
                        event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.COW);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpawnLocation(PlayerSpawnLocationEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            event.setSpawnLocation(new Location(Bukkit.getWorld("lobby"), 0, 105, -18));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        UHC.getInstance().getIgnoredPlayers().put(event.getPlayer().getUniqueId(), new ArrayList<>());

        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            event.getPlayer().kickPlayer("Scattering");
        }

        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            event.getPlayer().getInventory().clear();
            event.getPlayer().getInventory().setArmorContents(null);

            for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
                event.getPlayer().removePotionEffect(potionEffect.getType());
            }

            event.getPlayer().getActivePotionEffects().clear();

            event.getPlayer().sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------");
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes("&6Welcome &f" + event.getPlayer().getName() + "&6 to the &f#" + UHC.getInstance().getUhcNumber() + " UHC" + ChatColor.GOLD + "."));
            event.getPlayer().sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------");

            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            event.getPlayer().setHealth(20);
            event.getPlayer().setFoodLevel(20);

            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.hidePlayer(event.getPlayer(), false);
                    event.getPlayer().hidePlayer(player, false);
                }
            }, 2L);

            Player player = event.getPlayer();

            if (player.hasPermission("group.aurax") || player.hasPermission("group.janus")) {
                UHC.getInstance().getWhitelisted().put(event.getPlayer().getUniqueId(), 1);
            } else if (player.hasPermission("group.panom")) {
                UHC.getInstance().getWhitelisted().put(event.getPlayer().getUniqueId(), 2);
            } else if (player.hasPermission("group.tirred") || player.hasPermission("group.zilo")) {
                UHC.getInstance().getWhitelisted().put(event.getPlayer().getUniqueId(), 3);
            } else if (player.hasPermission("group.selix")) {
                UHC.getInstance().getWhitelisted().put(event.getPlayer().getUniqueId(), 4);
            }

            Profile profile = ProfileUtils.getInstance().getProfile(event.getPlayer().getUniqueId());

            if (profile.isDead()) {
                profile.setDead(false);
            }

            profile.setMatchKills(0);
            profile.setDeathArmor("null");
            profile.setDeathInventory("null");
            profile.setDeathLocation("null");

            profile.save(true);
        }
    }

    public UHCTeam whoHasLessPlayers(UHCTeam team1, UHCTeam team2) {
        if (team1.getPlayerList().size() < team2.getPlayerList().size()) {
            return team1;
        } else {
            return team2;
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase("lobby")) {
            if (event.getPlayer().getLocation().getY() < 0) {
                event.getPlayer().teleport(new Location(Bukkit.getWorld("lobby"), 0, 105, -18));
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            if (event instanceof EntityDamageByEntityEvent) {
                if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player && event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    Player killer = (Player) ((EntityDamageByEntityEvent) event).getDamager();

                    if (!UHC.getInstance().getPracticeManager().getPracticePlayers().contains(player) && !UHC.getInstance().getPracticeManager().getPracticePlayers().contains(killer)) {
                        event.setCancelled(true);
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFire(BlockBurnEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(BlockBreakEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            if (!UHC.getInstance().getConfigurator().getBooleanOption("BED").getValue()) {
                if (event.getBlock().getType() == Material.BED_BLOCK || event.getBlock().getType() == Material.BED) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(PlayerDropItemEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }
   /* @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            autoRespawn(event);
        }
    }*/

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoinPlayerCount(PlayerJoinEvent event) {
        int scatterTime = (int) (0.15 * Bukkit.getOnlinePlayers().size());

        UHC.getInstance().getGameManager().setCountdown(scatterTime);
    }
}
