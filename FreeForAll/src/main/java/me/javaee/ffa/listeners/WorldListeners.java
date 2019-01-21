package me.javaee.ffa.listeners;

import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.utils.LocationUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WorldListeners implements Listener {
    @EventHandler
    public void onFire(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        Information information = FFA.getPlugin().getInformationManager().getInformation();

        if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        if (information.getLobbyCuboid().contains(location)) {
            event.setCancelled(true);
        } else {
            if (event.getBlock().getType() == Material.ANVIL) {
                event.getPlayer().sendMessage(ChatColor.RED + "Your anvil will be destroyed in 15 seconds.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(FFA.getPlugin(), () -> location.getBlock().setType(Material.AIR), 20 * 15L);
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(FFA.getPlugin(), () -> location.getBlock().setType(Material.AIR), 20 * 7L);
            }
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
        Information information = FFA.getPlugin().getInformationManager().getInformation();

        if (event.getBlockClicked() != null && information.getLobbyCuboid().contains(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
        } else {
            if (!information.getLobbyCuboid().contains(event.getBlockClicked().getLocation())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(FFA.getPlugin(), () -> event.getBlockClicked().getRelative(event.getBlockFace()).breakNaturally(), 20 * 5L);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Information information = FFA.getPlugin().getInformationManager().getInformation();

        if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        if (information.getLobbyCuboid().contains(event.getPlayer().getLocation())) {
            event.setCancelled(true);
        }

        if (event.getBlock().getType() != Material.QUARTZ_BLOCK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {

        if (event.toWeatherState()) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();

                if (block.getType() == Material.PORTAL) {
                    event.getPlayer().sendBlockChange(block.getLocation(), Material.AIR, (byte) 0);
                    event.setCancelled(true);
                }
            }
        }
    }
}
