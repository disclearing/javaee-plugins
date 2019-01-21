package me.redis.kohi.schematic;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.DataException;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.World;

public class SchematicHandler {
    private static Field dataf = null;
    private String name;
    private CuboidClipboard clipBoard;
    private int yOffset;
    private int percentage;

    public SchematicHandler() {
        this.percentage = 2500;
    }

    public String getName() {
        return name;
    }

    public void loadSchmeatic(File file) throws IOException {
        Preconditions.checkState(this.clipBoard == null);

        name = file.getName().replaceAll(".schematic", "");
        SchematicFormat format = SchematicFormat.MCEDIT;

        try {
            this.clipBoard = format.load(file);
        } catch (DataException e) {
            throw new IOException(e);
        }
    }

    public void pasteSchematic(World world, int x, int y, int z) {
        Vector pastePos = new Vector(x, y, z);
        EditSession editSession = newEditSession(world);

        try {
            this.clipBoard.place(editSession, pastePos, true);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    public void rotateRandomly() {
        this.clipBoard.rotate2D(90 * (RandomUtils.nextInt(4) + 1));
    }

    public int getWidth() {
        return this.clipBoard.getWidth();
    }

    public int getLength() {
        return this.clipBoard.getLength();
    }

    public int getHeight() {
        return this.clipBoard.getHeight();
    }

    public int getBlockIDAt(int x, int y, int z) {
        BaseBlock[][][] block = getInternalData(this.clipBoard);

        if (block[x][y][z] == null) {
            throw new IllegalStateException("null at " + x + "," + y + "," + z);
        }

        return block[x][y][z].getId();
    }

    public int getDataAt(int x, int y, int z) {
        BaseBlock[][][] block = getInternalData(this.clipBoard);

        if (block[x][y][z] == null) {
            throw new IllegalStateException("null at " + x + "," + y + "," + z);
        }

        return block[x][y][z].getData();
    }

    private BaseBlock[][][] getInternalData(CuboidClipboard clipboard) {
        try {
            if (dataf == null) {
                try {
                    dataf = CuboidClipboard.class.getDeclaredField("data");
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
                dataf.setAccessible(true);
            }
            return (BaseBlock[][][]) dataf.get(this.clipBoard);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private EditSession newEditSession(World world) {
        return new EditSession(new BukkitWorld(world), 999999);
    }

    public int getYOffset() {
        return this.yOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public int getPercentage() {
        return this.percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
