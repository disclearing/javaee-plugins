package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.ChatColor;
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
public class GoldLessListener implements Listener {

    @EventHandler
    public void onMine(BlockBreakEvent event) {
        if (Scenario.getByName("Goldless").isEnabled() && event.getBlock().getType() == Material.GOLD_ORE) {
            Block block = event.getBlock();

            event.getBlock().getDrops().clear();
            block.getLocation().getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (Scenario.getByName("Goldless").isEnabled()) {
            Player death = event.getEntity().getPlayer();

            if (!Scenario.getByName("TimeBomb").isEnabled()) {
                death.getWorld().dropItemNaturally(death.getLocation(), new ItemStack(Material.GOLD_INGOT, 12));
                death.getWorld().dropItemNaturally(death.getLocation(), new ItemBuilder(Material.GOLDEN_APPLE).setName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Golden Head").build());
            }
        }
    }
}
