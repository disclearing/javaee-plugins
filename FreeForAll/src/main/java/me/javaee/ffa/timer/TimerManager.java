package me.javaee.ffa.timer;

import lombok.Getter;
import me.javaee.ffa.FFA;
import me.javaee.ffa.timer.type.TeleportTimer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.LinkedHashSet;
import java.util.Set;

public class TimerManager {
    @Getter private final FFA plugin;

    @Getter private TeleportTimer teleportTimer;

    @Getter private final Set<Timer> timers = new LinkedHashSet<>();

    public TimerManager(FFA plugin) {
        this.plugin = plugin;

        registerTimer(teleportTimer = new TeleportTimer(plugin));
    }

    public void registerTimer(Timer timer) {
        timers.add(timer);
        if (timer instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) timer, plugin);
        }
    }

    public void unregisterTimer(Timer timer) {
        timers.remove(timer);
    }
}