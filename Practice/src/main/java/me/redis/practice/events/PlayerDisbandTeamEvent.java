package me.redis.practice.events;

import me.redis.practice.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerDisbandTeamEvent extends PlayerPartyEvent implements Cancellable {

    private boolean cancelled;

    public PlayerDisbandTeamEvent(Player player, Team team) {
        super(player, team);
        this.cancelled = false;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}