package me.redis.practice.ladders.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateLadderArgument extends CommandArgument {
    public CreateLadderArgument() {
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

            for (Ladder ladder : Practice.getPlugin().getLadderManager().getLadders().values()) {
                if (ladder.getName().equalsIgnoreCase(name)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'&7" + name + "&c' already exists in the database."));
                    return true;
                }
            }

            Ladder ladder = new Ladder(name);
            ladder.save();

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'&7" + name + "&c' has been successfully created."));
        }
        return false;
    }
}
