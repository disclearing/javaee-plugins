package me.javaee.meetup.border;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.WeakHashMap;

public class GlitchPreventListener implements Listener {
    private WeakHashMap<Player, Location> lastValidLocation;
    private BorderManager manager;

    public GlitchPreventListener(BorderManager manager) {
        this.lastValidLocation = new WeakHashMap<>();
        this.manager = manager;
        new BukkitRunnable() {
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final List<Location> blocks = manager.getBlocks(player);
                    if (blocks != null) {
                        final Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                        Block glassBlock = null;
                        boolean isGlitching = false;
                        if (blocks.contains(block.getLocation())) {
                            glassBlock = block;
                            isGlitching = true;
                        } else if (blocks.contains(block.getRelative(BlockFace.NORTH).getLocation())) {
                            glassBlock = block.getRelative(BlockFace.NORTH);
                            isGlitching = true;
                        } else if (blocks.contains(block.getRelative(BlockFace.SOUTH).getLocation())) {
                            glassBlock = block.getRelative(BlockFace.SOUTH);
                            isGlitching = true;
                        } else if (blocks.contains(block.getRelative(BlockFace.EAST).getLocation())) {
                            glassBlock = block.getRelative(BlockFace.EAST);
                            isGlitching = true;
                        } else if (blocks.contains(block.getRelative(BlockFace.WEST).getLocation())) {
                            glassBlock = block.getRelative(BlockFace.WEST);
                            isGlitching = true;
                        }
                        if (isGlitching && player.getFallDistance() == 0.0f && player.isOnGround() && block.getType() == Material.AIR) {
                            if (!glassBlock.getRelative(BlockFace.NORTH).isEmpty() || !glassBlock.getRelative(BlockFace.SOUTH).isEmpty() || !glassBlock.getRelative(BlockFace.EAST).isEmpty() || !glassBlock.getRelative(BlockFace.WEST).isEmpty() || !glassBlock.getRelative(BlockFace.NORTH_EAST).isEmpty() || !glassBlock.getRelative(BlockFace.SOUTH_EAST).isEmpty() || !glassBlock.getRelative(BlockFace.SOUTH_WEST).isEmpty() || !glassBlock.getRelative(BlockFace.NORTH_WEST).isEmpty() || !GlitchPreventListener.this.lastValidLocation.containsKey(player)) {
                                continue;
                            }
                            player.teleport(GlitchPreventListener.this.lastValidLocation.get(player));
                        } else {
                            if (isGlitching || block.getType() == Material.AIR || player.getFallDistance() != 0.0f) {
                                continue;
                            }
                            GlitchPreventListener.this.lastValidLocation.put(player, player.getLocation());
                        }
                    }
                }
            }
        }.runTaskTimer(manager.getPlugin(), 1L, 1L);
    }
}
