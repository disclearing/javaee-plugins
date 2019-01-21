package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class HasteyBoysListener implements Listener {

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (Scenario.getByName("HasteyBoys").isEnabled()) {
            ItemStack item = event.getCurrentItem();

            if (item.getType().toString().contains("AXE") || item.getType().toString().contains("SPADE")) {
                item.addEnchantment(Enchantment.DIG_SPEED, 3);
            }
        }
    }
}
