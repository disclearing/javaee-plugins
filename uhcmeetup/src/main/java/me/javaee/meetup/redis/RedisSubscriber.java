package me.javaee.meetup.redis;

import lombok.Getter;
import me.javaee.meetup.Meetup;
import redis.clients.jedis.JedisPubSub;

/*
 * Copyright (c) 2017, Redis. All rights reserved.
 */

public class RedisSubscriber {
    @Getter private JedisPubSub jedisPubSub;

    public RedisSubscriber() {
        subscribe();
    }

    private void subscribe() {
        jedisPubSub = get();

        new Thread(() -> Meetup.getPlugin().getPool().getResource().subscribe(jedisPubSub, "uhc")).start();
    }

    private JedisPubSub get() {
        return new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                String[] args;

                if (channel.equalsIgnoreCase("uhc") && (args = message.split(";")).length >= 1) {
                    String type = args[0];

                    if (type.equalsIgnoreCase("meetup")) {
                        System.out.println("yeah boi");
                    }
                }
            }
        };
    }
}

