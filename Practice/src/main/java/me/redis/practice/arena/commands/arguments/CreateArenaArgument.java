package me.redis.practice.arena.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateArenaArgument extends CommandArgument {
    public CreateArenaArgument() {
        super("create");
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + ' ' + getName() + " <name>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            String name = args[1];

            for (Arena arena : Practice.getPlugin().getArenaManager().getArenas().values()) {
                if (arena.getName().equalsIgnoreCase(name)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'&7" + name + "&c' already exists in the database."));
                    return true;
                }
            }

            Arena arena = new Arena(name);
            arena.save();

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'&7" + name + "&c' has been successfully created."));
        }
        return true;
    }
}
