package me.redis.queue.bukkit.tasks;

import com.google.gson.JsonObject;
import me.redis.queue.bukkit.QueueBukkit;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class RedisInformationTask extends BukkitRunnable {

    @Override
    public void run() {
        JsonObject object = new JsonObject();

        object.addProperty("server", Bukkit.getServerName());
        object.addProperty("maxPlayers", Bukkit.getMaxPlayers());
        object.addProperty("onlinePlayers", Bukkit.getOnlinePlayers().size());
        object.addProperty("online", true);
        object.addProperty("whitelisted", Bukkit.hasWhitelist());

        QueueBukkit.getPlugin().getRedisMessagingHandler().sendMessage("queue:information", object.toString());
    }
}
