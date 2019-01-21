package me.redis.practice.events;

import me.redis.practice.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerKickTeamEvent extends PlayerPartyEvent implements Cancellable {

    private boolean cancelled;
    private Player kicked;
    private boolean clean;
    private boolean announce;

    public PlayerKickTeamEvent(Player player, Player kicked, Team team, boolean clean, boolean announce) {
        super(player, team);
        this.cancelled = false;
        this.clean = false;
        this.announce = false;
        this.kicked = kicked;
        this.clean = clean;
        this.announce = announce;
    }

    public Player getKickedPlayer() {
        return this.kicked;
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