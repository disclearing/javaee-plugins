package me.javaee.uhc.listeners.stats;

import me.javaee.uhc.UHC;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ConsumeListener implements Listener {

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Boolean statLess = (Boolean) UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.STATLESS.name()).getValue();

        if (!statLess) {
            Player player = event.getPlayer();
            Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());
            ItemStack itemStack = event.getItem();

            if (itemStack.getType() == Material.GOLDEN_APPLE) {
                if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName() != null) {
                    if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD.toString() + ChatColor.BOLD + "Golden Head")) {
                        profile.setGoldenHeads(profile.getGoldenHeads() + 1);

                        event.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
                        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                    }
                } else {
                    profile.setGoldenApples(profile.getGoldenApples() + 1);
                }
            }
        } else {
            ItemStack itemStack = event.getItem();

            if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName() != null) {
                if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD.toString() + ChatColor.BOLD + "Golden Head")) {
                    event.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                }
            }
        }
    }
}
