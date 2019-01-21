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
public class TeleCommand extends BaseCommand {
    public TeleCommand() {
        super("tele", Arrays.asList("telep", "specttp"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return;
        }

        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player) || UHC.getInstance().getGameManager().getHost() == player || UHC.getInstance().getGameManager().getModerators().contains(player)) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player with name '" + args[0] + "' not found.");
            } else {
                if (UHC.getInstance().getGameManager().getAlivePlayers().contains(target.getUniqueId())) {
                    if (!player.hasPermission("vip.bypass.caves")) {
                        if (target.getLocation().getY() <= 35) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou can't teleport to players that are below the surface. If you want, you can buy a rank at &bstore.silexpvp.net &eto bypass this."));
                            return;
                        }
                    }

                    player.teleport(target);
                    player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have been teleported to &b" + target.getName() + "&e."));
                } else {
                    player.sendMessage(ChatColor.RED + "You can only teleport to alive players.");
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You are not a spectator.");
        }
    }

    @Override
    public String getDescription() {
        return "Used to teleport if you are a spectator";
    }
}
