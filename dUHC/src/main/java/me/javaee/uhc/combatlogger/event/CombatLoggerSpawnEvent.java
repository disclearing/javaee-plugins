package me.javaee.uhc.combatlogger.event;

import lombok.Getter;
import lombok.Setter;
import me.javaee.uhc.combatlogger.CombatLogger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class CombatLoggerSpawnEvent extends CombatLoggerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter @Setter private boolean cancelled;

    public CombatLoggerSpawnEvent(CombatLogger combatLogger) {
        super(combatLogger);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}