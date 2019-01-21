package me.redis.practice.listeners;

import me.redis.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldListener implements Listener {
    public WorldListener() {
        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());

        Bukkit.getScheduler().runTaskTimer(Practice.getPlugin(), () -> {
            Bukkit.getWorlds().forEach(world -> world.setTime(6000));
        }, 0L, 20L * 5);
    }

    @EventHandler
    public void onThunder(ThunderChangeEvent event) {
        if (event.toThunderState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.getEntity().remove();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location newLocation = event.getTo();

            newLocation.setX(event.getTo().getX() + 0.5D);
            newLocation.setZ(event.getTo().getZ() + 0.5D);

            event.setTo(newLocation);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!((player.hasPermission("practice.admin") || player.isOp()) && player.getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!((player.hasPermission("practice.admin") || player.isOp()) && player.getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }
}