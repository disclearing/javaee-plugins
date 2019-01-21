package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class DiamondLessListener implements Listener {

    @EventHandler
    public void onMine(BlockBreakEvent event) {
        if (Scenario.getByName("Diamondless").isEnabled() && event.getBlock().getType() == Material.DIAMOND_ORE) {
            Block block = event.getBlock();

            event.getBlock().getDrops().clear();
            block.getLocation().getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (Scenario.getByName("Diamondless").isEnabled()) {
            Player death = event.getEntity().getPlayer();

            if (!Scenario.getByName("TimeBomb").isEnabled()) {
                death.getWorld().dropItemNaturally(death.getLocation(), new ItemStack(Material.DIAMOND));
            }
        }
    }
}
