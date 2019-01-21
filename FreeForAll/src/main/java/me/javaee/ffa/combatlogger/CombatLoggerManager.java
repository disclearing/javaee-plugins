package me.javaee.ffa.combatlogger;

import me.javaee.ffa.FFA;
import me.javaee.ffa.events.CombatLoggerDeathEvent;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.profiles.status.PlayerStatus;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;

public class CombatLoggerManager implements Listener {
    private final FFA plugin;

    public Map<CombatLogger, Long> combatLoggers = new HashMap<>();
    private Set<UUID> safelyDisconnected = new HashSet<>();

    public CombatLoggerManager(FFA plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public CombatLogger getByEntity(LivingEntity entity) {
        for (CombatLogger logger : combatLoggers.keySet()) {
            if (logger.getEntity().equals(entity)) {
                return logger;
            }
        }
        return null;
    }

    public CombatLogger getByPlayer(Player player) {
        for (CombatLogger logger : combatLoggers.keySet()) {
            if (logger.getUniqueId().equals(player.getUniqueId())) {
                return logger;
            }
        }
        return null;
    }

    public CombatLogger getByName(String name) {
        for (CombatLogger logger : combatLoggers.keySet()) {
            if (logger.getName().equalsIgnoreCase(name)) {
                return logger;
            }
        }
        return null;
    }

    public void removeCombatLogger(Player player) {
        if (getByPlayer(player) != null) {
            getByPlayer(player).getEntity().remove();
        }
    }

    public void safelyDisconnect(Player player, String reason) {
        safelyDisconnected.add(player.getUniqueId());
        player.kickPlayer(reason);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(null);

        if (FFA.getPlugin().getInformationManager().getInformation().getLobbyCuboid().contains(player.getLocation())) return;
        if (FFA.getPlugin().getProfileManager().getProfile(player).getPlayerStatus() == PlayerStatus.STAFF) return;

        combatLoggers.put(new CombatLogger(player), System.currentTimeMillis());

        Bukkit.getScheduler().runTaskLater(FFA.getPlugin(), () -> {
			if (getByName(player.getName()) == null) return;
			
            getByName(player.getName()).getEntity().remove();
        }, 20 * 15L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCombatLoggerDeath(CombatLoggerDeathEvent event) {
        CombatLogger logger = event.getCombatLogger();
        Location location = event.getCombatLogger().getEntity().getLocation();
        UUID killed = event.getCombatLogger().getUniqueId();
        Profile killedProfile = FFA.getPlugin().getProfileManager().getProfile(logger.getUniqueId());
        killedProfile.setDeaths(killedProfile.getDeaths() + 1);
        killedProfile.save();

        if (event.getKiller() != null && event.getKiller() instanceof Player) {
            Player killer = (Player) event.getKiller();

            Profile killerProfile = FFA.getPlugin().getProfileManager().getProfile(killer.getUniqueId());
            killerProfile.setKills(killerProfile.getKills() + 1);
            killerProfile.save();

            Bukkit.broadcastMessage(ChatColor.RED + logger.getName() + ChatColor.GRAY + "[" + ChatColor.WHITE + killedProfile.getKills() + ChatColor.GRAY + "] (Disconnected) " + ChatColor.YELLOW + "was slain by " + ChatColor.RED + killer.getName() + ChatColor.GRAY + "[" + ChatColor.WHITE + killerProfile.getKills() + ChatColor.GRAY + "]");
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + logger.getName() + ChatColor.GRAY + "[" + ChatColor.WHITE + killedProfile.getKills() + ChatColor.GRAY + "] (Disconnected) " + ChatColor.YELLOW + "died.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        CombatLogger logger = getByEntity(event.getEntity());
        if (logger != null) {
            Entity killer = event.getEntity().getKiller();
            CombatLoggerDeathEvent calledEvent = new CombatLoggerDeathEvent(logger, killer);

            Bukkit.getPluginManager().callEvent(calledEvent);

            if (calledEvent.isCancelled()) {
                return;
            }

            for (ItemStack armor : logger.getArmor()) {
                if (armor != null && armor.getType() != Material.AIR) {
                    event.getDrops().add(armor);
                }
            }

            for (ItemStack contents : logger.getContents()) {
                if (contents != null && contents.getType() != Material.AIR) {
                    event.getDrops().add(contents);
                }
            }

            event.getDrops().clear();
            combatLoggers.remove(logger);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof LivingEntity) {
            CombatLogger logger = getByEntity((LivingEntity) event.getRightClicked());
            if (logger != null) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            CombatLogger logger = getByEntity((LivingEntity) event.getEntity());
            if (logger != null) {
                combatLoggers.put(logger, System.currentTimeMillis());

                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setDamage(event.getFinalDamage() * 2);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority=EventPriority.HIGHEST)
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        CombatLogger logger = getByPlayer(player);

        if (logger != null) {
            event.setSpawnLocation(logger.getEntity().getLocation());
            player.setHealth(logger.getEntity().getHealth() / 2);
            logger.getEntity().remove();
            combatLoggers.remove(logger);
        }
    }

    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof LivingEntity) {
                CombatLogger logger = getByEntity((LivingEntity) entity);

                if (logger != null) {
                    event.setCancelled(true);
                }
            }
        }
    }
}