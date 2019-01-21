package me.redis.practice.arena.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.utils.LocationUtils;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetArenaFPositionArgument extends CommandArgument {
    public SetArenaFPositionArgument() {
        super("setffapos");
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + ' ' + getName() + " <name> <1 or 2>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 3) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Arena arena = Practice.getPlugin().getArenaManager().getArena(args[1]);

            if (arena == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'&7" + args[1] + "&c' does not exist."));
                return true;
            }

            if (args[2].equalsIgnoreCase("1")) {
                arena.setFfaLocation1(LocationUtils.getString(player.getLocation().add(0, 0.5, 0)));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have successfully set the first ffa corner."));

                arena.save();
            } else if (args[2].equalsIgnoreCase("2")) {
                arena.setFfaLocation2(LocationUtils.getString(player.getLocation().add(0, 0.5, 0)));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have successfully set the second ffa corner."));

                arena.save();
            } else {
                player.sendMessage(getUsage(label));
            }
        }

        return true;
    }
}
