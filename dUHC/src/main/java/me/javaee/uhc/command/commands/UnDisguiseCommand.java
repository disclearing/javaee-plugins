package me.javaee.uhc.command.commands;

import me.javaee.uhc.command.BaseCommand;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class UnDisguiseCommand extends BaseCommand {
    public UnDisguiseCommand() {
        super("undisguise", Arrays.asList("ud", "und"), true, true);
    }

    public void undisguisePlayer(Player player/*, String name*/) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online == player) continue;

            //Aqui remuevo al jugador para cambiar su GameProfile y luego agregarlo
            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) player).getHandle()));

            //Procedo a editar el Game Profile
            GameProfile gameProfile = ((CraftPlayer) player).getProfile();

            try {
                Field nameField = GameProfile.class.getDeclaredField("name");
                nameField.setAccessible(true);

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);

               // nameField.set(gameProfile, /* Cambiar a @param name */ getDisguisedPlayers().get(player.getName()));

            } catch (IllegalAccessException | NoSuchFieldException ex) {
                throw new IllegalStateException(ex);
            }

            //Añadimos al jugador otra vez y le enviamos los paquetes a los players online
            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.addPlayer(((CraftPlayer) player).getHandle()));
            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle()));
        }
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        GameProfile gameProfile = ((CraftPlayer) player).getProfile();

      /* for (String disguised : getDisguisedPlayers().keySet()) {
            if (disguised.equalsIgnoreCase(gameProfile.getName())) {
                undisguisePlayer(Bukkit.getPlayer(disguised));
                player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have been undisguised."));
            } else {
                player.sendMessage(ChatColor.RED + "You are not disguised.");
            }
        }*/
    }

    @Override
    public String getDescription() {
        return "Does not work";
    }

    // public Map<String, String> getDisguisedPlayers() {
       // return SkinCommand.disguisedPlayers;
   // }
}
