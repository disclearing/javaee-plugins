package me.redis.kohi.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.patterns.BlockChance;
import com.sk89q.worldedit.patterns.RandomFillPattern;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import me.redis.kohi.SurvivalGames;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class NMSUtils {
    public static void autoRespawn(PlayerDeathEvent event) {
        new BukkitRunnable() {
            public void run() {
                try {
                    Object nmsPlayer = event.getEntity().getClass().getMethod("getHandle", new Class[0]).invoke(event.getEntity(), new Object[0]);
                    Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
                    Class EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");
                    Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    Object mcserver = minecraftServer.get(con);
                    Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList", new Class[0]).invoke(mcserver, new Object[0]);
                    Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer, Integer.TYPE, Boolean.TYPE);
                    moveToWorld.invoke(playerlist, nmsPlayer, 0, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater(SurvivalGames.getPlugin(), 1);
    }


    public static boolean createCylinder(World w, int x, int y, int z, int radius) {
        List<BlockChance> blocks = new ArrayList<>();

        blocks.add(new BlockChance(new BaseBlock(Material.SMOOTH_BRICK.getId()), 33));
        blocks.add(new BlockChance(new BaseBlock(Material.GRAVEL.getId()), 33));
        blocks.add(new BlockChance(new BaseBlock(Material.COBBLESTONE.getId()), 33));

        WorldEditPlugin we = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        if (we == null) {
            return false;
        }

        EditSession es = we.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(w), -1);
        try {
            es.enableQueue();
            es.makeCylinder(new Vector(x, y, z), new RandomFillPattern(blocks), radius, radius, 1, true);
            es.flushQueue();
        } catch (MaxChangedBlocksException ignored) {
            return false;
        }
        return true;
    }

    public static boolean createCylinder(World w, int x, int y, int z, int radius, int height, int block) {
        WorldEditPlugin we = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");

        if (we == null) {
            return false;
        }

        EditSession es = we.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(w), -1);
        try {
            es.enableQueue();
            es.makeCylinder(new Vector(x, y, z), new SingleBlockPattern(new BaseBlock(block)), radius, height, true);
            es.drainArea(new Vector(x, y, z), 25);
            es.flushQueue();
        } catch (MaxChangedBlocksException ignored) {
            return false;
        }
        return true;
    }
}
