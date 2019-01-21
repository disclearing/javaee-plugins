package me.redis.queue.proxy.commands;

import me.redis.queue.proxy.QueueProxy;
import me.redis.queue.proxy.queue.Queue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PauseQueueCommand extends Command {
    public PauseQueueCommand() {
        super("pausequeue", "queue.command.pause", "qpause");
    }

    public static String j(final String s) {
        final int n = 5 << 4;
        final int n2 = 2;
        final int n3 = n ^ (n2 << n2 ^ 0x1);
        final int n4 = 5 << 3 ^ 0x5;
        final int length = s.length();
        final char[] array = new char[length];
        int n5;
        int i = n5 = length - 1;
        final char[] array2 = array;
        final char c = (char)n4;
        final int n6 = n3;
        while (i >= 0) {
            final char[] array3 = array2;
            final int n7 = n5;
            final char char1 = s.charAt(n7);
            --n5;
            array3[n7] = (char)(char1 ^ n6);
            if (n5 < 0) {
                break;
            }
            final char[] array4 = array2;
            final int n8 = n5--;
            array4[n8] = (char)(s.charAt(n8) ^ c);
            i = n5;
        }
        return new String(array2);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /pausequeue <server>");
            return;
        }

        Queue queue = QueueProxy.getPlugin().getQueueManager().getByServer(args[0]);

        if (queue == null) {
            sender.sendMessage(ChatColor.RED + "The server you specified is not valid.");
            return;
        }

        queue.setPaused(!queue.isPaused());
        sender.sendMessage(ChatColor.YELLOW + "You have " + (queue.isPaused() ? ChatColor.RED + "paused" : ChatColor.GREEN + "unpaused") + ChatColor.YELLOW + " the queue named '" + ChatColor.GRAY + queue.getServer() + ChatColor.YELLOW + "'.");
    }
}
