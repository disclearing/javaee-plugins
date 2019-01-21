package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.menu.menu.StatsMenu;
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
public class SitCommand extends BaseCommand {
    public SitCommand() {
        super("statistics", Arrays.asList("stats", "stat", "statistic"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            if (UHC.getInstance().getConfigurator().getBooleanOption("STATLESS").getValue()) {
                player.sendMessage(ChatColor.RED + "This is a stat less game!");
                return;
            }
        }

        if (args.length == 0) {
            new StatsMenu(player).open(player);
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player with name '" + args[0] + "' not found.");
            } else {
                new StatsMenu(target).open(player);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " [player]");
        }
    }

    @Override
    public String getDescription() {
        return "Gets your statistics";
    }
}
