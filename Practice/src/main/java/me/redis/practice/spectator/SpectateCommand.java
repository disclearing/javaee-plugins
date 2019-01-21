package me.redis.practice.spectator;

import me.redis.practice.Practice;
import me.redis.practice.utils.command.ExecutableCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand extends ExecutableCommand {
    public SpectateCommand() {
        super("spectate", null, "spec", "spect");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
                return true;
            }

            Practice.getPlugin().getSpectatorManager().startSpectating((Player) sender, Bukkit.getPlayer(args[0]));
        }

        return true;
    }
}
