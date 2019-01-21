package me.javaee.uhc.visualise.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import me.javaee.uhc.UHC;
import me.javaee.uhc.visualise.VisualBlock;
import me.javaee.uhc.visualise.VisualBlockData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BlockDigAdapter extends PacketAdapter {

    private static final int STARTED_DIGGING = 0;
    private static final int FINISHED_DIGGING = 2;

    private UHC plugin;

    public BlockDigAdapter(UHC plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG);

        this.plugin = plugin;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        StructureModifier<Integer> modifier = event.getPacket().getIntegers();

        try {
            int status = modifier.read(4);

            if (status == STARTED_DIGGING || status == FINISHED_DIGGING) {
                Player player = event.getPlayer();

                int x = modifier.read(0), y = modifier.read(1), z = modifier.read(2);
                Location location = new Location(player.getWorld(), x, y, z);

                VisualBlock visualBlock = plugin.getVisualiseHandler().getVisualBlockAt(player, location);
                if (visualBlock != null) {
                    event.setCancelled(true);
                    VisualBlockData data = visualBlock.getBlockData();

                    if (status == FINISHED_DIGGING || player.getGameMode() == GameMode.CREATIVE) {
                        player.sendBlockChange(location, data.getBlockType(), data.getData());
                    }
                }
            }
        } catch (FieldAccessException ex) {
            ex.printStackTrace();
        }
    }
}
