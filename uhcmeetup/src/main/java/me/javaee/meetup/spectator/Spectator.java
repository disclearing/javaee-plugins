package me.javaee.meetup.spectator;

import lombok.Getter;
import me.javaee.meetup.Meetup;
import me.javaee.meetup.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
@Getter public class Spectator {
    private ArrayList<Player> spectators = new ArrayList<>();

    public void setSpectator(Player player) {
        if (spectators.contains(player)) {
            spectators.remove(player);

            player.spigot().setCollidesWithEntities(true);
            player.setHealth(20D);
            player.setFoodLevel(20);
            player.setSaturation(20F);
            player.setAllowFlight(false);

            for (Player alive : Meetup.getPlugin().getGameManager().getAlivePlayers()) {
                if (Meetup.getPlugin().getGameManager().getAlivePlayers().size() != 0) {
                    alive.showPlayer(player);
                }
            }
        } else {
            spectators.add(player);

            for (Player alive : Meetup.getPlugin().getGameManager().getAlivePlayers()) {
                if (Meetup.getPlugin().getGameManager().getAlivePlayers().size() != 0) {
                    alive.hidePlayer(player);
                }
            }

            for (Player players : getSpectators()) {
                player.showPlayer(players);
                players.showPlayer(player);
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6You are now a spectator."));

            Meetup.getPlugin().getGameManager().getAlivePlayers().remove(player);

            player.spigot().setCollidesWithEntities(false);
            player.setHealth(20D);
            player.setFoodLevel(20);
            player.setSaturation(20F);
            player.setAllowFlight(true);
            player.getInventory().clear();

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getInventory().setItem(3, new ItemBuilder(Material.NAME_TAG).setName(ChatColor.GOLD + "Random teleporter").build());
                    player.getInventory().setItem(4, new ItemBuilder(Material.ENCHANTED_BOOK).setName(ChatColor.GOLD +  "Server Selector" + ChatColor.GRAY + " (Work In Progress)").build());
                    player.getInventory().setItem(5, new ItemBuilder(Material.INK_SACK).setDurability(10).setName(ChatColor.BLUE + "Hide Spectators").build());
                    player.getInventory().setItem(8, new ItemBuilder(Material.NETHER_STAR).setName(ChatColor.GOLD + "Back to lobby").build());

                    player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setColor(Color.RED).build());
                    player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setColor(Color.RED).build());
                }
            }.runTaskLater(Meetup.getPlugin(), 2L);
        }
    }
}
