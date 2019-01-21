package me.redis.queue.proxy.queue;

import lombok.Getter;
import lombok.Setter;
import me.redis.queue.proxy.profile.QueuedPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.LinkedList;

@Getter @Setter
public class Queue {
    private final String server;
    private int maxPlayers;
    private int onlinePlayers;
    private boolean paused;
    private boolean online;
    private boolean whitelisted;
    private final LinkedList<QueuedPlayer> queuedPlayers;

    public Queue(String server) {
        this.server = server;

        queuedPlayers = new LinkedList<>();
    }

    public QueuedPlayer getByPosition(int position) {
        return queuedPlayers.get(position);
    }

    public int getPosition(ProxiedPlayer player) {
        for (int i = 0; i < queuedPlayers.size(); ++i) {
            if (queuedPlayers.get(i).getPlayer().getName().equalsIgnoreCase(player.getName())) {
                return i + 1;
            }
        }
        return -1;
    }

    public int getRealPosition(ProxiedPlayer player) {
        for (int i = 0; i < queuedPlayers.size(); ++i) {
            if (queuedPlayers.get(i).getPlayer().getName().equalsIgnoreCase(player.getName())) {
                return i;
            }
        }
        return -1;
    }
}
