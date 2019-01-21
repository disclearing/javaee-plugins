package me.redis.queue.app.api.manager;

import lombok.Getter;
import me.redis.queue.app.api.Queue;

import java.util.HashSet;
import java.util.Set;

public class QueueManager {
    @Getter private final Set<Queue> queues = new HashSet<>();

    public Queue getByServer(String server) {
        for (Queue queue : queues) {
            if (queue.getServer().equalsIgnoreCase(server)) return queue;
        }

        return null;
    }
}
