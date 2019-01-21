package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import org.bukkit.ChatColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class HorseLessListener implements Listener {

    @EventHandler
    public void onRide(EntityMountEvent event) {
        if (Scenario.getByName("Horseless").isEnabled()) {
            if (event.getMount() instanceof Horse && event.getEntity() instanceof Player) {
                event.setCancelled(true);
                ((Player) event.getEntity()).sendMessage(ChatColor.WHITE + "Horseless " + ChatColor.GOLD + "is enabled, you can't ride horses.");
            }
        }
    }
}
