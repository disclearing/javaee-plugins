package me.redis.queue.bukkit;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.redis.queue.bukkit.api.QueueAPI;
import me.redis.queue.bukkit.rank.RankManager;
import me.redis.queue.bukkit.redis.RedisMessagingHandler;
import me.redis.queue.bukkit.redis.RedisWrapper;
import me.redis.queue.bukkit.tasks.RedisInformationTask;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class QueueBukkit extends JavaPlugin implements Listener {
    @Getter private static QueueBukkit plugin;

    private RedisWrapper redisWrapper;
    private RedisMessagingHandler redisMessagingHandler;

    private RankManager rankManager;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        redisWrapper = new RedisWrapper(getConfig().getConfigurationSection("redis"));
        redisMessagingHandler = new RedisMessagingHandler(this);

        rankManager = new RankManager();

        new QueueAPI();
        new RedisInformationTask().runTaskTimerAsynchronously(this, 5L, 5L);
    }

    @Override
    public void onDisable() {
        JsonObject object = new JsonObject();

        object.addProperty("server", Bukkit.getServerName());
        object.addProperty("online", false);

        redisMessagingHandler.publish("queue:end", object.toString());
        redisMessagingHandler.unsubscribe();
        redisWrapper.getJedis().close();
    }
}
