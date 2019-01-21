package me.redis.queue.proxy.tasks;

import com.google.gson.JsonObject;
import me.redis.queue.proxy.QueueProxy;
import me.redis.queue.proxy.profile.QueuedPlayer;
import me.redis.queue.proxy.queue.Queue;

import java.util.ArrayList;
import java.util.List;

public class QueueAPITask implements Runnable {

    @Override
    public void run() {
        for (Queue queue : QueueProxy.getPlugin().getQueueManager().getQueues()) {
            JsonObject object = new JsonObject();

            object.addProperty("server", queue.getServer());

            List<String> list = new ArrayList<>();
            for (QueuedPlayer queuedPlayer : queue.getQueuedPlayers()) {
                list.add(queuedPlayer.getPlayer().getName() + "@" + queue.getPosition(queuedPlayer.getPlayer()));
            }

            object.addProperty("players", list.toString());

            QueueProxy.getPlugin().getRedisMessagingHandler().sendMessage("queue:api", object.toString());
        }
    }
}
