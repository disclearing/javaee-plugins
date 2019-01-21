package me.javaee.uhc.tasks;

import me.javaee.uhc.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BlocksTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Block block : BukkitUtils.getBlocks(new Location(Bukkit.getWorld("Deathmatch"), 0, 91, 0).getBlock(), 25)) {
            if (block.getType() == Material.OBSIDIAN || block.getType() == Material.NETHER_FENCE || block.getType() == Material.SKULL || block.getType() == Material.CHEST) {
                block.setType(Material.AIR);
            }
        }
    }
}
