package me.javaee.uhc.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class PlayerDisguiseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private String disguisedName;

    public PlayerDisguiseEvent(Player player, String disguisedName) {
        super();

        this.player = player;
        this.disguisedName = disguisedName;
    }

    public Player getPlayer() {
        return player;
    }

    public String getDisguisedName() {
        return disguisedName;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
