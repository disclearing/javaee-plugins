package me.javaee.uhc.timer.type;

import com.google.common.base.Optional;
import me.javaee.uhc.UHC;
import me.javaee.uhc.events.NoCleanTimerStartEvent;
import me.javaee.uhc.events.NoCleanTimerStopEvent;
import me.javaee.uhc.timer.PlayerTimer;
import me.javaee.uhc.timer.event.TimerClearEvent;
import me.javaee.uhc.timer.event.TimerStartEvent;
import me.javaee.uhc.utils.BukkitUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Timer used to tag {@link Player}s in combat to prevent entering safe-zones.
 */
public class CombatTagTimer extends PlayerTimer implements Listener {

    private final UHC plugin;

    public CombatTagTimer(UHC plugin) {
        super("Combat", TimeUnit.SECONDS.toMillis(30L));
        this.plugin = plugin;
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.RED.toString() + ChatColor.BOLD;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(TimerClearEvent event) {
        if (event.getTimer() == this) {
            Optional<UUID> optionalUserUUID = event.getUserUUID();
            if (optionalUserUUID.isPresent()) {
                this.onExpire(optionalUserUUID.get());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPunch(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacked = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            setCooldown(attacked, attacked.getUniqueId());
            setCooldown(attacker, attacker.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        clearCooldown(event.getPlayer().getUniqueId());
    }
}
