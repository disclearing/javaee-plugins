package me.redis.practice.timer;

import lombok.Data;
import lombok.Getter;
import me.redis.practice.Practice;
import me.redis.practice.timer.type.EnderpearlTimer;
import me.redis.practice.timer.type.QueuesTimer;
import me.redis.practice.timer.type.RestartTimer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class TimerManager implements Listener {

    @Getter public final EnderpearlTimer enderpearlTimer;
    @Getter public final RestartTimer restartTimer;
    @Getter public final QueuesTimer queuesTimer;

    @Getter
    private final Set<Timer> timers = new LinkedHashSet<>();

    private final JavaPlugin plugin;

    public TimerManager(Practice plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
        this.registerTimer(this.enderpearlTimer = new EnderpearlTimer());
        this.registerTimer(this.restartTimer = new RestartTimer());
        this.registerTimer(this.queuesTimer = new QueuesTimer());
    }

    public void registerTimer(Timer timer) {
        this.timers.add(timer);
        if (timer instanceof Listener) {
            this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, this.plugin);
        }
    }

    public void unregisterTimer(Timer timer) {
        this.timers.remove(timer);
    }

    /**
     * Reloads the {@link Timer} data from storage.
     */

    /**
     * Saves the {@link Timer} data to storage.
     */
}
