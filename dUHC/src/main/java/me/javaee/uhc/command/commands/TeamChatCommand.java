package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.team.UHCTeam;
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
public class TeamChatCommand extends BaseCommand {
    public TeamChatCommand() {
        super("teamchat", Arrays.asList("tc", "ct"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() > 1) {
            if (UHCTeam.getByUUID(player.getUniqueId()) != null) {
                UHCTeam team = UHCTeam.getByUUID(player.getUniqueId());
                Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

                if (args.length == 0) {
                    if (profile.isTeamChat()) {
                        profile.setTeamChat(false);
                        player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &cdisabled &eyour team chat."));
                    } else {
                        profile.setTeamChat(true);
                        player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &aenabled &eyour team chat."));
                    }
                } else {
                    for (UUID players : team.getPlayerList()) {
                        if (Bukkit.getPlayer(players) != null) {
                            String message = "";
                            for (String part : args) {
                                if (message != "") message += " ";
                                message += part;
                            }
                            Bukkit.getPlayer(players).sendMessage(ChatColor.translateAlternateColorCodes("&7(TC) &6" + player.getName() + "&7: &e" + message));
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "You are not in a team.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "This is a free for all game.");
        }
    }

    @Override
    public String getDescription() {
        return "It toggles your teamchat mode";
    }
}
