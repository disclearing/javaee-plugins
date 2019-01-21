package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class HostCommand extends BaseCommand {
    GameManager gameManager = UHC.getInstance().getGameManager();

    public HostCommand() {
        super("host", Arrays.asList("hosts"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes("&cUsage: /" + label + " <player>"));
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player with name '" + args[0] + "' not found.");
            } else {
                if (UHC.getInstance().getGameManager().getModerators().contains(target)) {
                    target.sendMessage(ChatColor.RED + "That player is a moderator, remove him first.");
                } else {
                    gameManager.setHost(gameManager.getHost() == null ? target : null);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&f" + (gameManager.getHost() == null ? "None" : gameManager.getHost().getName()) + " &6is now the Host of the Game."));

                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLast uhc name was '&7" + UHC.getInstance().getServerInfo().getLastMatchName() + "&e'."));

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvnplink nether world world_nether");
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "Gives you the host of the game";
    }
}
