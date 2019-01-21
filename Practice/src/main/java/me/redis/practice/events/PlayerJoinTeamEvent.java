package me.redis.practice.events;

import me.redis.practice.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerJoinTeamEvent extends PlayerPartyEvent implements Cancellable {
    private boolean cancelled;
    private boolean announce;

    public PlayerJoinTeamEvent(Player player, Team team, boolean announce) {
        super(player, team);

        this.cancelled = false;
        this.announce = false;
        this.announce = announce;
    }

    public boolean shouldAnnounce() {
        return this.announce;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}