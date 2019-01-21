package me.javaee.uhc.listeners.scenarios;

import com.google.common.collect.ImmutableMap;
import me.javaee.uhc.handlers.Scenario;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilRepairEvent;
import org.bukkit.event.inventory.PrepareItemRepairAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class LimitedEnchantsListener implements Listener {
    static Map<Enchantment, Integer> limits = new HashMap<>();
    private final ImmutableMap<Material, EnumToolMaterial> ITEM_TOOL_MAPPING = (ImmutableMap.of(Material.IRON_INGOT, EnumToolMaterial.IRON, Material.GOLD_INGOT, EnumToolMaterial.GOLD, Material.DIAMOND, EnumToolMaterial.DIAMOND));
    private final ImmutableMap<Material, EnumArmorMaterial> ITEM_ARMOUR_MAPPING = (ImmutableMap.of(Material.IRON_INGOT, EnumArmorMaterial.IRON, Material.GOLD_INGOT, EnumArmorMaterial.GOLD, Material.DIAMOND, EnumArmorMaterial.DIAMOND));

    static {
        limits.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        limits.put(Enchantment.PROTECTION_EXPLOSIONS, 0);
        limits.put(Enchantment.PROTECTION_FIRE, 0);
        limits.put(Enchantment.PROTECTION_PROJECTILE, 0);
        limits.put(Enchantment.PROTECTION_FALL, 0);
        limits.put(Enchantment.DAMAGE_ARTHROPODS, 0);
        limits.put(Enchantment.DAMAGE_UNDEAD, 0);
        limits.put(Enchantment.FIRE_ASPECT, 0);
        limits.put(Enchantment.ARROW_FIRE, 0);
        limits.put(Enchantment.ARROW_KNOCKBACK, 0);
        limits.put(Enchantment.KNOCKBACK, 0);
        limits.put(Enchantment.DAMAGE_ALL, 1);
        limits.put(Enchantment.DURABILITY, 0);
        limits.put(Enchantment.OXYGEN, 0);
        limits.put(Enchantment.ARROW_DAMAGE, 1);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEnchantItem(EnchantItemEvent event) {
        if (Scenario.getByName("Limited Enchants").isEnabled()) {
            Map<Enchantment, Integer> adding = event.getEnchantsToAdd();
            Iterator<Map.Entry<Enchantment, Integer>> iterator = adding.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Enchantment, Integer> entry = iterator.next();
                Enchantment enchantment = entry.getKey();
                int maxLevel = getMaxLevel(enchantment);
                if (entry.getValue() > maxLevel) {
                    if (maxLevel > 0) {
                        adding.put(enchantment, maxLevel);
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPrepareAnvilRepair(PrepareAnvilRepairEvent event) {
        if (Scenario.getByName("Limited Enchants").isEnabled()) {
            ItemStack first = event.getFirst();
            ItemStack second = event.getSecond();

            if (first != null && first.getType() != Material.AIR && second != null && second.getType() != Material.AIR) {
                Object firstItemObj = net.minecraft.server.v1_7_R4.Item.REGISTRY.a(first.getTypeId());
                if (firstItemObj instanceof net.minecraft.server.v1_7_R4.Item) {
                    net.minecraft.server.v1_7_R4.Item nmsFirstItem = (net.minecraft.server.v1_7_R4.Item) firstItemObj;
                    if (nmsFirstItem instanceof ItemTool) {
                        if (ITEM_TOOL_MAPPING.get(second.getType()) == ((ItemTool) nmsFirstItem).i()) {
                            return;
                        }
                    } else if (nmsFirstItem instanceof ItemSword) {
                        EnumToolMaterial comparison = ITEM_TOOL_MAPPING.get(second.getType());
                        if (comparison != null && comparison.e() == nmsFirstItem.c()) {
                            return;
                        }
                    } else if (nmsFirstItem instanceof ItemArmor) {
                        if (ITEM_ARMOUR_MAPPING.get(second.getType()) == ((ItemArmor) nmsFirstItem).m_()) {
                            return;
                        }
                    }
                }
            }

            HumanEntity repairer = event.getRepairer();
            if (repairer instanceof Player) {
                validateIllegalEnchants(event.getResult());
            }
        }
    }

    public int getMaxLevel(Enchantment enchant) {
        return limits.getOrDefault(enchant, enchant.getMaxLevel());
    }

    private boolean validateIllegalEnchants(ItemStack stack) {
        boolean updated = false;
        if (stack != null && stack.getType() != Material.AIR) {
            ItemMeta meta = stack.getItemMeta();
            Set<Map.Entry<Enchantment, Integer>> entries;

            // Have to use this for books.
            if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
                entries = enchantmentStorageMeta.getStoredEnchants().entrySet();
                for (Map.Entry<Enchantment, Integer> entry : entries) {
                    Enchantment enchantment = entry.getKey();
                    int maxLevel = getMaxLevel(enchantment);
                    if (entry.getValue() > maxLevel) {
                        updated = true;
                        if (maxLevel > 0) {
                            enchantmentStorageMeta.addStoredEnchant(enchantment, maxLevel, false);
                        } else {
                            enchantmentStorageMeta.removeStoredEnchant(enchantment);
                        }
                    }
                }

                // Re-apply the ItemMeta.
                stack.setItemMeta(meta);
            } else {
                entries = stack.getEnchantments().entrySet();
                for (Map.Entry<Enchantment, Integer> entry : entries) {
                    Enchantment enchantment = entry.getKey();
                    int maxLevel = getMaxLevel(enchantment);
                    if (entry.getValue() > maxLevel) {
                        updated = true;
                        stack.removeEnchantment(enchantment);
                        if (maxLevel > 0) {
                            stack.addEnchantment(enchantment, maxLevel);
                        }
                    }
                }
            }
        }

        return updated;
    }
}
