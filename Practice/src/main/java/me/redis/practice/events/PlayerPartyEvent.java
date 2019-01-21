package me.redis.practice.events;

import lombok.Getter;
import lombok.Setter;
import me.redis.practice.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class PlayerPartyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter @Setter private Team team;
    @Getter private Player player;

    public PlayerPartyEvent(Player player, Team team) {
        this.team = team;
        this.player = player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}