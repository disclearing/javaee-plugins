package me.javaee.uhc.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class LatencyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        int ms = ((CraftPlayer) player).getHandle().ping;

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYour latency is &a" + ms + "&e."));
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player with name or uuid '" + args[0] + "' was not found.");
            } else {
                int targetMs = ((CraftPlayer) target).getHandle().ping;

                int higherNumber = Math.max(ms, targetMs);
                int lowerNumber = Math.min(ms, targetMs);

                player.sendMessage(ChatColor.translateAlternateColorCodes("&a" + target.getName() + " &elatency is &a" + targetMs + "&e."));
                player.sendMessage(ChatColor.translateAlternateColorCodes("&eLatency difference: &a" + (higherNumber - lowerNumber)));
            }
        }
        return true;
    }
}
