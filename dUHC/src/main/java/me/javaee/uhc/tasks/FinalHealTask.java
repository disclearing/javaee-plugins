package me.javaee.uhc.tasks;

import me.javaee.uhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class FinalHealTask extends BukkitRunnable {
    private int finalheal = UHC.getInstance().getConfigurator().getIntegerOption("HEALTIME").getValue() * 60;
    private int seconds = 0;

    @Override
    public void run() {
        if (seconds == finalheal) {
            for (UUID player : UHC.getInstance().getGameManager().getAlivePlayers()) {
                if (Bukkit.getPlayer(player) != null) {
                    Bukkit.getPlayer(player).setHealth(Bukkit.getPlayer(player).getMaxHealth());
                }
            }

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&c&lYou have got your last heal"));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));

            cancel();
        } else if (seconds == finalheal - (10 * 60)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will be given the final heal in &f10 minutes&6."));
        } else if (seconds == finalheal - (5 * 60)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will be given the final heal in &f5 minutes&6."));
        } else if (seconds == finalheal - (4 * 60)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will be given the final heal in &f4 minutes&6."));
        } else if (seconds == finalheal - (3 * 60)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will be given the final heal in &f3 minutes&6."));
        } else if (seconds == finalheal - (2 * 60)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will be given the final heal in &f2 minutes&6."));
        } else if (seconds == finalheal - 60) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will be given the final heal in &f1 minute&6."));
        }

        seconds++;
    }
}
