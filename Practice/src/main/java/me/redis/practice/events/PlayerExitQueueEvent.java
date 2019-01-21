package me.redis.practice.events;

import me.redis.practice.queue.IQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerExitQueueEvent extends PlayerQueueEvent implements Cancellable {

    private static final HandlerList handlers;
    private boolean cancelled;
    
    public PlayerExitQueueEvent(Player player, IQueue queue) {
        super(player, queue);
        this.cancelled = false;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    @Override
    public HandlerList getHandlers() {
        return PlayerExitQueueEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerExitQueueEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }

}