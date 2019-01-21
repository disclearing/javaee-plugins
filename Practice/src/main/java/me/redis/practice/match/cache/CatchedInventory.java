package me.redis.practice.match.cache;

import lombok.Getter;
import me.redis.practice.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class CatchedInventory {
    @Getter private UUID identifier;
    @Getter private String name;
    @Getter private double health;
    @Getter private double food;
    private PlayerInventory inventory;

    public CatchedInventory(String name, double health, double food, List<String> effects, PlayerInventory inventory) {
        this.identifier = UUID.randomUUID();
        this.name = name;
        this.health = health;
        this.food = food;
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(null, 54, "Inventory of " + this.name);

        for (int i = 9; i <= 35; ++i) {
            inv.setItem(i - 9, this.inventory.getContents()[i]);
        }

        for (int i = 0; i <= 8; ++i) {
            inv.setItem(i + 27, this.inventory.getContents()[i]);
        }

        inv.setItem(36, this.inventory.getHelmet());
        inv.setItem(37, this.inventory.getChestplate());
        inv.setItem(38, this.inventory.getLeggings());
        inv.setItem(39, this.inventory.getBoots());

        return inv;
    }

}
