package me.javaee.uhc.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class BroadcastCommandsTask extends BukkitRunnable {
    private int counter = 0;

    @Override
    public void run() {
        if (counter == 0) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(""));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&6&lRemember&7: &eIf there are hackers report them with '&7/report <player> <reason>&e'."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(""));

            counter = 1;
            return;
        }

        if (counter == 1) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(""));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&6&lRemember&7: &eIf there are trucers report them with '&7/truce <player> <player>&e'."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(""));

            counter = 2;
            return;
        }

        if (counter == 2) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(""));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&6&lRemember&7: &eYou can't truce or use hacked clients on the uhc."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(""));

            counter = 3;
            return;
        }

        if (counter == 3) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(""));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&6&lRemember&7: &eYou can mine however you want!"));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(""));

            counter = 0;
        }
    }
}
