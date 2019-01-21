package me.javaee.meetup.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class StaffChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof ConsoleCommandSender) {
            return true;
        }

        if (!commandSender.hasPermission("anticheat.staff")) {
            return true;
        }

        if (args.length > 1) {
            String message = "";
            for (String part : args) {
                if (message != "") message += " ";
                message += part;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("anticheat.staff")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dStaff Chat&7] &d" + commandSender.getName() + "&7: &f" + message));
                }
            }
        }
        return true;
    }
}
