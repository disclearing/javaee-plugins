package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class EventGameWhitelist extends BaseCommand {
    public EventGameWhitelist() {
        super("eventgamewl", Arrays.asList("egwl", "egwhitelist"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        for (String names : UHC.getInstance().getConfig().getStringList("whitelist")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOfflinePlayer(names).setWhitelisted(true);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&a" + names + "&e has been whitelisted."));
                }
            }.runTask(UHC.getInstance());
        }
    }

    @Override
    public String getDescription() {
        return "Whitelists people for an event.";
    }
}
