package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SuspendCommand extends BaseCommand {
    public SuspendCommand() {
        super("suspend", Arrays.asList("suspended", "suspendplayer"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/" + label + " <player>");
        } else {
            String target = args[0];

            if (UHC.getInstance().getGameManager().getSuspendedPlayers().contains(target.toLowerCase())) {
                Bukkit.broadcastMessage(ChatColor.RED + target + " has been unsuspended from this uhc.");
                UHC.getInstance().getGameManager().getSuspendedPlayers().remove(target.toLowerCase());
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + target + " has been suspended from this uhc.");
                UHC.getInstance().getGameManager().getSuspendedPlayers().add(target.toLowerCase());

                Bukkit.getOnlinePlayers().forEach(online -> {
                    if (online.getName().equalsIgnoreCase(target)) {
                        online.sendMessage(ChatColor.RED + "You have been suspended from this uhc.");
                        online.kickPlayer(ChatColor.RED + "You have been suspended from this uhc.");
                    }
                });
            }
        }
    }

    @Override
    public String getDescription() {
        return "Suspends a person from the uhc";
    }
}
