package me.javaee.uhc.listeners.misc;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.javaee.uhc.UHC;
import me.javaee.uhc.cave.BetterCaves;
import me.javaee.uhc.cave.RandomCaves;
import me.javaee.uhc.command.commands.WorldLoaderCommand;
import me.javaee.uhc.utils.ReflectionUtil;
import net.badlion.worldborder.WorldFillerTaskCompleteEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WorldGenerationListener implements Listener {
    public static boolean isGenerating = false;
    private static Set<Material> passThroughMaterials = new HashSet<>();

    public WorldGenerationListener() {
        WorldGenerationListener.passThroughMaterials.add(Material.LOG);
        WorldGenerationListener.passThroughMaterials.add(Material.LEAVES);
        WorldGenerationListener.passThroughMaterials.add(Material.LEAVES_2);
        WorldGenerationListener.passThroughMaterials.add(Material.LOG_2);
        WorldGenerationListener.passThroughMaterials.add(Material.AIR);
    }

    @EventHandler
    public void onWorldCompleteGeneration(WorldFillerTaskCompleteEvent event) {
        int radius = UHC.getInstance().getWorldBorder().GetWorldBorder("world").getRadiusX();
        WorldGenerationListener.isGenerating = true;
        WorldGenerationListener.addBedrockBorder(radius);
    }

    public static void addRedGlassBorder(final int radius) {
        EditSession es = new EditSession(new BukkitWorld(Bukkit.getWorld("world")), 2147483647);

        try {
            com.sk89q.worldedit.Vector v1 = new com.sk89q.worldedit.Vector(-1 * radius, 0, -1 * radius);
            com.sk89q.worldedit.Vector v2 = new com.sk89q.worldedit.Vector(radius, 100, radius);
            es.makeCuboidWalls(new CuboidRegion(v1, v2), new SingleBlockPattern(new BaseBlock(95, 14)));
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    public static void addBedrockBorder(final int radius, int blocksHigh) {
        for (int i = 0; i < blocksHigh; i++) {
            new BukkitRunnable() {
                public void run() {
                    WorldGenerationListener.addBedrockBorder(radius);
                }
            }.runTaskLater(UHC.getInstance(), i);
        }
    }

    private static void figureOutBlockToMakeBedrock(int x, int z) {
        Block block = UHC.getInstance().getServer().getWorld("world").getHighestBlockAt(x, z);
        Block below = block.getRelative(BlockFace.DOWN);

        while (WorldGenerationListener.passThroughMaterials.contains(below.getType()) && below.getY() > 1) {
            below = below.getRelative(BlockFace.DOWN);
        }

        below.getRelative(BlockFace.UP).setType(Material.BEDROCK);
    }

    public static void addBedrockBorder(final int radius) {
        new BukkitRunnable() {
            private int counter = -radius - 1;
            private boolean phase1 = false;
            private boolean phase2 = false;
            private boolean phase3 = false;

            @Override
            public void run() {
                if (!phase1) {
                    int maxCounter = counter + 500;
                    int x = -radius - 1;
                    for (int z = counter; z <= radius && counter <= maxCounter; z++, counter++) {
                        WorldGenerationListener.figureOutBlockToMakeBedrock(x, z);
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase1 = true;
                    }

                    return;
                }

                if (!phase2) {
                    int maxCounter = counter + 500;
                    int x = radius;
                    for (int z = counter; z <= radius && counter <= maxCounter; z++, counter++) {
                        WorldGenerationListener.figureOutBlockToMakeBedrock(x, z);
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase2 = true;
                    }

                    return;
                }

                if (!phase3) {
                    int maxCounter = counter + 500;
                    int z = -radius - 1;
                    for (int x = counter; x <= radius && counter <= maxCounter; x++, counter++) {
                        if (x == radius || x == -radius - 1) {
                            continue;
                        }

                        WorldGenerationListener.figureOutBlockToMakeBedrock(x, z);
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase3 = true;
                    }

                    return;
                }


                int maxCounter = counter + 500;
                int z = radius;
                for (int x = counter; x <= radius && counter <= maxCounter; x++, counter++) {
                    if (x == radius || x == -radius - 1) {
                        continue;
                    }

                    WorldGenerationListener.figureOutBlockToMakeBedrock(x, z);
                }

                if (counter >= radius) {
                    cancel();
                }
            }
        }.runTaskTimer(UHC.getInstance(), 0, 5);
    }

    @EventHandler
    public void onFillFinish(WorldFillerTaskCompleteEvent event) {
        WorldLoaderCommand.generating = false;

        if (event.getWorldName().equalsIgnoreCase("world")) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&fWorld &6has been successfully generated."));
        }

        UHC.getInstance().getConfigurator().updateOption("MAPGENERATED", true);
        UHC.getInstance().getGenerateSpawnsCommandHandler().setScatterPoints();
        for (int i = 0; i < 500; i++) {
            UHC.getInstance().getGenerateSpawnsCommandHandler().addLocation();

            if (i >= 499) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&aSpawn locations loaded."));
            }
        }

        if (UHC.getInstance().getServerInfo().isWorldGenerated()) {
            UHC.getInstance().getServerInfo().setWorldGenerated(false);
            UHC.getInstance().getServerInfo().save();
            Bukkit.broadcastMessage(ChatColor.GREEN + "Memory flushed and server restarted.");
            return;
        }

        UHC.getInstance().getServerInfo().setWorldGenerated(true);
        UHC.getInstance().getServerInfo().save();

        Bukkit.broadcastMessage(ChatColor.RED + "Rebooting to increase the performance and flush ram in 5 seconds.");

        new BukkitRunnable() {
            public void run() {
                Bukkit.shutdown();
            }
        }.runTaskLater(UHC.getInstance(), 20 * 5);
    }

    /*@EventHandler
    public void onInit(WorldInitEvent event) {
        if (event.getWorld().getName().equalsIgnoreCase("world")) {
            event.getWorld().getPopulators().add(new BetterCaves());
        }
    }

    @EventHandler
    public void fromToHandler(BlockFromToEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.STATIONARY_WATER || block.getType() == Material.STATIONARY_LAVA) {
            boolean continuousFlowMode = isContinuousFlowMode(event.getBlock().getWorld());

            if (continuousFlowMode) {
                Chunk chunk = block.getChunk();
                RandomCaves r = new RandomCaves(chunk);

                if (r.isInGiantCave(block.getX(), block.getY(), block.getZ()) && block.getData() == 0) {
                    event.setCancelled(true);
                }
            }
        }
    }*/

    private boolean isContinuousFlowMode(World world) {
        Object handle = ReflectionUtil.getProtectedValue(world, "world");
        Object d = ReflectionUtil.getProtectedValue(handle, "d");

        return (Boolean) d;
    }
}