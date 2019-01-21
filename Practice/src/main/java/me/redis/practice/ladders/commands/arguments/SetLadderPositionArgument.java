package me.redis.practice.ladders.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.utils.BukkitUtils;
import me.redis.practice.utils.JavaUtils;
import me.redis.practice.utils.SerializationUtils;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLadderPositionArgument extends CommandArgument {
    public SetLadderPositionArgument() {
        super("setposition");
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + ' ' + getName() + " <name> <position>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 3) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Ladder ladder = Practice.getPlugin().getLadderManager().getLadder(args[1]);

            if (ladder == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'&7" + args[1] + "&c' does not exist."));
                return true;
            }

            if (!JavaUtils.isInteger(args[2])) {
                player.sendMessage(ChatColor.RED + "You need to specify a valid number.");
                return true;
            }

            for (Ladder ladders : Practice.getPlugin().getLadderManager().getLadders().values()) {
                if (ladders.getPosition() == Integer.parseInt(args[2])) {
                    player.sendMessage(ChatColor.RED + "There's already a ladder with that position.");
                    return true;
                }
            }

            ladder.setPosition(Integer.parseInt(args[2]));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + ladder.getName() + "'s position is now " + args[2] + "."));
            ladder.save();
        }
        return true;
    }
}