package me.javaee.meetup.listeners.scenarios;

import me.javaee.meetup.Meetup;
import me.javaee.meetup.handlers.Scenario;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.TimeUnit;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class NoCleanListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (Scenario.getByName("NoClean").isEnabled()) {
            if (event.getEntity().getKiller() != null) {
                Player killer = event.getEntity().getKiller();

                Meetup.getPlugin().getTimerManager().getCombatTimer().setCooldown(killer, killer.getUniqueId(), TimeUnit.SECONDS.toMillis(20L), true);
            }
        }
    }
}
