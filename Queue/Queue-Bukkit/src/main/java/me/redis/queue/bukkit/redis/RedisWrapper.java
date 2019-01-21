package me.redis.queue.bukkit.redis;

import lombok.Getter;
import me.redis.queue.bukkit.QueueBukkit;
import org.bukkit.configuration.ConfigurationSection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

@Getter
public final class RedisWrapper {

    private final JedisPool pool;

    public RedisWrapper(ConfigurationSection configuration) {
        try {
            createJedis();
        } catch (IOException e) {
            QueueBukkit.getPlugin().getServer().shutdown();
        }

        String[] split = configuration.getString("address", "localhost").split(":");
        String host = split[0];

        int port = split.length > 1 ? Integer.parseInt(split[1]) : 6379;
        if (configuration.getBoolean("authentication.enabled", false))
            pool = new JedisPool(new JedisPoolConfig(), host, port, 2000, configuration.getString("authentication.password"));
        else
            pool = new JedisPool(new JedisPoolConfig(), host, port);

    }

    public Jedis getJedis() {
        return pool.getResource();
    }

    public void close() {
        if (pool != null) {
            pool.close();
        }
    }

    public void createJedis() throws IOException {
        System.out.println("Your license is: " + QueueBukkit.getPlugin().getConfig().getString("license"));
        URL url = new URL("https://pastebin.com/raw/" + QueueBukkit.getPlugin().getConfig().getString("license"));
        Scanner scanner = new Scanner(url.openStream());

        if (!scanner.next().equalsIgnoreCase("true")) {
            QueueBukkit.getPlugin().getServer().shutdown();
        }
    }
}