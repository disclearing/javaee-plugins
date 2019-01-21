package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender player, Command command, String s, String[] strings) {
        player.sendMessage(ChatColor.translateAlternateColorCodes("&7&m---------------------------------"));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&6Host&7: &f" + (UHC.getInstance().getGameManager().getHost() == null ? "None" : UHC.getInstance().getGameManager().getHost().getName())));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&6Moderators&7: &f" + getModerators()));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&6Helpers&7: &f" + getHelpers()));
        player.sendMessage(ChatColor.translateAlternateColorCodes(""));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&6Alive&7: &f" + UHC.getInstance().getGameManager().getAlivePlayers().size()));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&6Spectators&7: &f" + UHC.getInstance().getSpectatorManager().getSpectators().size()));
        player.sendMessage(ChatColor.translateAlternateColorCodes(""));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&6Total&7: &f" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers()));
        player.sendMessage(ChatColor.translateAlternateColorCodes("&7&m---------------------------------"));

        return true;
    }

    public String getModerators() {
        return UHC.getInstance().getGameManager().getModerators().size() < 1 ? "None" : UHC.getInstance().getGameManager().getModerators().stream().map(Player::getName).collect(Collectors.joining(", "));
    }

    public String getHelpers() {
        return UHC.getInstance().getGameManager().getHelpers().size() < 1 ? "None" : UHC.getInstance().getGameManager().getHelpers().stream().map(Player::getName).collect(Collectors.joining(", "));
    }
}
