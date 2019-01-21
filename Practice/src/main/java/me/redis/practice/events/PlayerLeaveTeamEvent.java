package me.redis.practice.events;

import me.redis.practice.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerLeaveTeamEvent extends PlayerPartyEvent implements Cancellable {

    private boolean cancelled;
    private boolean clean;
    private boolean announce;

    public PlayerLeaveTeamEvent(Player player, Team team, boolean clean, boolean announce) {
        super(player, team);
        this.cancelled = false;
        this.clean = false;
        this.announce = false;
        this.clean = clean;
        this.announce = announce;
    }

    public boolean shouldClean() {
        return this.clean;
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