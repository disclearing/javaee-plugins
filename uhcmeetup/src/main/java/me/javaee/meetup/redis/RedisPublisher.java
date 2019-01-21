package me.javaee.meetup.redis;

import me.javaee.meetup.Meetup;
import redis.clients.jedis.Jedis;

/*
 * Copyright (c) 2017, Redis. All rights reserved.
 */

public class RedisPublisher {
    public void write(String message) {
        Jedis jedis = null;

        try {
            jedis = Meetup.getPlugin().getPool().getResource();

            jedis.publish("uhc", message);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
