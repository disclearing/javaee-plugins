package me.javaee.meetup.handlers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Random;

public class SpawnsHandler {
    @Getter @Setter public ArrayList<Location> scatterPoints = new ArrayList<>();

    public void addLocation() {
        Location loc = getScatterLocation();

        loc.getWorld().loadChunk(loc.getChunk());
        scatterPoints.add(loc);
    }

    private Location getScatterLocation() {
        int x = (int) ((int) (Math.random() * 66) + (Math.random() * 13));
        int z = (int) ((int) (Math.random() * 32) + (Math.random() * 90));

        return new Location(Bukkit.getWorld("world"), x, Bukkit.getWorld("world").getHighestBlockYAt(x, z), z);
    }
}
