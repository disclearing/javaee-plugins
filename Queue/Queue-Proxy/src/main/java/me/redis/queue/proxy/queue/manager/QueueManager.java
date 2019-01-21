package me.redis.queue.proxy.queue.manager;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.redis.queue.proxy.QueueProxy;
import me.redis.queue.proxy.profile.QueuedPlayer;
import me.redis.queue.proxy.profile.rank.Rank;
import me.redis.queue.proxy.queue.Queue;
import me.redis.queue.proxy.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class QueueManager {
    @Getter private final Set<Queue> queues = new HashSet<>();

    public QueueManager() {
        ProxyServer.getInstance().getScheduler().schedule(QueueProxy.getPlugin(), () -> {
            for (Queue queue : queues) {
                if (!queue.getQueuedPlayers().isEmpty()) {
                    ProxiedPlayer player = queue.getQueuedPlayers().get(0).getPlayer();
                    ServerInfo info = ProxyServer.getInstance().getServerInfo(queue.getServer());

                    if (info != null) {
                        if (player.hasPermission("queue.permission.bypass")) {
                            queue.getQueuedPlayers().remove(queue.getQueuedPlayers().get(0));

                            player.sendMessage(Messages.SENT_TO_SERVER.toString().replace("%player_queue%", queue.getServer()));
                            player.connect(info);
                        } else {
                            if (!queue.isPaused() && !queue.isWhitelisted() && queue.getOnlinePlayers() < queue.getMaxPlayers()) {
                                queue.getQueuedPlayers().remove(queue.getQueuedPlayers().get(0));

                                player.sendMessage(Messages.SENT_TO_SERVER.toString().replace("%player_queue%", queue.getServer()));
                                player.connect(info);
                            }
                        }
                    } else {
                        player.sendMessage(Messages.COULD_NOT_LOCATE.toString());
                    }
                }
            }
        }, 1L, 1L, TimeUnit.SECONDS);

        ProxyServer.getInstance().getScheduler().schedule(QueueProxy.getPlugin(), () -> {
            for (Queue queue : queues) {
                if (!queue.getQueuedPlayers().isEmpty()) {
                    for (QueuedPlayer queuedPlayer : queue.getQueuedPlayers()) {
                        queuedPlayer.getPlayer().sendMessage(Messages.POSITION.toString().replace("%player_position%", String.valueOf(queue.getPosition(queuedPlayer.getPlayer()))).replace("%player_queue%", queue.getServer()));

                        if (queuedPlayer.getRank() == null) { //In redis, I send a message from the bungee to the server, and if they have a permission that matches any of this it retrieves the rank name and if they don't it returns "null"
                            queuedPlayer.getPlayer().sendMessage(Messages.NO_RANK.toString());
                        } else {
                            queuedPlayer.getPlayer().sendMessage(Messages.HAS_RANK.toString().replace("%player_rank%", queuedPlayer.getRank().getName()).replace("%infront%", String.valueOf(getInFrontPlayers(queuedPlayer, queue))));
                        }
                    }
                }
            }
        }, 8L, 7L, TimeUnit.SECONDS);
    }

    public Queue getByServer(String server) {
        for (Queue queue : queues) {
            if (queue.getServer().equalsIgnoreCase(server)) return queue;
        }

        return null;
    }

    public Queue getByPlayer(ProxiedPlayer player) {
        for (Queue queue : queues) {
            if (queue.getQueuedPlayers().stream().filter(qPlayer -> qPlayer.getPlayer().getUniqueId().equals(player.getUniqueId())).findAny().orElse(null) != null) return queue;
        }

        return null;
    }

    public void requestJoin(Queue queue, ProxiedPlayer player) {
        JsonObject object = new JsonObject();

        object.addProperty("server", queue.getServer());
        object.addProperty("playerName", player.getName());

        QueueProxy.getPlugin().getRedisMessagingHandler().sendMessage("queue:add:send", object.toString());
    }

    public int findIndex(Queue queue, Rank rank) {
        if (queue.getQueuedPlayers().isEmpty()) {
            return 0;
        }

        if (rank == null) {
            return queue.getQueuedPlayers().size();
        }

        for (int i = 0; i < queue.getQueuedPlayers().size(); ++i) {
            QueuedPlayer queuedPlayer = queue.getQueuedPlayers().get(i);

            if (queuedPlayer.getRank() == null || queuedPlayer.getRank().getPriority() < rank.getPriority()) {
                return i;
            }
        }

        return queue.getQueuedPlayers().size();
    }

    public int getInFrontPlayers(QueuedPlayer queuedPlayer, Queue queue) {
        int position = queue.getPosition(queuedPlayer.getPlayer());
        int queued = queue.getQueuedPlayers().size();

        return position - queued;
    }
}
