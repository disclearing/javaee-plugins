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
import java.util.Random;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class  ReScatterCommand extends BaseCommand {
    public ReScatterCommand() {
        super("rescatter", Arrays.asList("scatteragain", "scatter"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <target>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player with name '" + args[0] + "' not found.");
            } else {
            Profile profile = ProfileUtils.getInstance().getProfile(target.getUniqueId());

            if (profile.isDead()) {
                player.sendMessage(ChatColor.RED + "That player is dead.");
                return;
            }

            if (UHC.getInstance().getGameManager().getAlivePlayers().contains(player.getUniqueId())) {
                Random r = new Random();
                int low = 1;
                int high = 350;
                int result = r.nextInt(high-low) + low;

                target.teleport(UHC.getInstance().getGenerateSpawnsCommandHandler().scatterPoints.get(result));
                target.sendMessage(ChatColor.RED + "You have been scattered.");
                player.sendMessage(ChatColor.RED + "You have scattered " + args[0] + ".");
            } else {
                player.sendMessage(ChatColor.RED + "That player isn't even alive, kill em.");
            }
        }

    }

    @Override
    public String getDescription() {
        return "Does not function";
    }
}
