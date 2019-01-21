package me.redis.practice.arena.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetArenaAuthorsArgument extends CommandArgument {
    public SetArenaAuthorsArgument() {
        super("authors");
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + ' ' + getName() + " <name> <add/remove> <author>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Arena arena = Practice.getPlugin().getArenaManager().getArena(args[1]);

            if (arena == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'&7" + args[1] + "&c' does not exist."));
                return true;
            }

            if (args[2].equalsIgnoreCase("add")) {
                if (arena.getAuthors().contains(args[3])) {
                    player.sendMessage(ChatColor.RED + "That player is already an author of this arena.");
                    return true;
                }

                arena.getAuthors().add(args[3]);
                arena.save();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have successfully added an author to the arena."));
            } else if (args[2].equalsIgnoreCase("remove")) {
                if (!arena.getAuthors().contains(args[3])) {
                    player.sendMessage(ChatColor.RED + "That player is not an author of this arena.");
                    return true;
                }

                arena.getAuthors().remove(args[3]);
                arena.save();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have successfully removed an author to the arena."));
            }
        }

        return true;
    }
}
