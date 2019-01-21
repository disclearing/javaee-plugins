package me.javaee.uhc.visualise.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import me.javaee.uhc.UHC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BlockPlaceAdapter extends PacketAdapter {

    private final UHC plugin;

    public BlockPlaceAdapter(UHC plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_PLACE);

        this.plugin = plugin;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        StructureModifier<Integer> modifier = event.getPacket().getIntegers();
        Player player = event.getPlayer();

        try {
            int face = modifier.read(3);
            if (face == 255) {
                return;
            }

            Location clickedBlock = new Location(player.getWorld(), modifier.read(0), modifier.read(1), modifier.read(2));
            if (plugin.getVisualiseHandler().getVisualBlockAt(player, clickedBlock) != null) {
                Location placedLocation = clickedBlock.clone();
                switch (face) {
                    case 2:
                        placedLocation.add(0, 0, -1);
                        break;
                    case 3:
                        placedLocation.add(0, 0, 1);
                        break;
                    case 4:
                        placedLocation.add(-1, 0, 0);
                        break;
                    case 5:
                        placedLocation.add(1, 0, 0);
                        break;
                    default:
                        return;
                }

                if (plugin.getVisualiseHandler().getVisualBlockAt(player, placedLocation) == null) {
                    event.setCancelled(true);
                    player.sendBlockChange(placedLocation, Material.AIR, (byte) 0);
                    player.updateInventory();
                }
            }
        } catch (FieldAccessException ex) {
            ex.printStackTrace();
        }
    }
}
