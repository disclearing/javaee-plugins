package me.javaee.uhc.visualise;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public enum VisualType {

    BORDER() {
        private final BlockFiller blockFiller = new BlockFiller() {
            @Override
            VisualBlockData generate(Player player, Location location) {
                return new VisualBlockData(Material.STAINED_GLASS, DyeColor.YELLOW.getData());
            }
        };

        @Override
        BlockFiller blockFiller() {
            return blockFiller;
        }
    };

    abstract BlockFiller blockFiller();
}