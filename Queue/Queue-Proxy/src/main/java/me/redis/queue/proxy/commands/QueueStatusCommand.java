package me.redis.queue.proxy.commands;

import me.redis.queue.proxy.QueueProxy;
import me.redis.queue.proxy.queue.Queue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class QueueStatusCommand extends Command {
    public QueueStatusCommand() {
        super("queuestatus", "queue.command.status", "qstatus");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Queue System...");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.status.separator")));
        for (Queue queue : QueueProxy.getPlugin().getQueueManager().getQueues()) {
            if (!queue.isOnline()) {
                sender.sendMessage(" " + ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + queue.getServer());
                sender.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + " This server is offline...");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.status.separator")));
            }  else {
                for (String message : QueueProxy.getPlugin().getMessages().getStringList("messages.status.lines")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("%queue_name%", queue.getServer()).replace("%server_online%", String.valueOf(queue.getOnlinePlayers())).replace("%server_max%", String.valueOf(queue.getMaxPlayers())).replace("%whitelist_status%", (queue.isWhitelisted() ? "whitelisted" : "not whitelisted")).replace("%paused_status%", (queue.isPaused() ? "paused" : "not paused")).replace("%queue_players%", String.valueOf(queue.getQueuedPlayers().size()))));
                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.status.separator")));
            }
        }

    }
}
