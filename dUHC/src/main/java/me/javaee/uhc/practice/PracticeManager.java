package me.javaee.uhc.practice;

import lombok.Getter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.practice.kits.Kit;
import me.javaee.uhc.practice.listeners.PracticeListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
@Getter
public class PracticeManager {
    public Kit kit = new Kit();
    public List<Player> practicePlayers = new ArrayList<>();
    public Map<Player, Integer> killStreak = new HashMap<>();

    public void init() {
        Bukkit.getPluginManager().registerEvents(new PracticeListener(), UHC.getInstance());
    }

    public void removeFromPractice(Player player) {
        getPracticePlayers().remove(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.teleport(Bukkit.getWorld("lobby").getSpawnLocation());
        player.setHealth(20);
        player.setFoodLevel(20);

        for (Player players : practicePlayers) {
            players.hidePlayer(player, false);
            player.hidePlayer(players, false);
        }
    }

    public void addToPractice(Player player) {
        getPracticePlayers().add(player);

        getKit().giveItems(player);

        player.teleport(getScatterLocation());

        for (Player players : practicePlayers) {
            players.showPlayer(player);
            player.showPlayer(players);
        }
    }

    public Location getScatterLocation() {
        Random localRandom = new Random();

        int i = localRandom.nextInt(45 * 2) - 45;
        int j = localRandom.nextInt(45 * 2) - 45;

        return new Location(Bukkit.getWorld("world_the_end"), i, Bukkit.getWorld("world_the_end").getHighestBlockYAt(i, j) + 30.0D, j);
    }
}
