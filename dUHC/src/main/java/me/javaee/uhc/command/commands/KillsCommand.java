package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class KillsCommand extends BaseCommand {
    public KillsCommand() {
        super("kills", Arrays.asList("howkills", "killstats", "kc"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            } else {
                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    player.sendMessage(ChatColor.RED + "That player is offline.");
                } else {
                    if (UHC.getInstance().getGameManager().getKillNames().get(target.getUniqueId()).size() == 0) {
                        player.sendMessage(ChatColor.RED + "That player does not have kills.");
                    } else {
                        int counter = 0;

                        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + target.getName());

                        for (String kill : UHC.getInstance().getGameManager().getKillNames().get(target.getUniqueId())) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes(" &6&l" + (counter + 1) + ". &e" + kill));
                            counter++;
                        }
                    }
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "The game must be running.");
        }
    }

    @Override
    public String getDescription() {
        return "It gets the kills of a player";
    }
}
