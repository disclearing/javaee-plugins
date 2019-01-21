package me.javaee.uhc.events;

import me.javaee.uhc.team.UHCTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class TeamCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private UHCTeam team;
    private Player player;

    public TeamCreateEvent(Player player, UHCTeam team) {
        super();

        this.player = player;
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public UHCTeam getTeam() {
        return team;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
