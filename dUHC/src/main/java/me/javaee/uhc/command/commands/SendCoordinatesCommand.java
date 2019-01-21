package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.Configurator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class SendCoordinatesCommand extends BaseCommand {
    public SendCoordinatesCommand() {
        super("sendcoordinates", Arrays.asList("sendcoords", "cs", "sendc", "scoords", "scs"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Configurator.Option teamSize = UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.TEAMSIZE.name());

        if (teamSize.getValue() == null || (int) teamSize.getValue() < 2) {
            player.sendMessage(ChatColor.RED + "This is a free for all game.");
        } else {
            if (UHCTeam.getByUUID(player.getUniqueId()) == null) {
                player.sendMessage(ChatColor.RED + "You don't have a team.");
            } else {
                for (UUID teamPlayers : UHCTeam.getByUUID(player.getUniqueId()).getPlayerList()) {
                    if (Bukkit.getPlayer(teamPlayers) != null) Bukkit.getPlayer(teamPlayers).sendMessage(ChatColor.translateAlternateColorCodes("&a" + player.getName() + "&7: &c" + player.getLocation().getBlockX() + "&e, &c" + player.getLocation().getBlockY() + "&e,&c " + player.getLocation().getBlockZ()));
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "Sends coordinates to your teammates";
    }
}
