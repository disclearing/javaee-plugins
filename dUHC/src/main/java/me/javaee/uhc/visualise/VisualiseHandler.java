package me.javaee.uhc.visualise;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import lombok.Getter;
import me.javaee.uhc.utils.cuboid.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class VisualiseHandler {

    @Getter private final Table<UUID, Location, VisualBlock> storedVisualises = HashBasedTable.create();
    private static List<Material> passthrough = new ArrayList<>();

    static {
        passthrough.add(Material.AIR);
        passthrough.add(Material.WATER);
        passthrough.add(Material.STATIONARY_WATER);
        passthrough.add(Material.LAVA);
        passthrough.add(Material.STATIONARY_LAVA);
    }

    @Deprecated
    public VisualBlock getVisualBlockAt(Player player, int x, int y, int z) throws NullPointerException {
        return getVisualBlockAt(player, new Location(player.getWorld(), x, y, z));
    }

    public VisualBlock getVisualBlockAt(Player player, Location location) throws NullPointerException {
        Preconditions.checkNotNull(player, "Player cannot be null");
        Preconditions.checkNotNull(location, "Location cannot be null");
        synchronized (storedVisualises) {
            return storedVisualises.get(player.getUniqueId(), location);
        }
    }

    public Map<Location, VisualBlock> getVisualBlocks(Player player) {
        synchronized (storedVisualises) {
            return new HashMap<>(storedVisualises.row(player.getUniqueId()));
        }
    }

    public Map<Location, VisualBlock> getVisualBlocks(Player player, VisualType visualType) {
        return Maps.filterValues(getVisualBlocks(player), new Predicate<VisualBlock>() {
            @Override
            public boolean apply(VisualBlock visualBlock) {
                return visualType == visualBlock.getVisualType();
            }
        });
    }

    public LinkedHashMap<Location, VisualBlockData> generate(Player player, Cuboid cuboid, VisualType visualType, boolean canOverwrite) {
        Collection<Location> locations = new HashSet<>(cuboid.getSizeX() * cuboid.getSizeY() * cuboid.getSizeZ());
        Iterator<Location> iterator = cuboid.locationIterator();
        while (iterator.hasNext()) {
            locations.add(iterator.next());
        }

        return generate(player, locations, visualType, canOverwrite);
    }

    public LinkedHashMap<Location, VisualBlockData> generate(Player player, Iterable<Location> locations, VisualType visualType, boolean canOverwrite) {
        synchronized (storedVisualises) {
            LinkedHashMap<Location, VisualBlockData> results = new LinkedHashMap<>();

            ArrayList<VisualBlockData> filled = visualType.blockFiller().bulkGenerate(player, locations);
            if (filled != null) {
                int count = 0;
                for (Location location : locations) {
                    if (!canOverwrite && storedVisualises.contains(player.getUniqueId(), location)) {
                        continue;
                    }

                    Material previousType = location.getBlock().getType();
                    if (previousType.isSolid() || !passthrough.contains(previousType)) {
                        continue;
                    }

                    VisualBlockData visualBlockData = filled.get(count++);
                    results.put(location, visualBlockData);
                    player.sendBlockChange(location, visualBlockData.getBlockType(), visualBlockData.getData());
                    storedVisualises.put(player.getUniqueId(), location, new VisualBlock(visualType, visualBlockData, location));
                }
            }

            return results;
        }
    }

    public boolean clearVisualBlock(Player player, Location location) {
        return clearVisualBlock(player, location, true);
    }

    public boolean clearVisualBlock(Player player, Location location, boolean sendRemovalPacket) {
        synchronized (storedVisualises) {
            VisualBlock visualBlock = storedVisualises.remove(player.getUniqueId(), location);
            if (sendRemovalPacket && visualBlock != null) {
                // Have to send a packet to the original block type, don't send if the fake block has the same data properties though.
                Block block = location.getBlock();
                VisualBlockData visualBlockData = visualBlock.getBlockData();
                if (visualBlockData.getBlockType() != block.getType() || visualBlockData.getData() != block.getData()) {
                    player.sendBlockChange(location, block.getType(), block.getData());
                }

                return true;
            }
        }

        return false;
    }
    
    public Map<Location, VisualBlock> clearVisualBlocks(Player player) {
        return clearVisualBlocks(player, null, null);
    }

    public Map<Location, VisualBlock> clearVisualBlocks(Player player, @Nullable VisualType visualType, @Nullable Predicate<VisualBlock> predicate) {
        return clearVisualBlocks(player, visualType, predicate, true);
    }

    @Deprecated
    public Map<Location, VisualBlock> clearVisualBlocks(Player player, @Nullable VisualType visualType, @Nullable Predicate<VisualBlock> predicate, boolean sendRemovalPackets) {
        synchronized (storedVisualises) {
            if (!storedVisualises.containsRow(player.getUniqueId())) {
                return Collections.emptyMap();
            }

            Map<Location, VisualBlock> results = new HashMap<>(storedVisualises.row(player.getUniqueId())); // copy to prevent commodification
            Map<Location, VisualBlock> removed = new HashMap<>();
            for (Map.Entry<Location, VisualBlock> entry : results.entrySet()) {
                VisualBlock visualBlock = entry.getValue();

                if ((predicate == null || predicate.apply(visualBlock)) && (visualType == null || visualBlock.getVisualType() == visualType)) {
                    Location location = entry.getKey();
                    if (removed.put(location, visualBlock) == null) { // not really necessary, but might as well
                        clearVisualBlock(player, location, sendRemovalPackets); // this will call remove on storedVisualises.
                    }
                }
            }

            return removed;
        }
    }
}