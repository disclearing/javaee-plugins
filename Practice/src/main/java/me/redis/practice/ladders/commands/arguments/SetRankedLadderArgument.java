package me.redis.practice.ladders.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRankedLadderArgument extends CommandArgument {
    public SetRankedLadderArgument() {
        super("ranked");
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

            Ladder ladder = Practice.getPlugin().getLadderManager().getLadder(args[1]);

            if (ladder == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c'&7" + args[1] + "&c' does not exist."));
                return true;
            }

            if (ladder.isRanked()) {
                ladder.setRanked(false);
                ladder.save();

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + ladder.getName() + " is now unranked."));
            } else {
                ladder.setRanked(true);
                ladder.save();

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + ladder.getName() + " is now ranked."));
            }
        }

        return true;
    }
}