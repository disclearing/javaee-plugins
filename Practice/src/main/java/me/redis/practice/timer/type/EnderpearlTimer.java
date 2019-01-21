package me.redis.practice.timer.type;

import me.redis.practice.Practice;
import me.redis.practice.timer.PlayerTimer;
import me.redis.practice.timer.TimerCooldown;
import me.redis.practice.utils.DurationFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EnderpearlTimer extends PlayerTimer implements Listener {
    public EnderpearlTimer() {
        super("Enderpearl", TimeUnit.SECONDS.toMillis(15L));
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD;
    }

    @Override
    public void onExpire(UUID userUUID) {
        super.onExpire(userUUID);
        Player player = Bukkit.getPlayer(userUUID);
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + "Your " + getDisplayName() + ChatColor.GREEN + " timer has expired. You may now Enderpearl again.");
        }
    }

    @Override
    public TimerCooldown clearCooldown(UUID playerUUID) {
        TimerCooldown runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            return runnable;
        }

        return null;
    }

    @Override
    public void clearCooldown(Player player) {
        super.clearCooldown(player);
    }

    public void refund(Player player) {
        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
        clearCooldown(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        clearCooldown(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        clearCooldown(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                long remaining = getRemaining(player);
                if (remaining > 0L) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are still on " + getDisplayName() + ChatColor.RED + " cooldown for another " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + '.');
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl) {
            EnderPearl enderPearl = (EnderPearl) projectile;
            if (enderPearl.getShooter() instanceof Player) {
                Player shooter = (Player) enderPearl.getShooter();
                if (getRemaining(shooter) <= 0L) {
                    setCooldown(shooter, shooter.getUniqueId(), defaultCooldown, true);
                }
            }
        }
    }
}