package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.UHC;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.utils.BukkitUtils;
import net.minecraft.server.v1_7_R4.EntityFishingHook;
import net.minecraft.server.v1_7_R4.ItemFishingRod;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ClimberScenario implements Listener {
    private ArrayList<Player> cooldown = new ArrayList<>();

    @EventHandler
    public void onFishShit(ProjectileHitEvent event) {
        if (Scenario.getByName("Climber").isEnabled()) {
            if (event.getEntity() instanceof FishHook && event.getEntity().getShooter() instanceof Player) {
                Location targetLocation = event.getEntity().getLocation();
                Player player = (Player) event.getEntity().getShooter();

                if (cooldown.contains(player)) return;

                if (targetLocation == null) return;


                //Teleport player a bit in the air before applying the vector. (Bukkit shit)
                player.teleport(player.getLocation().add(0, 0.5, 0));

                //Get the vector that the player needs to get there
                Vector vector = BukkitUtils.getVectorForPoints(player.getLocation(), targetLocation);

                player.setVelocity(vector);

                //It gives a cooldown of 1 second to the player.
                cooldown.add(player);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(UHC.getInstance(), () -> cooldown.remove(player), 20 * 3);
            }
        }
    }
}
