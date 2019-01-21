package me.redis.kohi.chest;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ChestLoot {
    private Material material;
    private double chance;
    private int min;
    private int max;
    private short damage;
    private Map<Enchantment, Integer> enchantments;
    private String name;
    private List<String> lore;

    public ChestLoot(Material material, double chance, int min, int max, short damage) {
        this.material = material;
        this.chance = chance;
        this.min = min;
        this.max = max;
        this.damage = damage;
    }

    public void applyEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public void applyName(String name) {
        this.name = name;
    }

    public void applyLore(List<String> lore) {
        this.lore = lore;
    }

    public boolean hasChance(Random random) {
        double result = random.nextDouble() * 100.0D;
        return this.chance > result;
    }

    public ItemStack getRandomItemStack(Random random) {
        int amount = -1;
        if (this.min == this.max) {
            amount = this.min;
        } else {
            int diff = this.max - this.min;
            amount = this.min + random.nextInt(diff);
        }
        ItemStack stack = new ItemStack(this.material, amount);
        stack.setDurability(this.damage);
        if ((this.enchantments != null) && (stack.getType() != Material.ENCHANTED_BOOK)) {
            for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                stack.addUnsafeEnchantment( entry.getKey(), entry.getValue());
            }
        }
        ItemMeta meta = stack.getItemMeta();
        if ((this.lore != null) || (this.name != null)) {
            if (this.lore != null) {
                meta.setLore(this.lore);
            }
            if (this.name != null) {
                meta.setDisplayName(this.name);
            }
            stack.setItemMeta(meta);
        }
        if ((meta instanceof EnchantmentStorageMeta)) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
            for (Map.Entry<Enchantment, Integer> entry2 : this.enchantments.entrySet()) {
                enchantmentStorageMeta.addStoredEnchant(entry2.getKey(), entry2.getValue(), true);
            }
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public String toString() {
        return "ChestItemStack(material=" + this.material + ", chance=" + this.chance + ", min=" + this.min + ", max=" + this.max + ", damage=" + this.damage + ", enchantments=" + this.enchantments + ", name=" + this.name + ", lore=" + this.lore + ")";
    }
}