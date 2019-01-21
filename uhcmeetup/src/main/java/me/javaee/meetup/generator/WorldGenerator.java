package me.javaee.meetup.generator;

import me.javaee.meetup.Meetup;
import me.javaee.meetup.enums.GameState;
import me.javaee.meetup.utils.ScatterUtils;
import net.badlion.worldborder.WorldFillerTaskCompleteEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class WorldGenerator implements Listener {
    @EventHandler
    public void onWorldFinishGeneration(WorldFillerTaskCompleteEvent event) {
        Bukkit.getWorld("world").setSpawnLocation(0, 80, 0);
        ArrayList<Location> locations = ScatterUtils.randomSquareScatter(150, 110, 2);
        Meetup.getPlugin().getSpawnsHandler().setScatterPoints(locations);

        Meetup.getPlugin().getBorderShrinkTask().setCurrentRadius(125);
        prepareSpawn();
        Meetup.getPlugin().getGameManager().setCanJoin(true);
        Meetup.getPlugin().getGameManager().setGameState(GameState.WAITING);
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
                    } else if ((block.getType() == Material.getMaterial(175))) {
                        locationQueue.add(block.getLocation());
                        materialQueue.add(Material.AIR);
                    }
                }
            }
        }
        final int blocks = locationQueue.size();
        new BukkitRunnable() {
            public void run() {
                for (int x = 0; x < 150; x++) {
                    if (!locationQueue.isEmpty()) {
                        Location location = (Location) locationQueue.poll();
                        Material material = (Material) materialQueue.poll();
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
        }.runTaskTimer(Meetup.getPlugin(), 0L, 1L);
    }

    private void addFirstBorder() {
        new BukkitRunnable() {
            private World world = Bukkit.getServer().getWorld("world");

            private int counter = -100 - 1;
            private boolean phase1 = false;
            private boolean phase2 = false;
            private boolean phase3 = false;

            @Override
            public void run() {
                if (!this.phase1) {
                    int maxCounter = this.counter + 500;
                    int x = -100 - 1;
                    for (int z = this.counter; z <= 100 && this.counter <= maxCounter; z++, this.counter++) {
                        Block highestBlock = this.world.getHighestBlockAt(x, z);

                        // Ignore non-solid blocks
                        while (!highestBlock.getType().isSolid() || highestBlock.getType() == Material.LEAVES || highestBlock.getType() == Material.LEAVES_2) {
                            highestBlock = highestBlock.getRelative(0, -1, 0);
                        }

                        int y = highestBlock.getY() + 1;
                        for (int i = y; i < 200; i++) {
                            Block block = this.world.getBlockAt(x, i, z);

                            block.setType(Material.GLASS);
                            block.setData((byte) 0);
                        }
                    }

                    if (this.counter >= 100) {
                        this.counter = -100 - 1;
                        this.phase1 = true;
                    }

                    return;
                }

                if (!this.phase2) {
                    int maxCounter = this.counter + 500;
                    int x = 100;
                    for (int z = this.counter; z <= 100 && this.counter <= maxCounter; z++, this.counter++) {
                        Block highestBlock = this.world.getHighestBlockAt(x, z);

                        // Ignore non-solid blocks
                        while (!highestBlock.getType().isSolid() || highestBlock.getType() == Material.LEAVES || highestBlock.getType() == Material.LEAVES_2) {
                            highestBlock = highestBlock.getRelative(0, -1, 0);
                        }

                        int y = highestBlock.getY() + 1;
                        for (int i = y; i < 80; i++) {
                            Block block = this.world.getBlockAt(x, i, z);

                            block.setType(Material.BEDROCK);
                            block.setData((byte) 0);
                        }
                    }

                    if (this.counter >= 100) {
                        this.counter = -100 - 1;
                        this.phase2 = true;
                    }

                    return;
                }

                if (!this.phase3) {
                    int maxCounter = this.counter + 500;
                    int z = -100 - 1;
                    for (int x = this.counter; x <= 100 && this.counter <= maxCounter; x++, this.counter++) {
                        if (x == 100 || x == -100 - 1) {
                            continue;
                        }

                        Block highestBlock = this.world.getHighestBlockAt(x, z);

                        // Ignore non-solid blocks
                        while (!highestBlock.getType().isSolid() || highestBlock.getType() == Material.LEAVES || highestBlock.getType() == Material.LEAVES_2) {
                            highestBlock = highestBlock.getRelative(0, -1, 0);
                        }

                        int y = highestBlock.getY() + 1;
                        for (int i = y; i < 200; i++) {
                            Block block = this.world.getBlockAt(x, i, z);

                            block.setType(Material.GLASS);
                            block.setData((byte) 0);
                        }
                    }

                    if (this.counter >= 100) {
                        this.counter = -100 - 1;
                        this.phase3 = true;
                    }

                    return;
                }


                int maxCounter = this.counter + 500;
                int z = 100;
                for (int x = this.counter; x <= 100 && this.counter <= maxCounter; x++, this.counter++) {
                    if (x == 100 || x == -100 - 1) {
                        continue;
                    }

                    Block highestBlock = this.world.getHighestBlockAt(x, z);

                    // Ignore non-solid blocks
                    while (!highestBlock.getType().isSolid() || highestBlock.getType() == Material.LEAVES || highestBlock.getType() == Material.LEAVES_2) {
                        highestBlock = highestBlock.getRelative(0, -1, 0);
                    }

                    int y = highestBlock.getY() + 1;
                    for (int i = y; i < 200; i++) {
                        Block block = this.world.getBlockAt(x, i, z);

                        block.setType(Material.GLASS);
                        block.setData((byte) 0);
                    }
                }

                if (this.counter >= 100) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Meetup.getPlugin(), 0L, 1L);
    }

    public void swapBiomes() {
        // Swap all biomes with other biomes
        Bukkit.getServer().setBiomeBase(Biome.OCEAN, Biome.FOREST, 0);
        Bukkit.getServer().setBiomeBase(Biome.RIVER, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.BEACH, Biome.TAIGA, 0);
        Bukkit.getServer().setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.JUNGLE_HILLS, Biome.TAIGA, 0);
        Bukkit.getServer().setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 0);
        Bukkit.getServer().setBiomeBase(Biome.DEEP_OCEAN, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.FOREST, 0);

        // Weird sub-biomes
        Bukkit.getServer().setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 128);
        Bukkit.getServer().setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 128);
        Bukkit.getServer().setBiomeBase(Biome.SAVANNA, Biome.SAVANNA, 128);
        Bukkit.getServer().setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.DESERT, 128);

        // LIMITED threshold biomes
        Bukkit.getServer().setBiomeBase(Biome.FOREST_HILLS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.BIRCH_FOREST_HILLS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.BIRCH_FOREST_HILLS, Biome.PLAINS, 128);
        Bukkit.getServer().setBiomeBase(Biome.BIRCH_FOREST_HILLS_MOUNTAINS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.BIRCH_FOREST_MOUNTAINS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.TAIGA, Biome.ICE_PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.TAIGA, Biome.DESERT, 128);
        Bukkit.getServer().setBiomeBase(Biome.TAIGA_HILLS, Biome.MESA, 0);
        Bukkit.getServer().setBiomeBase(Biome.TAIGA_MOUNTAINS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.ICE_PLAINS_SPIKES, Biome.BIRCH_FOREST, 0);
        Bukkit.getServer().setBiomeBase(Biome.MEGA_SPRUCE_TAIGA, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.MEGA_SPRUCE_TAIGA_HILLS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.MEGA_TAIGA, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.MEGA_TAIGA, Biome.PLAINS, 128);
        Bukkit.getServer().setBiomeBase(Biome.MEGA_TAIGA_HILLS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.COLD_BEACH, Biome.DESERT, 0);
        Bukkit.getServer().setBiomeBase(Biome.COLD_TAIGA, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.COLD_TAIGA, Biome.PLAINS, 128);
        Bukkit.getServer().setBiomeBase(Biome.COLD_TAIGA_HILLS, Biome.DESERT, 0);
        Bukkit.getServer().setBiomeBase(Biome.COLD_TAIGA_MOUNTAINS, Biome.DESERT, 0);

        // DISALLOWED threshold biomes
        Bukkit.getServer().setBiomeBase(Biome.ROOFED_FOREST_MOUNTAINS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.ROOFED_FOREST, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.SWAMPLAND, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.SWAMPLAND_MOUNTAINS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.MESA, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.MESA, Biome.PLAINS, 128);
        Bukkit.getServer().setBiomeBase(Biome.MESA_PLATEAU, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.MESA_PLATEAU, Biome.PLAINS, 128);
        Bukkit.getServer().setBiomeBase(Biome.MESA_BRYCE, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.MESA_PLATEAU_FOREST, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.MESA_PLATEAU_MOUNTAINS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.MESA_PLATEAU_FOREST_MOUNTAINS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS, Biome.DESERT, 128);
        Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS_MOUNTAINS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS_PLUS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS_PLUS, Biome.PLAINS, 128);
        Bukkit.getServer().setBiomeBase(Biome.EXTREME_HILLS_PLUS_MOUNTAINS, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.FROZEN_OCEAN, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.FROZEN_RIVER, Biome.PLAINS, 0);
        Bukkit.getServer().setBiomeBase(Biome.ICE_MOUNTAINS, Biome.PLAINS, 0);
    }

    public Map<Pair, Integer> blockYLimits = new HashMap<>();
    public Set<Material> blackListedMaterials = new HashSet<>();

    public void scanArena() {
        // Add blacklisted locations
        this.blackListedMaterials.add(Material.GLASS);
        this.blackListedMaterials.add(Material.AIR);
        this.blackListedMaterials.add(Material.LOG);
        this.blackListedMaterials.add(Material.LOG_2);
        this.blackListedMaterials.add(Material.YELLOW_FLOWER);
        this.blackListedMaterials.add(Material.RED_ROSE);
        this.blackListedMaterials.add(Material.BROWN_MUSHROOM);
        this.blackListedMaterials.add(Material.RED_MUSHROOM);
        this.blackListedMaterials.add(Material.DOUBLE_PLANT);
        this.blackListedMaterials.add(Material.LONG_GRASS);
        this.blackListedMaterials.add(Material.LEAVES);
        this.blackListedMaterials.add(Material.LEAVES_2);

        int y = Bukkit.getWorld("world").getHighestBlockYAt(0, 0) + 15;
        Location location = new Location(Bukkit.getWorld("world"), 0.5, y, 0.5, -359, 0);

        // Find the edges
        int safety = 0;
        Location xMinLoc = location.clone();
        while (safety < 300 && xMinLoc.getBlock().getType() != Material.BEDROCK) {
            ++safety;
            xMinLoc.add(-1, 0, 0);
        }

        safety = 0;
        Location xMaxLoc = location.clone();
        while (safety < 300 && xMaxLoc.getBlock().getType() != Material.BEDROCK) {
            ++safety;
            xMaxLoc.add(1, 0, 0);
        }

        safety = 0;
        Location zMinLoc = location.clone();
        while (safety < 300 && zMinLoc.getBlock().getType() != Material.BEDROCK) {
            ++safety;
            zMinLoc.add(0, 0, -1);
        }

        safety = 0;
        Location zMaxLoc = location.clone();
        while (safety < 300 && zMaxLoc.getBlock().getType() != Material.BEDROCK) {
            ++safety;
            zMaxLoc.add(0, 0, 1);
        }

        // Create our internal corners (corners inside the actual arena [not wool])
        int xMin = xMinLoc.getBlockX() + 1;
        int xMax = xMaxLoc.getBlockX() - 1;
        int zMin = zMinLoc.getBlockZ() + 1;
        int zMax = zMaxLoc.getBlockZ() - 1;

        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                Block block = xMinLoc.getWorld().getHighestBlockAt(x, z);
                Block under = block.getRelative(0, -1, 0);

                // While the under block isn't something we want
                while (this.blackListedMaterials.contains(under.getType()) && under.getY() > 0) {
                    under = under.getRelative(0, -1, 0);
                }

                if (under.getY() == 0) {
                    throw new RuntimeException("Invalid block found " + under.toString());
                }

                Pair pair = Pair.of(under.getX(), under.getZ());
                Integer max = under.getY() + 5;

                this.blockYLimits.put(pair, max);
            }
        }
    }

    public int getMaxBlockYLevel(Location location) {
        Pair pair = Pair.of(location.getBlockX(), location.getBlockZ());

        Integer yLimit = this.blockYLimits.get(pair);

        if (yLimit == null) {
            throw new IllegalArgumentException("Missing block y limit for " + pair);
        }

        return yLimit;
    }
}
