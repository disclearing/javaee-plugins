package me.redis.kohi.listeners;

import me.redis.kohi.SurvivalGames;
import me.redis.kohi.database.profiles.Profile;
import me.redis.kohi.database.profiles.status.PlayerStatus;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Iterator;

public class SpectatorListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

        if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
            if (event.getMessage().startsWith("!")) {
                event.setFormat(event.getFormat().replace("!", ""));
                return;
            }

            for (Iterator<Player> it = event.getRecipients().iterator(); it.hasNext(); ) {
                Player recipent = it.next();
                Profile recipentProfile = SurvivalGames.getPlugin().getProfileManager().getProfile(recipent);

                if (recipentProfile.getPlayerStatus() == PlayerStatus.PLAYING) {
                    it.remove();
                }
            }

            event.setFormat(ChatColor.GRAY + "[Spectator] " + event.getFormat());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getInventory() != null) {
            Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

            if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

        if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

        if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

        if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

        if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

            if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

            if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
                event.setCancelled(true);
            }
        }

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

            if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
                event.setCancelled(true);
            }
        }
    }
}
