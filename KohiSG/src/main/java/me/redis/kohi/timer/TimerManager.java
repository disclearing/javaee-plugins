package me.redis.kohi.timer;

import lombok.Getter;
import me.redis.kohi.SurvivalGames;
import me.redis.kohi.timer.type.EnderpearlTimer;
import me.redis.kohi.timer.type.FeastTimer;
import me.redis.kohi.timer.type.ProtectionTimer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.LinkedHashSet;
import java.util.Set;

public class TimerManager {
    @Getter private final SurvivalGames plugin;

    @Getter private final ProtectionTimer protection;
    @Getter private final EnderpearlTimer enderpearlTimer;
    @Getter private final FeastTimer feastTimer;

    @Getter private final Set<Timer> timers = new LinkedHashSet<>();

    public TimerManager(SurvivalGames plugin) {
        this.plugin = plugin;

        registerTimer(protection = new ProtectionTimer(plugin));
        registerTimer(enderpearlTimer = new EnderpearlTimer());
        registerTimer(feastTimer = new FeastTimer(plugin));
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