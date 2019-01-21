package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class BowLessListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (Scenario.getByName("Bowless").isEnabled()) {
            if (event.getPlayer().getItemInHand().getType() == Material.BOW) {
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes("&fBowless &6is enabled in this game."));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCraftBow(CraftItemEvent event) {
        if (Scenario.getByName("Bowless").isEnabled()) {
            if (event.getCurrentItem().getType() == Material.BOW) {
                ((Player) event.getWhoClicked()).sendMessage(ChatColor.translateAlternateColorCodes("&fBowless &6is enabled in this game."));
                event.setCancelled(true);
            }
        }
    }
}
