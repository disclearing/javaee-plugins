package me.javaee.ffa.wand;

import lombok.Getter;
import lombok.Setter;
import me.javaee.ffa.FFA;
import me.javaee.ffa.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Getter @Setter
public class Wand {
    private Location firstLocation;
    private Location secondLocation;

    public Wand(Player player) {
        if (FFA.getPlugin().getWandManager().getWand(player) != null) {
            player.getInventory().addItem(new ItemBuilder(Material.GOLD_HOE).setDisplayName("&6Area wand").create());
        } else {
            player.getInventory().addItem(new ItemBuilder(Material.GOLD_HOE).setDisplayName("&6Area wand").create());
            FFA.getPlugin().getWandManager().getWands().put(player, this);
        }
    }
}
