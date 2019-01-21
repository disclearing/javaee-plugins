package me.redis.practice.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerCreateTeamEvent extends PlayerPartyEvent implements Cancellable {

    private boolean cancelled;

    public PlayerCreateTeamEvent(Player player) {
        super(player, null);
        this.cancelled = false;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}