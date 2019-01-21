package me.redis.kohi.timer.type;

import com.google.common.base.Optional;
import me.redis.kohi.SurvivalGames;
import me.redis.kohi.timer.PlayerTimer;
import me.redis.kohi.timer.event.TimerClearEvent;
import me.redis.kohi.timer.event.TimerStartEvent;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
public class ProtectionTimer extends PlayerTimer implements Listener {
    private final SurvivalGames plugin;

    public ProtectionTimer(SurvivalGames plugin) {
        super("Protection", TimeUnit.MINUTES.toMillis(3));
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

       if (event.getEntity() instanceof Player) {
           Player attacked = (Player) event.getEntity();

           if (SurvivalGames.getPlugin().getTimerManager().getProtection().getRemaining(attacked) > 0) {
               event.setCancelled(true);
           }
       }
   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player attacked = (Player) event.getEntity();

            if (SurvivalGames.getPlugin().getTimerManager().getProtection().getRemaining(attacked) != 0) {
                if (event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) event.setCancelled(true);
            }
        }
   }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        clearCooldown(event.getPlayer().getUniqueId());
    }
}
