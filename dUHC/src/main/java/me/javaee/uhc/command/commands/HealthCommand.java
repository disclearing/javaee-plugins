package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Collections;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class HealthCommand extends BaseCommand {
    public HealthCommand() {
        super("health", Collections.singletonList("h"), false, true);
    }
    DecimalFormat decimalFormat = new DecimalFormat("#.#");

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player with name '" + args[0] + "' not found.");
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes("&f" + target.getName() + " &6health is &f" + decimalFormat.format(target.getHealth()) + "&6."));
            }
        }
    }

    @Override
    public String getDescription() {
        return "Sees the health of a player";
    }
}
