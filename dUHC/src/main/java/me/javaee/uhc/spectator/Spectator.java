package me.javaee.uhc.spectator;

import lombok.Getter;
import lombok.Setter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
@Getter
public class Spectator {
    @Setter private Boolean mutedChat = false;
    private ArrayList<Player> spectators = new ArrayList<>();

    public void setSpectator(Player player) {
        if (spectators.contains(player)) {
            spectators.remove(player);

            for (UUID alive : UHC.getInstance().getGameManager().getAlivePlayers()) {
                if (UHC.getInstance().getGameManager().getAlivePlayers().size() != 0) {
                    if (Bukkit.getPlayer(alive) != null) {
                        Bukkit.getPlayer(alive).showPlayer(player);
                    }
                }
            }

            player.setAllowFlight(false);
        } else {
            spectators.add(player);

            for (UUID alive : UHC.getInstance().getGameManager().getAlivePlayers()) {
                if (UHC.getInstance().getGameManager().getAlivePlayers().size() != 0) {
                    if (Bukkit.getPlayer(alive) != null) {
                        Bukkit.getPlayer(alive).hidePlayer(player);
                    }
                }
            }

            for (Player players : getSpectators()) {
                player.showPlayer(players);
                players.showPlayer(player);
            }

            UHC.getInstance().getGameManager().getModerators().forEach(mods -> player.hidePlayer(mods));

            if (UHC.getInstance().getGameManager().getHost() != null) {
                player.hidePlayer(UHC.getInstance().getGameManager().getHost());
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes("&6You are now a spectator."));

            UHC.getInstance().getGameManager().getAlivePlayers().remove(player.getUniqueId());

            player.spigot().setCollidesWithEntities(false);
            player.setHealth(20D);
            player.setFoodLevel(20);
            player.setSaturation(20F);
            player.setAllowFlight(true);
            player.getInventory().clear();
            player.setCanPickupItems(false);

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getInventory().setItem(3, new ItemBuilder(Material.NAME_TAG).setName(ChatColor.GOLD + "Random teleporter").build());
                    player.getInventory().setItem(4, new ItemBuilder(Material.INK_SACK).setDurability(8).setName(ChatColor.BLUE + "Show Spectators").build());
                    player.getInventory().setItem(5, new ItemBuilder(Material.COMPASS).setName(ChatColor.GOLD + "Go to center").build());

                    player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setColor(Color.RED).build());
                    player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setColor(Color.RED).build());

                    if (player.getLocation().getBlockY() < 40) {
                        player.teleport(player.getLocation().getWorld().getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation());
                    }
                }
            }.runTaskLater(UHC.getInstance(), 20L);
        }
    }
}
