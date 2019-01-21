package me.redis.queue.proxy.commands;

import me.redis.queue.proxy.QueueProxy;
import me.redis.queue.proxy.queue.Queue;
import me.redis.queue.proxy.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LeaveQueueCommand extends Command {
    public LeaveQueueCommand() {
        super("leavequeue", "queue.command.join", "quitqueue");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (QueueProxy.getPlugin().getQueueManager().getByPlayer(player) == null) {
                player.sendMessage(Messages.NOT_QUEUED.toString());
                return;
            }

            Queue queue = QueueProxy.getPlugin().getQueueManager().getByPlayer(player);

            queue.getQueuedPlayers().remove(queue.getPosition(player) - 1);
            player.sendMessage(Messages.LEFT_QUEUE.toString());

            return;
        }

        sender.sendMessage(ChatColor.RED + "This command must be executed by a player.");
    }
}
