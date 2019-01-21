package me.javaee.ffa.combatlogger;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CombatLogger {
    @Getter private final LivingEntity entity;
    @Getter private final UUID uniqueId;
    @Getter private final String name;
    @Getter private final ItemStack[] contents, armor;

    public CombatLogger(Player player) {
        uniqueId = player.getUniqueId();
        name = player.getName();

        contents = player.getInventory().getContents();
        armor = player.getInventory().getArmorContents();

        entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        entity.setMaxHealth(1);
        entity.setHealth(1);
        entity.setFallDistance(player.getFallDistance());
        entity.setLastDamageCause(player.getLastDamageCause());

        Villager villager = (Villager) entity;
        villager.setProfession(Villager.Profession.FARMER);
        villager.setAdult();

        for (PotionEffect effect : player.getActivePotionEffects()) {
            entity.addPotionEffect(effect);
        }

        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100), true);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100), true);

        entity.setRemoveWhenFarAway(false);
        entity.setCustomName(ChatColor.RED + player.getName());
        entity.setCustomNameVisible(true);
    }
}