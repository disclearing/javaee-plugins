package me.javaee.uhc.timer;

import lombok.Data;
import lombok.Getter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.timer.type.CombatTagTimer;
import me.javaee.uhc.timer.type.NoCleanTimer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class TimerManager implements Listener {

    @Getter public final NoCleanTimer nocleanTimer;
    @Getter public final CombatTagTimer combatTagTimer;

    @Getter
    private final Set<Timer> timers = new LinkedHashSet<>();

    private final JavaPlugin plugin;

    public TimerManager(UHC plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
        this.registerTimer(this.nocleanTimer = new NoCleanTimer(plugin));
        this.registerTimer(this.combatTagTimer = new CombatTagTimer(plugin));
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
