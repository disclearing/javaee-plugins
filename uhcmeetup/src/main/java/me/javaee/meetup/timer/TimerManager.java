package me.javaee.meetup.timer;

import lombok.Data;
import lombok.Getter;
import me.javaee.meetup.Meetup;
import me.javaee.meetup.timer.type.NoCleanTimer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class TimerManager implements Listener {

    @Getter public final NoCleanTimer combatTimer;

    @Getter
    private final Set<Timer> timers = new LinkedHashSet<>();

    private final JavaPlugin plugin;

    public TimerManager(Meetup plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
        this.registerTimer(this.combatTimer = new NoCleanTimer(plugin));
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
