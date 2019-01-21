package me.redis.queue.bukkit.api;

import com.google.gson.JsonObject;
import me.redis.queue.bukkit.QueueBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class QueueAPI {
    public static String getQueueByPlayer(Player player) {
        for (String queue : QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().keySet()) {
            ArrayList<String> queuedPlayers = new ArrayList(Arrays.asList(QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().get(queue).substring(1, QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().get(queue).length() - 1).replaceAll("\\s", "").split(",")));

            for (String name : queuedPlayers) {
                if (name.toLowerCase().contains(player.getName().toLowerCase())) return queue;
            }
        }

        return null;
    }

    public static int getPositionByPlayer(Player player) {
        for (String queue : QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().keySet()) {
            ArrayList<String> queuedPlayers = new ArrayList(Arrays.asList(QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().get(queue).substring(1, QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().get(queue).length() - 1).replaceAll("\\s", "").split(",")));

            for (String name : queuedPlayers) {
                if (name.toLowerCase().contains(player.getName().toLowerCase())) return Integer.parseInt(name.split("@")[1]);
            }
        }

        return -1;
    }

    public static int getQueuedPlayers(String queueName) {
        for (String queue : QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().keySet()) {
            if (queue.equalsIgnoreCase(queueName)) {
                return new ArrayList(Arrays.asList(QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().get(queue).substring(1, QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().get(queue).length() - 1).replaceAll("\\s", "").split(","))).size();
            }
        }

        return -1;
    }

    public static boolean isInQueue(Player player) {
        for (String queue : QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().keySet()) {
            ArrayList<String> queuedPlayers = new ArrayList(Arrays.asList(QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().get(queue).substring(1, QueueBukkit.getPlugin().getRedisMessagingHandler().getQueues().get(queue).length() - 1).replaceAll("\\s", "").split(",")));

            for (String name : queuedPlayers) {
                if (name.toLowerCase().contains(player.getName().toLowerCase())) return true;
            }
        }

        return false;
    }

    public static void addToQueue(String queue, Player player) {
        if (player != null && getQueueByPlayer(player) == null) {
            JsonObject object = new JsonObject();
            object.addProperty("server", queue);
            object.addProperty("playerName", player.getName());
            object.addProperty("rank", QueueBukkit.getPlugin().getRankManager().getRank(player) == null ? "null" : QueueBukkit.getPlugin().getRankManager().getRank(player).getName());

            QueueBukkit.getPlugin().getRedisMessagingHandler().sendMessage("queue:add:get", object.toString());
        }
    }
}
