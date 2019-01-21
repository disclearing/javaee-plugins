package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.enums.GameState;
import net.silexpvp.nightmare.util.LuckPermsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class VipWhitelistCommand extends BaseCommand {
    public VipWhitelistCommand() {
        super("vipwhitelist", Collections.singletonList("vwl, wl"), true, true);
    }
    private UHC uhc = UHC.getInstance();

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (UHC.getInstance().getGameManager().getGameState() != GameState.WAITING) {
            return;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
        } else {
            if (Bukkit.getOfflinePlayer(args[0]).isWhitelisted()) {
                player.sendMessage(ChatColor.RED + Bukkit.getOfflinePlayer(args[0]).getName() + " is already whitelisted.");
                return;
            }

            if (player.hasPermission("rank.superpro")) {
                Bukkit.getOfflinePlayer(args[0]).setWhitelisted(true);

                player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have whitelisted &f" + args[0] + " &e. &7" + "(You can whitelist infinite players)."));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(LuckPermsUtils.getPrefix(player) + player.getName() + " &ehas whitelisted &f" + LuckPermsUtils.getPrefix(args[0]) + Bukkit.getOfflinePlayer(args[0]).getName() + "&e with his rank."));
                return;
            }

            if (UHC.getInstance().getWhitelisted().get(player.getUniqueId())  != null) {
                if (uhc.getWhitelisted().get(player.getUniqueId()) != 0) {
                    uhc.getWhitelisted().put(player.getUniqueId(), uhc.getWhitelisted().get(player.getUniqueId()) - 1);

                    Bukkit.getOfflinePlayer(args[0]).setWhitelisted(true);

                    player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have whitelisted &f" + args[0] + " &e. &7" + "(You can whitelist " + uhc.getWhitelisted().get(player.getUniqueId()) + " more players)."));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(LuckPermsUtils.getPrefix(player) + player.getName() + " &ehas whitelisted &6" + Bukkit.getOfflinePlayer(args[0]).getName() + "&e with his rank."));
                } else {
                    player.sendMessage(ChatColor.RED + "You can't whitelist more players.");
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "You can whitelist a player if you are vip";
    }
}
