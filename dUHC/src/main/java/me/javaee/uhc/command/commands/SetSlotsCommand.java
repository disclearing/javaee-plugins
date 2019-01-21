package me.javaee.uhc.command.commands;

import me.javaee.uhc.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class SetSlotsCommand extends BaseCommand {
    public SetSlotsCommand() {
        super("setslots", Collections.singletonList("slots"), true, true);
    }

    private void setMaxPlayers(int amount) throws ReflectiveOperationException {
        String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer").getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
        Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
        maxplayers.setAccessible(true);
        maxplayers.set(playerlist, amount);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <amount>");
        } else {
            Integer amount = tryParseInteger(args[0]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + amount + "' is not a valid number.");
            } else {
                if (amount <= 0) {
                    sender.sendMessage(ChatColor.RED + "'" + amount + "' is not a valid number.");
                } else {
                    try {
                        broadcastCommandMessage(sender, ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " has updated the slots capacity from " + ChatColor.GRAY + Bukkit.getMaxPlayers() + ChatColor.GREEN + " to " + ChatColor.GRAY + amount + ChatColor.GREEN + '.');
                        setMaxPlayers(amount);
                    } catch (ReflectiveOperationException expeption) {
                        expeption.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "Sets the slots of the uhc";
    }

    public static Integer tryParseInteger(String string) {
        try {
            return Integer.parseInt(string);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
