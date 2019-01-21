package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class NoFallListener implements Listener {

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (Scenario.getByName("NoFall").isEnabled()) {
            if (event.getEntity() instanceof Player) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
