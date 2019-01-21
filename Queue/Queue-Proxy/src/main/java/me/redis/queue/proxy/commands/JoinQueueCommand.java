package me.redis.queue.proxy.commands;

import com.google.gson.JsonObject;
import me.redis.queue.proxy.QueueProxy;
import me.redis.queue.proxy.profile.QueuedPlayer;
import me.redis.queue.proxy.profile.rank.Rank;
import me.redis.queue.proxy.queue.Queue;
import me.redis.queue.proxy.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class JoinQueueCommand extends Command {
    public JoinQueueCommand() {
        super("joinqueue", "queue.command.join", "queue");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /joinqueue <server>");
                return;
            }

            if (QueueProxy.getPlugin().getQueueManager().getByPlayer(player) != null) {
                player.sendMessage(Messages.ALREADY_QUEUED.toString());
                return;
            }

            Queue queue = QueueProxy.getPlugin().getQueueManager().getByServer(args[0]);

            if (queue == null) {
                player.sendMessage(Messages.SERVER_NOT_VALID.toString());
                return;
            }

            if (!queue.isOnline()) {
                player.sendMessage(Messages.SERVER_NOT_ONLINE.toString());
                return;
            }

            QueueProxy.getPlugin().getQueueManager().requestJoin(queue, player);
            return;
        }

        sender.sendMessage(ChatColor.RED + "This command must be executed by a player.");
    }
}
