package me.javaee.uhc.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import me.javaee.uhc.UHC;
import me.javaee.uhc.visualise.VisualBlock;
import me.javaee.uhc.visualise.VisualBlockData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtocolLibHook extends PacketAdapter {
    public ProtocolLibHook(UHC plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Status.Server.OUT_SERVER_INFO);

        this.plugin = plugin;
    }


}
