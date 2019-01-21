package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.UHC;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.handlers.Scenario;
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

                UHC.getInstance().getTimerManager().getNocleanTimer().setCooldown(killer, killer.getUniqueId(), TimeUnit.SECONDS.toMillis(20L), true);
            }
        } else if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            if (event.getEntity().getKiller() != null) {
                Player killer = event.getEntity().getKiller();

                UHC.getInstance().getTimerManager().getNocleanTimer().setCooldown(killer, killer.getUniqueId(), TimeUnit.MILLISECONDS.toMillis(10000L), true);
            }
        }
    }
}
