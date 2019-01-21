package me.javaee.uhc.events;

import me.javaee.uhc.team.UHCTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamLeaveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private UHCTeam team;
    private Player player;

    public TeamLeaveEvent(Player player, UHCTeam team) {
        super();

        this.player = player;
        this.team = team;
    }

    public UHCTeam getTeam() {
        return team;
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
