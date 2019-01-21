package me.redis.queue.bukkit.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.redis.queue.bukkit.QueueBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RedisMessagingHandler {
    private final QueueBukkit plugin;
    private JedisPubSub subscriber;

    @Getter private Map<String, String> queues = new HashMap<>();

    public RedisMessagingHandler(final QueueBukkit plugin) {
        this.plugin = plugin;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = plugin.getRedisWrapper().getJedis()) {
                jedis.subscribe(subscriber = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        JsonObject data = new JsonParser().parse(message).getAsJsonObject();

                        switch (channel) {
                            case "queue:api": {
                                String server = data.get("server").getAsString();
                                String players = data.get("players").getAsString();

                                queues.put(server, players);
                                break;
                            }

                            case "queue:add:send": {
                                String server = data.get("server").getAsString();
                                String playerName = data.get("playerName").getAsString();

                                Player player = Bukkit.getPlayer(playerName);

                                if (player != null) {
                                    JsonObject object = new JsonObject();
                                    object.addProperty("server", server);
                                    object.addProperty("playerName", playerName);
                                    object.addProperty("rank", plugin.getRankManager().getRank(player) == null ? "null" : plugin.getRankManager().getRank(player).getName());

                                    sendMessage("queue:add:get", object.toString());
                                }

                                break;
                            }
                        }
                    }
                }, "queue:information", "queue:api", "queue:add:send");
            }
        });
    }

    public void unsubscribe() {
        plugin.getLogger().info("Closing Redis messaging service...");
        subscriber.unsubscribe();
    }

    public void sendMessage(String channel, String message) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = plugin.getRedisWrapper().getJedis()) {
                jedis.publish(channel, message);
            }
        });
    }

    public void publish(String channel, String message) {
        try (Jedis jedis = plugin.getRedisWrapper().getJedis()) {
            jedis.publish(channel, message);
        }
    }

    protected HashMap<String,String> convertToStringToHashMap(String text){
        HashMap<String,String> data = new HashMap<>();
        Pattern p = Pattern.compile("[\\{\\}\\=\\, ]++");
        String[] split = p.split(text);
        for ( int i=1; i+2 <= split.length; i+=2 ){
            data.put( split[i], split[i+1] );
        }
        return data;
    }
}
