package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class TimberListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (Scenario.getByName("Timber").isEnabled()) {
            Block block = event.getBlock();

            if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
                event.getPlayer().getInventory().addItem(new ItemStack(block.getType()));
                loopThroughTimber(block, event.getPlayer());
            }
        }
    }

    private void loopThroughTimber(Block consBlock, Player player) {
        for (BlockFace blockface : BlockFace.values()) {
            if (consBlock.getRelative(blockface).getType().equals(Material.LOG) || consBlock.getRelative(blockface).getType().equals(Material.LOG_2)) {
                Block block = consBlock.getRelative(blockface);

                player.getInventory().addItem(new ItemStack(block.getType()));
                block.setType(Material.AIR);
                loopThroughTimber(block, player);
            }
        }
    }
}
