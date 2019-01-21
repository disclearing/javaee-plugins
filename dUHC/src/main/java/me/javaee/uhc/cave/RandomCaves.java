package me.javaee.uhc.cave;

import me.javaee.uhc.UHC;
import org.bukkit.Chunk;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

/**
 * Taken from the GiantCaves plugin
 */


public class RandomCaves {
    public Chunk chunk;
    private final double f1xz;
    private final double f1y;
    private final double subtractForLessThanCutoff;
    private final int caveBandBuffer;
    private final NoiseGenerator noiseGen1;
    private final NoiseGenerator noiseGen2;
    private final NoiseGenerator noiseGen3;

    public RandomCaves(Chunk chunk) {
        this.chunk = chunk;
        this.subtractForLessThanCutoff = (100 - 25);
        this.f1xz = (1.0D / 16);
        this.f1y = (1.0D / 9);
        this.caveBandBuffer = 16;
        this.noiseGen1 = new SimplexNoiseGenerator(chunk.getWorld());
        this.noiseGen2 = new SimplexNoiseGenerator((long) this.noiseGen1.noise(chunk.getX(), chunk.getZ()));
        this.noiseGen3 = new SimplexNoiseGenerator((long) this.noiseGen1.noise(chunk.getX(), chunk.getZ()));
    }

    public boolean isInGiantCave(int x, int y, int z) {
        double xx = this.chunk.getX() << 4 | x & 0xF;
        double zz = this.chunk.getZ() << 4 | z & 0xF;

        double n1 = this.noiseGen1.noise(xx * this.f1xz, y * this.f1y, zz * this.f1xz) * 100.0D;
        double n2 = this.noiseGen2.noise(xx * 0.25D, y * 0.05D, zz * 0.25D) * 2.0D;
        double n3 = this.noiseGen3.noise(xx * 0.025D, y * 0.005D, zz * 0.025D) * 20.0D;
        double lc = linearCutoffCoefficient(y);

        return n1 + n2 - n3 - lc > 55;
    }

    private double linearCutoffCoefficient(int y) {
        if (y < 6 || y > 55) {
            return subtractForLessThanCutoff;
        }

        if (y <= 6 + caveBandBuffer) {
            double yy = y - 6;
            return - subtractForLessThanCutoff / caveBandBuffer * yy + subtractForLessThanCutoff;
        }

        if (y >= 55 - caveBandBuffer) {
            double yy = y - 55 + caveBandBuffer;
            return subtractForLessThanCutoff / caveBandBuffer * yy;
        }
        return 0.0D;
    }
}