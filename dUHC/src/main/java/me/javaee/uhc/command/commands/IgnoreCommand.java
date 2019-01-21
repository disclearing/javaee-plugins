package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            } else {
                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    player.sendMessage(ChatColor.RED + "That player is not online.");
                } else {
                    if (UHC.getInstance().getIgnoredPlayers().get(player.getUniqueId()).contains(target.getUniqueId())) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes("&c" + target.getName() + "&e has been removed from your ignore list."));
                        UHC.getInstance().getIgnoredPlayers().get(player.getUniqueId()).remove(target.getUniqueId());
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes("&c" + target.getName() + "&e has been added to your ignore list."));
                        UHC.getInstance().getIgnoredPlayers().get(player.getUniqueId()).add(target.getUniqueId());
                    }
                }
            }
        }
        return true;
    }
}
