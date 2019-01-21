package me.redis.queue.bukkit.rank;

import lombok.Getter;
import me.redis.queue.bukkit.QueueBukkit;

@Getter
public class Rank {
    private final String name;
    private final String permission;
    private final int priority;

    public Rank(String name, String permission, int priority) {
        this.name = name;
        this.permission = permission;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return permission;
    }
}