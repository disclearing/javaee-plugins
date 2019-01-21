package me.javaee.uhc.utils;

import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FakePlayer {
    EntityPlayer fakePlayer;

    public FakePlayer(Player player, Location location) {
        createPlayer(player, location);
    }

    private void createPlayer(Player player, Location location) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorldServer = ((CraftWorld) player.getWorld()).getHandle();

        GameProfile profile = new GameProfile(UUID.randomUUID(), player.getName());
        profile.getProperties().putAll(((CraftPlayer) player).getHandle().getProfile().getProperties());

        fakePlayer = new EntityPlayer(nmsServer, nmsWorldServer, profile, new PlayerInteractManager(nmsWorldServer));
        fakePlayer.setLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0.0f, 0.0f);

        PacketPlayOutNamedEntitySpawn packetSpawnEntity = new PacketPlayOutNamedEntitySpawn(fakePlayer);

        sendPacketToAll(packetSpawnEntity);
    }

    private void sendPacket(Player player, Packet packet) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        if (entityPlayer != null) {
            entityPlayer.playerConnection.sendPacket(packet);
        }
    }

    private void sendPacketToAll(Packet packet) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            sendPacket(online, packet);
        }
    }

    public void setSleeping(Location location) {
        PacketPlayOutBed bedPacket = new PacketPlayOutBed(fakePlayer, location.getBlockX(), location.getWorld().getHighestBlockYAt(location), location.getBlockZ());

        sendPacketToAll(bedPacket);
    }
}