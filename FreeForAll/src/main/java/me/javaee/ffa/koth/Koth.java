package me.javaee.ffa.koth;

import lombok.Getter;
import lombok.Setter;
import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

@Getter @Setter
public class Koth implements Listener {
    private Information information = FFA.getPlugin().getInformationManager().getInformation();
    private int seconds = 150;
    private int capSeconds = 150;
    private Player controller;

    public Koth() {
        new BukkitRunnable() {
            @Override public void run() {
                if (!information.isKothStarted()) return;

                if (getCapSeconds() <= 0) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[KingOfTheHill] &eThe &9Koth &ehas been captured by &c" + controller.getName() + "&e."));

                    setController(null);
                    setCapSeconds(seconds);
                    information.setKothStarted(false);
                    cancel();
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isFlying() || player.getGameMode() != GameMode.SURVIVAL) return;

                    if (isInsideArea(player.getLocation())) {
                        if (getController() == null) {
                            setController(player);
                        }

                        if (getCapSeconds() % 60 == 0 && getCapSeconds() != 150) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[KingOfTheHill] &eSomeone is trying to control the &9Koth&e. &7(" + BukkitUtils.niceTime(getCapSeconds(), false) + ")"));
                        }
                    } else {
                        if (getController() == player) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[KingOfTheHill] &eSomeone has been knocked off the &9Koth&e. &7(" + BukkitUtils.niceTime(getCapSeconds(), false) + ")"));
                            setController(null);
                            setCapSeconds(getSeconds());
                        }
                    }
                }

                if (getController() != null) {
                    setCapSeconds(getCapSeconds() - 1);
                }
            }
        }.runTaskTimer(FFA.getPlugin(), 20L, 20L);
    }

    public boolean isInsideArea(Location location) {
        return FFA.getPlugin().getInformationManager().getInformation().getKothCuboid().contains(location);
    }
}
