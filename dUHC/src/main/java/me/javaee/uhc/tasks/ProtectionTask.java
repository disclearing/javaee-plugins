package me.javaee.uhc.tasks;

import me.javaee.uhc.UHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class ProtectionTask extends BukkitRunnable {
    private int pvptime = UHC.getInstance().getConfigurator().getIntegerOption("PVPTIME").getValue() * 60;
    private int seconds = 0;

    @Override
    public void run() {
        if (seconds == pvptime) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&c&lThe PvP has been enabled"));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7&m--------------------------"));

            Bukkit.getOnlinePlayers().forEach(online -> {
                online.playSound(online.getLocation(), Sound.ANVIL_LAND, 1F, 1F);
            });
            
            cancel();
        } else if (seconds == pvptime - (10 * 60)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will lose your pvp protection in &f10 minutes&6."));
        } else if (seconds == pvptime - (5 * 60)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will lose your pvp protection in &f5 minutes&6."));
        } else if (seconds == pvptime - (4 * 60)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will lose your pvp protection in &f4 minutes&6."));
        } else if (seconds == pvptime - (3 * 60)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will lose your pvp protection in &f3 minutes&6."));
        } else if (seconds == pvptime - (2 * 60)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will lose your pvp protection in &f2 minutes&6."));
        } else if (seconds == pvptime - 60) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6You will lose your pvp protection in &f1 minute&6."));
        }

        seconds++;
    }
}
