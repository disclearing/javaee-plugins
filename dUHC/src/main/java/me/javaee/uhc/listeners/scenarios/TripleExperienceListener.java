package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class TripleExperienceListener implements Listener {

    @EventHandler
    public void tripleExperience(PlayerExpChangeEvent event) {
        if (Scenario.getByName("Triple Experience").isEnabled()) {
            event.setAmount(event.getAmount() * 5);
        }
    }
}
