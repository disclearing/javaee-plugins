package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilRepairEvent;
import org.bukkit.event.inventory.PrepareItemRepairAnvilEvent;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */

public class NoEnchantsListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchant(PrepareItemEnchantEvent event) {
        if (Scenario.getByName("NoEnchants").isEnabled()|| Scenario.getByName("BuildUHC").isEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchant(EnchantItemEvent event) {
        if (Scenario.getByName("NoEnchants").isEnabled() || Scenario.getByName("BuildUHC").isEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnvil(PrepareAnvilRepairEvent event) {
        if (Scenario.getByName("NoEnchants").isEnabled() || Scenario.getByName("BuildUHC").isEnabled()) {
            event.setCancelled(true);
        }
    }
}
