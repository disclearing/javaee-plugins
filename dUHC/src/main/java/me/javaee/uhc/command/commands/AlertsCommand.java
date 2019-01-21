package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
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
public class AlertsCommand extends BaseCommand {
    public AlertsCommand() {
        super("alerts", Arrays.asList("minealerts", "ma"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

        if (profile.isAlerts()) {
            profile.setAlerts(false);
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &cdisabled &ethe alerts."));
        } else {
            profile.setAlerts(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &aenabled &ethe alerts."));
        }
    }

    @Override
    public String getDescription() {
        return "Toggles your mine alerts";
    }
}
