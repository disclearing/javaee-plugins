package me.javaee.ffa.events;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.javaee.ffa.combatlogger.CombatLogger;
import org.bukkit.event.Event;

public abstract class CombatLoggerEvent extends Event {

    @Getter protected final CombatLogger combatLogger;

    public CombatLoggerEvent(CombatLogger combatLogger) {
        this.combatLogger = Preconditions.checkNotNull(combatLogger, "CombatLogger cannot be null");
    }
}
