package me.redis.queue.proxy.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.redis.queue.proxy.QueueProxy;
import me.redis.queue.proxy.profile.QueuedPlayer;
import me.redis.queue.proxy.profile.rank.Rank;
import me.redis.queue.proxy.queue.Queue;
import me.redis.queue.proxy.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisMessagingHandler {
    private final QueueProxy plugin;
    private JedisPubSub subscriber;

    public RedisMessagingHandler(QueueProxy plugin) {
        this.plugin = plugin;

        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try (Jedis jedis = plugin.getRedisWrapper().getJedis()) {
                jedis.subscribe(subscriber = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        JsonObject data = new JsonParser().parse(message).getAsJsonObject();

                        switch (channel) {
                            case "queue:information": {
                                String server = data.get("server").getAsString();
                                int maxPlayers = data.get("maxPlayers").getAsInt();
                                int onlinePlayers = data.get("onlinePlayers").getAsInt();
                                boolean online = data.get("online").getAsBoolean();
                                boolean whitelisted = data.get("whitelisted").getAsBoolean();

                                if (plugin.getQueueManager().getByServer(server) != null) {
                                    Queue queue = plugin.getQueueManager().getByServer(server);

                                    queue.setMaxPlayers(maxPlayers);
                                    queue.setOnlinePlayers(onlinePlayers);
                                    queue.setWhitelisted(whitelisted);
                                    queue.setOnline(online);
                                }

                                break;
                            }

                            case "queue:end": {
                                String server = data.get("server").getAsString();
                                boolean online = data.get("online").getAsBoolean();

                                if (plugin.getQueueManager().getByServer(server) != null) {
                                    Queue queue = plugin.getQueueManager().getByServer(server);

                                    queue.setOnline(online);
                                }

                                break;
                            }

                            case "queue:add:get": {
                                String playerName = data.get("playerName").getAsString();
                                String server = data.get("server").getAsString();
                                String rankPermission = data.get("rank").getAsString();

                                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
                                Rank rank = plugin.getRankManager().getRank(rankPermission);

                                if (player != null && plugin.getQueueManager().getByPlayer(player) == null) {
                                    QueuedPlayer queuedPlayer = new QueuedPlayer(player, rank);
                                    Queue queue = plugin.getQueueManager().getByServer(server);

                                    if (queue == null) {
                                        System.out.println("[QueueSystem] A server tried to add " + playerName + " to a queue named '" + server + "' but it doesn't exist.");
                                        return;
                                    }

                                    queue.getQueuedPlayers().add(plugin.getQueueManager().findIndex(queue, rank), queuedPlayer);
                                    player.sendMessage(Messages.POSITION.toString().replace("%player_position%", String.valueOf(queue.getPosition(player))).replace("%player_queue%", queue.getServer()));
                                } else {
                                    System.out.println("[QueueSystem] A server tried to add " + playerName + " to a queue, but they are already in one.");
                                }

                                break;
                            }
                        }
                    }
                }, "queue:information", "queue:end", "queue:add:get");
            }
        });
    }

    public void unsubscribe() {
        plugin.getLogger().info("Closing Redis messaging service...");
        subscriber.unsubscribe();
    }

    public void sendMessage(String channel, String message) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {

            try (Jedis jedis = plugin.getRedisWrapper().getJedis()) {
                jedis.publish(channel, message);
            }
        });
    }

}
