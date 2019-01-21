package me.javaee.uhc.visualise;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.base.Predicate;
import me.javaee.uhc.UHC;
import me.javaee.uhc.utils.cuboid.Cuboid;
import me.javaee.uhc.visualise.protocol.BlockDigAdapter;
import me.javaee.uhc.visualise.protocol.BlockPlaceAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WallBorderListener extends BukkitRunnable implements Listener {

    private final UHC plugin;

    private ConcurrentMap<Player, Location> previous = new ConcurrentHashMap<>();

    public WallBorderListener(UHC plugin) {
        this.plugin = plugin;

        /*ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new BlockDigAdapter(plugin));
        manager.addPacketListener(new BlockPlaceAdapter(plugin));*/

        for (Player player : Bukkit.getOnlinePlayers()) {
            previous.put(player, player.getLocation());
        }

        runTaskTimerAsynchronously(plugin, 5L, 5L);
    }

    @Override
    public void run() {
        for (Map.Entry<Player, Location> entry : previous.entrySet()) {
            Player player = entry.getKey();
            if (player.isOnline()) {
                Location from = entry.getValue();

                Location to = player.getLocation();
                int toX = to.getBlockX();
                int toY = to.getBlockY();
                int toZ = to.getBlockZ();

                if (from.getX() != toX || from.getY() != toY || from.getZ() != toZ) {
                    handlePositionChanged(player, to.getWorld(), toX, toY, toZ);
                    previous.replace(player, player.getLocation());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        previous.remove(player);
        plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        previous.put(player, player.getLocation());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location from = event.getFrom();

        Location to = event.getTo();
        int toX = to.getBlockX();
        int toY = to.getBlockY();
        int toZ = to.getBlockZ();

        if (from.getX() != toX || from.getY() != toY || from.getZ() != toZ) {
            handlePositionChanged(event.getPlayer(), to.getWorld(), toX, toY, toZ);
        }
    }

    private void handlePositionChanged(Player player, World toWorld, int toX, int toY, int toZ) {
        VisualType visualType;
        visualType = VisualType.BORDER;

        // Values used to calculate the new visual cuboid height.
        int minHeight = toY - 4;
        int maxHeight = toY + 5;
        int minX = toX - 7;
        int maxX = toX + 7;
        int minZ = toZ - 7;
        int maxZ = toZ + 7;

        Collection<Vector> edges = new Cuboid(Bukkit.getWorld("world"), UHC.getInstance().getBorderManager().getBorder(), 100, UHC.getInstance().getBorderManager().getBorder(), -UHC.getInstance().getBorderManager().getBorder() - 1, 0, -UHC.getInstance().getBorderManager().getBorder() - 1).edges(); // TODO: Don't use #edges(), find a way just to get for surrounding x and z loop
        for (Vector edge : edges) {
            if (Math.abs(edge.getBlockX() - toX) > 7) {
                continue;
            }
            if (Math.abs(edge.getBlockZ() - toZ) > 7) {
                continue;
            }

            Location location = edge.toLocation(toWorld);
            if (location != null) {
                Location first = location.clone();
                first.setY(minHeight);

                Location second = location.clone();
                second.setY(maxHeight);

                plugin.getVisualiseHandler().generate(player, new Cuboid(first, second), visualType, false).size();
            }
        }

        plugin.getVisualiseHandler().clearVisualBlocks(player, visualType, visualBlock -> {
            Location other = visualBlock.getLocation();
            return other.getWorld().equals(toWorld) && (Math.abs(toX - other.getBlockX()) > 7 || Math.abs(toY - other.getBlockY()) > 5 || Math.abs(toZ - other.getBlockZ()) > 7);
        });
    }
}