package me.redis.queue.proxy.profile;

import me.redis.queue.proxy.profile.rank.Rank;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class QueuedPlayer {
    private ProxiedPlayer player;
    private Rank rank;

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public Rank getRank() {
        return this.rank;
    }

    public QueuedPlayer(ProxiedPlayer player, Rank rank) {
        this.player = player;
        this.rank = rank;
    }
}
