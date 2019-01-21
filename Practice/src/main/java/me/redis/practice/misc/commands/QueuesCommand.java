package me.redis.practice.misc.commands;

import me.redis.practice.Practice;
import me.redis.practice.utils.TimeUtil;
import me.redis.practice.utils.command.ExecutableCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class QueuesCommand extends ExecutableCommand {
    public QueuesCommand() {
        super("queue");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <time>");
            return true;
        }

        if (args[0].equalsIgnoreCase("pause")) {
            if (Practice.getPlugin().getTimerManager().getQueuesTimer().getRemaining() <= 0) {
                sender.sendMessage(ChatColor.RED + "The queue timer is not on.");
                return true;
            }

            if (Practice.getPlugin().getTimerManager().getQueuesTimer().isPaused()) {
                Practice.getPlugin().getTimerManager().getQueuesTimer().setPaused(false);
                sender.sendMessage(ChatColor.RED + "You have paused the queue timer.");
            } else {
                Practice.getPlugin().getTimerManager().getQueuesTimer().setPaused(true);
                sender.sendMessage(ChatColor.RED + "You have unpaused the queue timer.");
            }
            return true;
        }

        String time = args[0];
        Practice.getPlugin().getTimerManager().getQueuesTimer().setRemaining(TimeUtil.parseDuration(time), true);
        sender.sendMessage(ChatColor.RED + "You have started the queue restart timer with a time of " + args[0] + ".");

        return true;
    }
}
