package me.javaee.uhc.biome;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.commands.WorldLoaderCommand;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;

public class WorldManager {
    private UHC uhc = UHC.getInstance();

    public void createWorld(String worldName) {
        /*BiomeSwap biomeSwap = new BiomeSwap();
        biomeSwap.startWorldGen();*/
        World world = Bukkit.createWorld(new WorldCreator(worldName).environment(World.Environment.NORMAL));
        world.setDifficulty(Difficulty.HARD);
        Bukkit.getServer().getWorld(worldName);
        world.setGameRuleValue("naturalRegeneration", "false");

        setChunkBiome(world.getChunkAt(0, 0), Biome.PLAINS);
    }

    public void setChunkBiome(Chunk chunk, Biome biome) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Block block = chunk.getBlock(x, 0, z);

                block.setBiome(biome);
            }
        }
    }

    public void createNetherWorld() {
        World localWorld2 = Bukkit.getServer().createWorld(new org.bukkit.WorldCreator("world_nether").environment(World.Environment.NETHER).type(WorldType.NORMAL));
        localWorld2.setDifficulty(Difficulty.HARD);
        Bukkit.getServer().getWorld("world_nether");
        localWorld2.setGameRuleValue("naturalRegeneration", "false");
    }

    public void loadWorld(final World world, final int radius, final int speed) {
        final String name = world.getName();
        WorldLoaderCommand.generating = true;

        new BukkitRunnable() {
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + name + " set " + radius + " " + radius + " 0 0");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + name + " fill " + speed);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + name + " fill confirm");
            }
        }.runTaskLater(this.uhc, 20 * 2);
    }

    public void prepareSpawn() {
        int[] i = {0};
        final Queue<Location> locationQueue = new ArrayDeque();
        final Queue<Material> materialQueue = new ArrayDeque();
        final World world = Bukkit.getWorld("world");
        Location max = new Location(world, 125.0D, 160.0D, 125.0D);
        Location min = new Location(world, -125.0D, 50.0D, -125.0D);
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    if (block.getType() == Material.GRASS) {
                        locationQueue.add(block.getLocation());
                        materialQueue.add(Material.GRASS);
                    } else if ((block.getType() == Material.LOG) || (block.getType() == Material.LOG_2) || (block.getType() == Material.LEAVES) || (block.getType() == Material.LEAVES_2) || (block.getType() == Material.VINE) || (block.getType() == Material.SNOW) || (block.getType() == Material.DOUBLE_PLANT) || (block.getType() == Material.YELLOW_FLOWER) || (block.getType() == Material.RED_MUSHROOM) || (block.getType() == Material.BROWN_MUSHROOM)) {
                        locationQueue.add(block.getLocation());
                        materialQueue.add(Material.AIR);
                    }
                }
            }
        }
        new BukkitRunnable() {
            public void run() {
                for (int x = 0; x < 150; x++) {
                    if (!locationQueue.isEmpty()) {
                        Location location =  locationQueue.poll();
                        Material material = materialQueue.poll();
                        if (material == Material.GRASS) {
                            int rand = (int) (Math.random() * 2.0D + 1.0D);
                            int data;
                            if (rand == 1) {
                                data = 1;
                            } else {
                                data = 4;
                            }
                            location.getBlock().setType(Material.GRASS);
                            location.getBlock().setData((byte) data);
                        } else {
                            location.getBlock().setType(Material.AIR);
                        }
                    } else {
                        cancel();
                    }
                }
            }
        }.runTaskTimer(this.uhc, 0L, 1L);
    }

    public void clearWater() {
        int[] i = {0};
        final Queue<Location> locationQueue = new ArrayDeque();
        final Queue<Material> materialQueue = new ArrayDeque();

        final World world = Bukkit.getWorld("world");
        Location max = new Location(world, 150.0D, 160.0D, 150.0D);
        Location min = new Location(world, -150.0D, 50.0D, -150.0D);

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    if (block.getType() == Material.GRASS) {
                        locationQueue.add(block.getLocation());
                        materialQueue.add(Material.GRASS);
                    } else if (block.getType() == Material.STATIONARY_WATER || block.getType() == Material.WATER) {
                        locationQueue.add(block.getLocation());
                        materialQueue.add(Material.GRASS);
                    }
                }
            }
        }
        new BukkitRunnable() {
            public void run() {
                for (int x = 0; x < 150; x++) {
                    if (!locationQueue.isEmpty()) {
                        Location location =  locationQueue.poll();
                        Material material = materialQueue.poll();
                        if (material == Material.GRASS) {
                            int rand = (int) (Math.random() * 2.0D + 1.0D);
                            int data;

                            if (rand == 1) {
                                data = 1;
                            } else {
                                data = 4;
                            }

                            location.getBlock().setType(Material.GRASS);
                            location.getBlock().setData((byte) data);
                        } else {
                            location.getBlock().setType(Material.GRASS);
                        }
                    } else {
                        cancel();
                    }
                }
            }
        }.runTaskTimer(this.uhc, 0L, 1L);
    }
}