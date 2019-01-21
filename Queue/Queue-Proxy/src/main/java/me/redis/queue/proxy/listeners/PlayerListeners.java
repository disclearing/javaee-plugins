package me.redis.queue.proxy.listeners;

import me.redis.queue.proxy.QueueProxy;
import me.redis.queue.proxy.queue.Queue;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListeners implements Listener {
    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (QueueProxy.getPlugin().getQueueManager().getByPlayer(player) != null) {
            Queue queue = QueueProxy.getPlugin().getQueueManager().getByPlayer(player);

            QueueProxy.getPlugin().getQueueManager().getByPlayer(player).getQueuedPlayers().remove(queue.getRealPosition(player));
        }
    }
}
