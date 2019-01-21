package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilRepairEvent;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ColdWeaponsListener implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (Scenario.getByName("ColdWeapons").isEnabled()) {
            if (event.getEnchantsToAdd().get(Enchantment.FIRE_ASPECT) != null) {
                event.setCancelled(true);
            } else if (event.getEnchantsToAdd().get(Enchantment.ARROW_FIRE) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAnvil(PrepareAnvilRepairEvent event) {
        if (Scenario.getByName("ColdWeapons").isEnabled()) {
            if (event.getResult().getEnchantments().containsKey(Enchantment.FIRE_ASPECT) || event.getResult().getEnchantments().containsKey(Enchantment.ARROW_FIRE)) {
                event.setCancelled(true);
            }
        }
    }
}
