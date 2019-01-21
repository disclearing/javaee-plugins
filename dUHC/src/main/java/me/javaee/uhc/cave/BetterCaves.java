package me.javaee.uhc.cave;

import me.javaee.uhc.UHC;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class BetterCaves extends BlockPopulator {
    private Material material = Material.AIR;

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        RandomCaves randomCaves = new RandomCaves(chunk);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 55; y >= 6; y--) {
                    if (randomCaves.isInGiantCave(x, y, z)) {
                        Block block = chunk.getBlock(x, y, z);

                        block.setType(this.material);
                    }
                }
            }
        }
    }
}

