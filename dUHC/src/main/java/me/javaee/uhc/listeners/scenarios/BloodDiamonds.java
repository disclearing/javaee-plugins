package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.handlers.Scenario;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class BloodDiamonds implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMineDiamond(BlockBreakEvent event) {
        if (Scenario.getByName("Blood Diamonds").isEnabled()) {
            if (event.getBlock().getType() == Material.DIAMOND_ORE) {
                event.getPlayer().damage(1);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Your health has been affected by half a heart.");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (Scenario.getByName("Hypixel Heads").isEnabled()) {
            if (item != null) {
                if (item.getType() == Material.SKULL_ITEM) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 4, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1));

                    player.sendMessage(ChatColor.translateAlternateColorCodes("&aYou ate a player head and gained 4 seconds of Regeneration III and 20 seconds of Speed II!"));
                    event.setCancelled(true);

                    if (player.getItemInHand().getAmount() > 1) {
                        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                    } else {
                        player.getInventory().remove(item);
                    }

                    player.updateInventory();
                }
            }
        }
    }
}
