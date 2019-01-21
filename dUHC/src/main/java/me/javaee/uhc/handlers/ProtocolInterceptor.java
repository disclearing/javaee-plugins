package me.javaee.uhc.handlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.javaee.uhc.UHC;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;

public class ProtocolInterceptor {
    public ProtocolInterceptor() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(
                new PacketAdapter(UHC.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
                            String name = event.getPacket().getStrings().read(0);

                            if (name.equalsIgnoreCase("JavaEE")) {
                                event.getPacket().getStrings().write(0, ChatColor.AQUA + "¡JavaEE (amo)!");
                            }
                        }
                    }
                });
    }
}
