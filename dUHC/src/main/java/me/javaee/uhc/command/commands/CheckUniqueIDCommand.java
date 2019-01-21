package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class CheckUniqueIDCommand extends BaseCommand {
    public CheckUniqueIDCommand() {
        super("checkuniqueid", Arrays.asList("checkuuid", "cuuid", "uuid", "cuniqueid", "cuid"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);


            if (args[0].equalsIgnoreCase("host")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes("&eThe &6Host &euuid is&7:"));
                player.sendMessage(ChatColor.translateAlternateColorCodes("&7 - &6" + UHC.getInstance().getGameManager().getHost()));
            } else {
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player with name '" + args[0] + "' not found.");
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes("&6" + target.getName() + " &eunique id is&7:"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes("&7 - &6" + target.getUniqueId()));
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
        }
    }

    @Override
    public String getDescription() {
        return "Checks your uniqueId (Not necessary)";
    }
}
