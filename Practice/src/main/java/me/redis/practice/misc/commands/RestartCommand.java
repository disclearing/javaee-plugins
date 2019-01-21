package me.redis.practice.misc.commands;

import me.redis.practice.Practice;
import me.redis.practice.utils.TimeUtil;
import me.redis.practice.utils.command.ExecutableCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RestartCommand extends ExecutableCommand {
    public RestartCommand() {
        super("restart");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <time>");
            return true;
        }

        if (args[0].equalsIgnoreCase("pause")) {
            if (Practice.getPlugin().getTimerManager().getRestartTimer().getRemaining() <= 0) {
                sender.sendMessage(ChatColor.RED + "The restart timer is not on.");
                return true;
            }

            if (Practice.getPlugin().getTimerManager().getRestartTimer().isPaused()) {
                Practice.getPlugin().getTimerManager().getRestartTimer().setPaused(false);
                sender.sendMessage(ChatColor.RED + "You have paused the restart timer.");
            } else {
                Practice.getPlugin().getTimerManager().getRestartTimer().setPaused(true);
                sender.sendMessage(ChatColor.RED + "You have unpaused the restart timer.");
            }
            return true;
        }

        String time = args[0];
        Practice.getPlugin().getTimerManager().getRestartTimer().setRemaining(TimeUtil.parseDuration(time), true);
        sender.sendMessage(ChatColor.RED + "You have started the restart timer with a time of " + args[0] + ".");

        return true;
    }
}
