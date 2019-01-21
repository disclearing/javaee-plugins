package me.redis.kohi.border;

import lombok.Getter;
import lombok.Setter;
import me.redis.kohi.SurvivalGames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class BorderManager {
    public static int EXTRA_BEDROCK_BORDER_HEIGHT = 5;
    @Getter @Setter private int radius;
    @Getter @Setter private int seconds = 60;
    @Getter @Setter private boolean started = false;

    private static Set<Material> passThroughMaterials = new HashSet<>();

    public BorderManager() {
        passThroughMaterials.add(Material.LOG);
        passThroughMaterials.add(Material.LEAVES);
        passThroughMaterials.add(Material.LEAVES_2);
        passThroughMaterials.add(Material.LOG_2);
        passThroughMaterials.add(Material.GRASS);
        passThroughMaterials.add(Material.LONG_GRASS);
        passThroughMaterials.add(Material.WATER);
        passThroughMaterials.add(Material.STATIONARY_WATER);
        passThroughMaterials.add(Material.AIR);
    }

    public void setCurrentRadius(int radius) {
        SurvivalGames.getPlugin().getServer().dispatchCommand(SurvivalGames.getPlugin().getServer().getConsoleSender(), "wb world setcorners -" + radius + " -" + radius + " " + radius + " " + radius);

        Bukkit.broadcastMessage(ChatColor.GOLD + "The world border radius is now " + radius);
        addBedrockBorder(radius, EXTRA_BEDROCK_BORDER_HEIGHT);
        setRadius(radius);
    }

    private static void figureOutBlockToMakeBedrock(int x, int z) {
        Block block = SurvivalGames.getPlugin().getServer().getWorld("world").getHighestBlockAt(x, z);
        Block below = block.getRelative(BlockFace.DOWN);

        while (passThroughMaterials.contains(below.getType()) && below.getY() > 1) {
            below = below.getRelative(BlockFace.DOWN);
        }

        below.getRelative(BlockFace.UP).setType(Material.BEDROCK);
    }

    private static void figureOutBlockToMakeIron(int x, int z) {
        Block block = SurvivalGames.getPlugin().getServer().getWorld("world").getHighestBlockAt(x, z);
        Block below = block.getRelative(BlockFace.DOWN);

        while (passThroughMaterials.contains(below.getType()) && below.getY() > 1) {
            below = below.getRelative(BlockFace.DOWN);
        }

        below.getRelative(BlockFace.UP).setType(Material.IRON_FENCE);
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
                        figureOutBlockToMakeBedrock(x, z);
                        figureOutBlockToMakeIron(x, z);
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
                        figureOutBlockToMakeBedrock(x, z);
                        figureOutBlockToMakeIron(x, z);
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

                        figureOutBlockToMakeBedrock(x, z);
                        figureOutBlockToMakeIron(x, z);
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

                    figureOutBlockToMakeBedrock(x, z);
                    figureOutBlockToMakeIron(x, z);
                }

                if (counter >= radius) {
                    cancel();
                }
            }
        }.runTaskTimer(SurvivalGames.getPlugin(), 0, 5);
    }

    public static void addIronBorder(final int radius) {
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
                        figureOutBlockToMakeIron(x, z);
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
                        figureOutBlockToMakeIron(x, z);
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

                        figureOutBlockToMakeIron(x, z);
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

                    figureOutBlockToMakeIron(x, z);
                }

                if (counter >= radius) {
                    cancel();
                }
            }
        }.runTaskTimer(SurvivalGames.getPlugin(), 0, 5);
    }

    public static void addBedrockBorder(final int radius, int blocksHigh) {
        for (int i = 0; i < blocksHigh; i++) {
            new BukkitRunnable() {
                public void run() {
                    addBedrockBorder(radius);
                }
            }.runTaskLater(SurvivalGames.getPlugin(), i);
        }
    }
}
