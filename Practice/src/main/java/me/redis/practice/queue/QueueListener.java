package me.redis.practice.queue;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.profile.Profile;
import me.redis.practice.events.PlayerEnterQueueEvent;
import me.redis.practice.events.PlayerExitQueueEvent;
import me.redis.practice.utils.PracticeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueListener implements Listener {
    @EventHandler
    public void onEnterQueue(PlayerEnterQueueEvent event) {
        PracticeUtils.resetPlayer(event.getPlayer());

        event.getPlayer().getInventory().setContents(PracticeUtils.getQueueInventory());
        event.getPlayer().updateInventory();
    }

    @EventHandler
    public void onExitQueue(PlayerExitQueueEvent event) {
        Player player = event.getPlayer();
        Profile playerData = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        event.getQueue().removeFromQueue(playerData.getQueueData());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);

        event.setQuitMessage(null);

        if (profile.getStatus() == ProfileStatus.QUEUE) {
            PlayerExitQueueEvent queueEvent = new PlayerExitQueueEvent(player, profile.getCurrentQueue());
            Bukkit.getPluginManager().callEvent(queueEvent);
        } else if (profile.getStatus() == ProfileStatus.SPECTATOR) {
            Practice.getPlugin().getSpectatorManager().stopSpectating(player, false);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }
}