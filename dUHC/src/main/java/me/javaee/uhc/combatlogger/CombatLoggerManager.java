package me.javaee.uhc.combatlogger;

import me.javaee.uhc.UHC;
import me.javaee.uhc.combatlogger.event.CombatLoggerDeathEvent;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.listeners.misc.EndListener;
import me.javaee.uhc.tasks.DisconnectedTask;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.InventorySerialization;
import me.javaee.uhc.utils.ItemBuilder;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CombatLoggerManager implements Listener {

    private final UHC plugin;

    public Map<CombatLogger, Long> combatLoggers = new HashMap<>();
    private Set<UUID> safelyDisconnected = new HashSet<>();

    public CombatLoggerManager(UHC plugin) {
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

    public void removeCombatLoggers() {
        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getEntitiesByClass(Villager.class)) {
                CombatLogger player = getByEntity(entity);

                if (player != null) {
                    Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

                    UHC.getInstance().getGameManager().getAlivePlayers().remove(player.getUniqueId());

                    InventorySerialization.saveInventoryToProfile(profile, player.getContents());
                    InventorySerialization.saveArmorToProfile(profile, player.getArmor());
                    profile.setDeathLocation(player.getEntity().getLocation().getX() + ";" + player.getEntity().getLocation().getZ());
                    profile.setDeaths(profile.getDeaths() + 1);
                    profile.setDead(true);
                    profile.save(true);

                    UHCTeam team = UHCTeam.getByUUID(player.getUniqueId());
                    if (team != null) {
                        int newDtr = team.getDtr() - 1;

                        team.setDtr(newDtr);

                        if (team.getDtr() < 1) {
                            UHC.getInstance().getTeams().remove(team);
                        }
                    }

                    player.getEntity().remove();
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&c" + player.getEntity().getCustomName() + "&7[&f" + ProfileUtils.getInstance().getProfile(player.getUniqueId()).getMatchKills() + "&7] (Disconnected) &eremoved."));

                    player.getEntity().remove();
                    combatLoggers.remove(player);
                }
            }
        }
    }

    public void removeCombatLogger(Player player) {
        if (getByPlayer(player) != null) {
            getByPlayer(player).getEntity().remove();
        }
    }

    public void removeCombatLogger(LivingEntity entity) {
        if (getByEntity(entity) != null) {
            CombatLogger combatLogger = getByEntity(entity);
            Profile profile = ProfileUtils.getInstance().getProfile(combatLogger.getUniqueId());

            UHC.getInstance().getGameManager().getAlivePlayers().remove(combatLogger.getUniqueId());

            InventorySerialization.saveInventoryToProfile(profile, combatLogger.getContents());
            InventorySerialization.saveArmorToProfile(profile, combatLogger.getArmor());
            profile.setDeathLocation(combatLogger.getEntity().getLocation().getX() + ";" + combatLogger.getEntity().getLocation().getZ());
            profile.setDeaths(profile.getDeaths() + 1);
            profile.setDead(true);
            profile.save(true);

            UHCTeam team = UHCTeam.getByUUID(combatLogger.getUniqueId());
            if (team != null) {
                int newDtr = team.getDtr() - 1;

                team.setDtr(newDtr);

                if (team.getDtr() < 1) {
                    UHC.getInstance().getTeams().remove(team);
                }
            }

            combatLogger.getEntity().remove();
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&c" + combatLogger.getEntity().getCustomName() + "&7[&f" + ProfileUtils.getInstance().getProfile(combatLogger.getUniqueId()).getMatchKills() + "&7] (Disconnected) &edidn't relog back in time."));
            combatLoggers.remove(combatLogger);
        }
    }

    public void safelyDisconnect(Player player, String reason) {
        safelyDisconnected.add(player.getUniqueId());
        player.kickPlayer(reason);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getGameManager().getGameState() != GameState.INGAME) return;

        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) return;

        if (UHC.getInstance().getGameManager().getModerators().contains(player)) return;

        if (UHC.getInstance().getGameManager().getHost() == player) return;

        if (!UHC.getInstance().getGameManager().getAlivePlayers().contains(player.getUniqueId())) return;

        combatLoggers.put(new CombatLogger(player), System.currentTimeMillis());
        ProfileUtils.getInstance().getProfile(event.getPlayer().getUniqueId()).setOfflineTask(new DisconnectedTask(event.getPlayer()).runTaskLater(UHC.getInstance(), 60 * 20 * 10));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCombatLoggerDeath(CombatLoggerDeathEvent event) {
        CombatLogger logger = event.getCombatLogger();
        Location location = event.getCombatLogger().getEntity().getLocation();

        UUID killed = event.getCombatLogger().getUniqueId();

        UHC.getInstance().getGameManager().getAlivePlayers().remove(killed);

        Profile killedProfile = ProfileUtils.getInstance().getProfile(logger.getUniqueId());
        killedProfile.setDeaths(killedProfile.getDeaths() + 1);
        InventorySerialization.saveInventoryToProfile(killedProfile, logger.getContents());
        InventorySerialization.saveArmorToProfile(killedProfile, logger.getArmor());
        killedProfile.setDeathLocation(logger.getEntity().getLocation().getX() + ";" + logger.getEntity().getLocation().getZ());
        killedProfile.setDead(true);
        killedProfile.save(true);

        UHCTeam team = UHCTeam.getByUUID(killed);
        if (team != null) {
            int newDtr = team.getDtr() - 1;

            team.setDtr(newDtr);

            if (team.getDtr() < 1) {
                UHC.getInstance().getTeams().remove(team);
            }
        }

        if (event.getKiller() != null && event.getKiller() instanceof Player) {
            Player killer = (Player) event.getKiller();

            Profile killerProfile = ProfileUtils.getInstance().getProfile(killer.getUniqueId());
            killerProfile.setKills(killerProfile.getKills() + 1);
            killerProfile.setMatchKills(killerProfile.getMatchKills() + 1);
            killerProfile.save(true);
            UHC.getInstance().getGameManager().getKills().put(killer.getUniqueId(), UHC.getInstance().getGameManager().getKills().get(killer.getUniqueId()) + 1);

            if (Scenario.getByName("Diamondless").isEnabled()) {
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.DIAMOND));
            }

            if (Scenario.getByName("Goldless").isEnabled()) {
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.GOLD_INGOT, 8));
            }

            if (Scenario.getByName("Barebones").isEnabled()) {
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.DIAMOND, 1));
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.GOLDEN_APPLE, 2));
                location.getWorld().dropItem(location, new ItemBuilder(Material.GOLDEN_APPLE).setName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Golden Head").build());
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.ARROW, 32));
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.STRING, 2));
            }

            Bukkit.broadcastMessage(ChatColor.RED + logger.getName() + ChatColor.GRAY + "[" + ChatColor.WHITE + killedProfile.getMatchKills() + ChatColor.GRAY + "] (Disconnected) " + ChatColor.YELLOW + "was slain by " + ChatColor.RED + killer.getName() + ChatColor.GRAY + "[" + ChatColor.WHITE + killerProfile.getMatchKills() + ChatColor.GRAY + "]");
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + logger.getName() + ChatColor.GRAY + "[" + ChatColor.WHITE + killedProfile.getMatchKills() + ChatColor.GRAY + "] (Disconnected) " + ChatColor.YELLOW + "died.");
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

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            CombatLogger logger = getByEntity((LivingEntity) event.getEntity());

            if (logger != null) {
                if (!UHC.getInstance().getGameManager().isPvpEnable()) {
                    event.setCancelled(true);
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