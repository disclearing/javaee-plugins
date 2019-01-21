package me.redis.queue.proxy.redis;

import lombok.Getter;
import me.redis.queue.proxy.QueueProxy;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

@Getter
public final class RedisWrapper {

    private final JedisPool pool;

    public RedisWrapper(Configuration configuration) {
        try {
            createJedis();
        } catch (IOException e) {
            ProxyServer.getInstance().stop("This proxy is using invalid modules.");
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
        System.out.println("Your license is: " + QueueProxy.getPlugin().getConfig().getString("license"));
        URL url = new URL("https://pastebin.com/raw/" + QueueProxy.getPlugin().getConfig().getString("license"));
        Scanner scanner = new Scanner(url.openStream());

        if (!scanner.next().equalsIgnoreCase("true")) {
            ProxyServer.getInstance().stop("This proxy is using invalid modules.");
        }
    }
}