package me.javaee.uhc.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NoCleanTimerStopEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;

    public NoCleanTimerStopEvent(Player player) {
        super();

        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
