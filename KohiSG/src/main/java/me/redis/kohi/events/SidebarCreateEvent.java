package me.redis.kohi.events;

import me.redis.kohi.scoreboard.board.Board;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SidebarCreateEvent extends Event {
    @Getter private final Board board;
    @Getter private final Player player;

    private static final HandlerList handlers = new HandlerList();

    public SidebarCreateEvent(Board board, Player player) {
        this.board = board;
        this.player = player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
