package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ToggleSpectatorsCommand extends BaseCommand {
    public static List<Player> toggleSpectator = new ArrayList<>();
    public ToggleSpectatorsCommand() {
        super("togglespectators", Arrays.asList("tspectator", "ts", "tsp"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            if (!UHC.getInstance().getGameManager().getAlivePlayers().contains(player.getUniqueId())) {
                if (toggleSpectator.contains(player)) {
                    toggleSpectator.remove(player);

                    for (Player players : UHC.getInstance().getSpectatorManager().getSpectators()) {
                        player.showPlayer(players);
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &aenabled &espectators."));
                } else {
                    toggleSpectator.add(player);

                    for (Player players : UHC.getInstance().getSpectatorManager().getSpectators()) {
                        player.hidePlayer(players);
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &cdisabled &espectators."));
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes("&cYou can't use this if you are alive."));
            }
        } else {
            player.sendMessage(ChatColor.RED + "The game hasn't started yet.");
        }
    }

    @Override
    public String getDescription() {
        return "Toggles the spectators";
    }
}
