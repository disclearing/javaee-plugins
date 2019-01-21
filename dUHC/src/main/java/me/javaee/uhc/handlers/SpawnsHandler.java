package me.javaee.uhc.handlers;

import me.javaee.uhc.UHC;
import me.javaee.uhc.utils.Configurator;
import me.javaee.uhc.utils.Pair;
import me.javaee.uhc.utils.ScatterUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SpawnsHandler {
    public ArrayList<Location> scatterPoints = new ArrayList<>();

    public void setScatterPoints() {
        Bukkit.getWorld("world").loadChunk(Bukkit.getWorld("world").getHighestBlockAt(0, 0).getChunk());
    }

    public void addLocation() {
        Location location = getNewLocation(Bukkit.getWorld("world"));

        location.getWorld().loadChunk(location.getChunk());

        location.getWorld().loadChunk(location.add(50, 0, 50).getChunk());
        location.getWorld().loadChunk(location.add(-50, 0, 50).getChunk());
        location.getWorld().loadChunk(location.add(-50, 0, -50).getChunk());
        location.getWorld().loadChunk(location.add(50, 0, -50).getChunk());

        location.getWorld().loadChunk(location.add(50, 0, 0).getChunk());
        location.getWorld().loadChunk(location.add(-50, 0, 0).getChunk());
        location.getWorld().loadChunk(location.add(0, 0, -50).getChunk());
        location.getWorld().loadChunk(location.add(0, 0, 50).getChunk());

        scatterPoints.add(location);
    }

    public Location getNewLocation(World world) {
        Random random = new Random();

        int x = random.nextInt(UHC.getInstance().getGameManager().getCurrentRadius2() * 2) - UHC.getInstance().getGameManager().getCurrentRadius2();
        int z = random.nextInt(UHC.getInstance().getGameManager().getCurrentRadius2() * 2) - UHC.getInstance().getGameManager().getCurrentRadius2();
        Location spawn = new Location(world, x, world.getHighestBlockYAt(x, z) + 0.5D, z);

        if (spawn.add(0, -1, 0).getBlock().getType() == Material.WATER || spawn.add(0, -1, 0).getBlock().getType() == Material.STATIONARY_WATER) {
            spawn.getBlock().setType(Material.GLASS);
        }

        return spawn;
    }
}
