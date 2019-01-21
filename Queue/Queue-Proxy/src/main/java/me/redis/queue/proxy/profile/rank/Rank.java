package me.redis.queue.proxy.profile.rank;

import lombok.Getter;
import me.redis.queue.proxy.QueueProxy;

@Getter
public class Rank {
    private String name;
    private String permission;
    private int priority;

    public Rank(String name, String permission, int priority) {
        this.name = name;
        this.permission = permission;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return this.permission;
    }
}