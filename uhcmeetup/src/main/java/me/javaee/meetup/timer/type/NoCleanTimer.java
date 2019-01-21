package me.javaee.meetup.timer.type;

import com.google.common.base.Optional;
import me.javaee.meetup.Meetup;
import me.javaee.meetup.timer.PlayerTimer;
import me.javaee.meetup.timer.event.TimerClearEvent;
import me.javaee.meetup.timer.event.TimerStartEvent;
import me.javaee.meetup.utils.BukkitUtils;
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
public class NoCleanTimer extends PlayerTimer implements Listener {

    private final Meetup plugin;

    public NoCleanTimer(Meetup plugin) {
        super("No Clean", TimeUnit.SECONDS.toMillis(20L));
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

    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);

        player.sendMessage(ChatColor.RED + "You don't have your no clean anymore.");
    }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
   public void onPunch(EntityDamageByEntityEvent event) {
       Player attacker = BukkitUtils.getFinalAttacker(event, true);

       if (event.getEntity() instanceof Player) {
           Player attacked = (Player) event.getEntity();

           if (Meetup.getPlugin().getTimerManager().getCombatTimer().getRemaining(attacked) > 0) {
               attacker.sendMessage(ChatColor.RED + "That player has their no clean timer.");
               event.setCancelled(true);
               return;
           }

           if (attacker != null) {
               if (Meetup.getPlugin().getTimerManager().getCombatTimer().getRemaining(attacker) != 0) {
                   Meetup.getPlugin().getTimerManager().getCombatTimer().clearCooldown(attacker);
               }
           }
       }
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
   public void onWater(PlayerBucketEmptyEvent event) {
       Player player = event.getPlayer();

       if (Meetup.getPlugin().getTimerManager().getCombatTimer().getRemaining(player) > 0) {
           Meetup.getPlugin().getTimerManager().getCombatTimer().clearCooldown(player);
       }
   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player attacked = (Player) event.getEntity();

            if (Meetup.getPlugin().getTimerManager().getCombatTimer().getRemaining(attacked) != 0) {
                event.setCancelled(true);
            }
        }
   }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStart(TimerStartEvent event) {
        if (event.getTimer() == this) {
            Optional<Player> optional = event.getPlayer();
            if (optional.isPresent()) {
                Player player = optional.get();
                player.sendMessage(ChatColor.YELLOW + "You now have your no clean timer for " + ChatColor.RED + DurationFormatUtils.formatDurationWords(event.getDuration(), true, true) + ChatColor.YELLOW + '.');
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        clearCooldown(event.getPlayer().getUniqueId());
    }
}
