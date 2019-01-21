package me.javaee.uhc.events;

import lombok.Getter;
import me.javaee.uhc.handlers.Scenario;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ScenarioEnableEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter private Scenario scenario;

    public ScenarioEnableEvent(Scenario scenario) {
        this.scenario = scenario;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
