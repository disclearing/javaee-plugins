package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class DatabaseCommand extends BaseCommand {
    public DatabaseCommand() {
        super("database", Collections.singletonList("db"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <save>/<set> <name> <value>");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("save")) {
            double now = System.currentTimeMillis();
            UHC.getInstance().getServerInfo().save();

            player.sendMessage(ChatColor.translateAlternateColorCodes("&7Saved the database in " + (System.currentTimeMillis() - now) + "ms."));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("lastWinner")) {
            //UHC.getInstance().getServerInfo().setLastWinner(args[2]);

          //  player.sendMessage(ChatColor.translateAlternateColorCodes("&7The last winner is now: " + UHC.getInstance().getServerInfo().getLastWinner() + "."));
        }
    }

    @Override
    public String getDescription() {
        return "It changes the last winner";
    }
}
