package me.redis.queue.app.redis;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public final class RedisWrapper {
    private final JedisPool pool;

    public RedisWrapper() {
        pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
    }

    public Jedis getJedis() {
        return pool.getResource();
    }

    public void close() {
        pool.close();
    }
}