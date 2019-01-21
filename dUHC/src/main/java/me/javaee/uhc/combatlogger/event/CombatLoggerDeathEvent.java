package me.javaee.uhc.combatlogger.event;

import lombok.Getter;
import lombok.Setter;
import me.javaee.uhc.combatlogger.CombatLogger;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;
import java.util.Optional;

public class CombatLoggerDeathEvent extends CombatLoggerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter @Setter private boolean cancelled = false;

    @Getter private Entity killer;

    public CombatLoggerDeathEvent(CombatLogger combatLogger, @Nullable Entity killer) {
        super(combatLogger);

        if (killer != null) this.killer = killer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}