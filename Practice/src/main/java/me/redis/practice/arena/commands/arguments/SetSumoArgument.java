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

public class SetSumoArgument extends CommandArgument {
    public SetSumoArgument() {
        super("setsumo");
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + ' ' + getName() + " <name> <1 or 2>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Arena arena = Practice.getPlugin().getArenaManager().getArena(args[1]);

            if (arena == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'&7" + args[1] + "&c' does not exist."));
                return true;
            }
        }

        return true;
    }
}
