package me.redis.practice.match.cache;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.profile.Profile;
import me.redis.practice.utils.ItemBuilder;
import me.redis.practice.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class MatchCache {
    public static int playingAmount, queueingAmount, spectatingAmount = 0;
    public static Map<UUID, Inventory> inventories = new HashMap<>();

    public MatchCache() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int play = 0;
                int queue = 0;
                int spectate = 0;

                if (!Practice.getPlugin().getProfileManager().getProfiles().values().isEmpty()) {
                    for (Profile profile : Practice.getPlugin().getProfileManager().getProfiles().values()) {
                        if (profile.getStatus() == ProfileStatus.MATCH) {
                            play++;
                        }
                        else if (profile.getStatus() == ProfileStatus.QUEUE) {
                            queue++;
                        }
                        else if (profile.getStatus() == ProfileStatus.SPECTATOR) {
                            spectate++;
                        }
                    }
                }

                playingAmount = play;
                queueingAmount = queue;
                spectatingAmount = spectate;
            }
        }.runTaskTimerAsynchronously(Practice.getPlugin(), 0L, 20L);
    }

    public static void storeInventory(Player player, boolean dead) {
        Inventory inventory = Bukkit.createInventory(null, 45, player.getName() + " ｜ Information");

        inventory.setContents(player.getInventory().getContents());
        inventory.setItem(36, player.getInventory().getHelmet());
        inventory.setItem(37, player.getInventory().getChestplate());
        inventory.setItem(38, player.getInventory().getLeggings());
        inventory.setItem(39, player.getInventory().getBoots());

        if (dead) {
            inventory.setItem(44, new ItemBuilder(Material.SKULL_ITEM).setDisplayName("&c" + player.getName() + " died").create());
        } else {
            inventory.setItem(44, new ItemBuilder(Material.SPECKLED_MELON).setDisplayName("&c" + player.getName() + "'s health&7: &f" + ((int) player.getHealth())).create());
        }

        inventories.put(player.getUniqueId(), inventory);
    }

    public static void storeInventory(Player player, boolean dead, int missed) {
        Inventory inventory = Bukkit.createInventory(null, 45, player.getName() + " ｜ Information");

        inventory.setContents(player.getInventory().getContents());
        inventory.setItem(36, player.getInventory().getHelmet());
        inventory.setItem(37, player.getInventory().getChestplate());
        inventory.setItem(38, player.getInventory().getLeggings());
        inventory.setItem(39, player.getInventory().getBoots());

        if (dead) {
            inventory.setItem(44, new ItemBuilder(Material.SKULL_ITEM).setDisplayName("&c" + player.getName() + " died").create());
        } else {
            inventory.setItem(44, new ItemBuilder(Material.SPECKLED_MELON).setDisplayName("&c" + player.getName() + "'s health&7: &f" + ((int) player.getHealth())).create());
        }

        inventory.setItem(43, new ItemBuilder(Material.POTION).setDisplayName("&eMissed potions&7: &f" + missed).create());

        inventories.put(player.getUniqueId(), inventory);
    }
}