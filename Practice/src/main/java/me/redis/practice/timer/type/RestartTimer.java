package me.redis.practice.timer.type;

import me.redis.practice.Practice;
import me.redis.practice.timer.GlobalTimer;
import me.redis.practice.events.TimerExpireEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class RestartTimer extends GlobalTimer implements Listener {
    public RestartTimer() {
        super("Restart", TimeUnit.MINUTES.toMillis(5));

        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
    }

    @Override
    public String getScoreboardPrefix() {
        return null;
    }

    @EventHandler
    public void stopTimer(TimerExpireEvent event) {
        if (event.getTimer() instanceof RestartTimer) {
            Bukkit.shutdown();
        }
    }
}
