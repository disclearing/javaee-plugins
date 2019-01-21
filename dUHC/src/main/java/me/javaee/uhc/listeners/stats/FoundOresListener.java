package me.javaee.uhc.listeners.stats;

import me.javaee.uhc.UHC;
import me.javaee.uhc.handlers.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.Set;

public class FoundOresListener implements Listener {
    public static Material SEARCH_TYPE;
    private static Material ORE_TYPE;

    static {
        SEARCH_TYPE = Material.DIAMOND_ORE;
        ORE_TYPE = Material.GOLD_ORE;
    }

    public static Set<String> foundLocations;

    public FoundOresListener() {
        foundLocations = new HashSet<>();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().name().toLowerCase().contains("ore")) {
            if (!Scenario.getByName("DoubleOres").isEnabled() && !Scenario.getByName("Cutclean").isEnabled() && !Scenario.getByName("Barebones").isEnabled()) {
                Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
                    UHC.getInstance().getMineManager().handleDatabaseMine(event);
                    UHC.getInstance().getMineManager().handleAlertsMine(event);
                });
            }
        }
    }
}
