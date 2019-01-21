package me.javaee.uhc.tasks;

import me.javaee.uhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CavesTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                if (player.hasPermission("vip.bypass.caves")) return;

                if (player.getLocation().getBlockY() <= 40) {
                    player.teleport(new Location(player.getWorld(), player.getLocation().getX(), player.getWorld().getHighestBlockYAt(player.getLocation()), player.getLocation().getY()));
                    player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes("&eYou can't spectate caves. If you want, you can buy a rank at &bstore.silexpvp.net &eto bypass this."));
                }
            }
        }
    }
}
