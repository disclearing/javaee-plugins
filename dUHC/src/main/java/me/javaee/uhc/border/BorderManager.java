package me.javaee.uhc.border;

import me.javaee.uhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class BorderManager {
    private final UHC plugin = UHC.getInstance();
    private final Map<Player, List<Location>> map;
    private final byte color;
    private int border;

    public List<Location> getBlocks(Player player) {
        return map.get(player);
    }

    public void update(Player player, List<Location> toUpdate) {
        if (map.containsKey(player)) {
            for (Location location : map.get(player)) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getTypeId(), block.getData());
            }
            for (Location location2 : toUpdate) {
                player.sendBlockChange(location2, 95, color);
            }
            map.put(player, toUpdate);
        } else {
            for (Location location2 : toUpdate) {
                player.sendBlockChange(location2, 95, color);
            }
            map.put(player, toUpdate);
        }
    }

    public BorderManager() {
        map = new WeakHashMap<>();
        color = 1;
        border = UHC.getInstance().getGameManager().getCurrentRadius2();
    }

    public UHC getPlugin() {
        return plugin;
    }

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
    }
}
