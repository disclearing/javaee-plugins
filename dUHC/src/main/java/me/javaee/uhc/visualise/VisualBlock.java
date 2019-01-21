package me.javaee.uhc.visualise;

import lombok.Getter;
import org.bukkit.Location;

@Getter public class VisualBlock {

    private final VisualType visualType;

    private final VisualBlockData blockData;
    private final Location location;

    public VisualBlock(VisualType visualType, VisualBlockData blockData, Location location) {
        this.visualType = visualType;
        this.blockData = blockData;
        this.location = location;
    }
}