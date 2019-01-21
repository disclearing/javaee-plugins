package me.redis.queue.app.api;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

@Getter @Setter
public class Queue {
    private final String server;
    private int maxPlayers;
    private int onlinePlayers;
    private boolean paused;
    private boolean online;
    private boolean whitelisted;
    private int inQueue;

    public Queue(String server) {
        this.server = server;
    }
}
