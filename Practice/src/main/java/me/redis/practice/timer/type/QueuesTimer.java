package me.redis.practice.timer.type;

import me.redis.practice.Practice;
import me.redis.practice.timer.GlobalTimer;
import me.redis.practice.events.TimerExpireEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class QueuesTimer extends GlobalTimer implements Listener {
    public QueuesTimer() {
        super("queues", TimeUnit.MINUTES.toMillis(1));

        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
    }

    @Override
    public String getScoreboardPrefix() {
        return null;
    }

    @EventHandler
    public void stopTimer(TimerExpireEvent event) {
        if (event.getTimer() instanceof QueuesTimer) {
            Practice.getPlugin().getQueueManager().restartQueues();
        }
    }
}
