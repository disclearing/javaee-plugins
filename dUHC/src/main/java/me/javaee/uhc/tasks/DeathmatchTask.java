package me.javaee.uhc.tasks;

import me.javaee.uhc.UHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class DeathmatchTask extends BukkitRunnable {
    UHC uhc = UHC.getInstance();
    int counter = 0;
    public static int sbCounter = 300;
    public static boolean isNow = false;

    @Override
    public void run() {
        if (counter == 1) {
            UHC.getInstance().setDeathmatch(true);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &65 minutes&f."));
        }
        if (counter == 60) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &64 minutes&f."));
        }
        if (counter == 120) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &63 minutes&f."));
        }
        if (counter == 180) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &62 minutes&f."));
        }
        if (counter == 240) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &61 minutes&f."));
        }
        if (counter == 290) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &610 seconds&f."));
        }
        if (counter == 295) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &65 seconds&f."));
        }
        if (counter == 296) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &64 seconds&f."));
        }
        if (counter == 297) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &63 seconds&f."));
        }
        if (counter == 298) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &62 seconds&f."));
        }
        if (counter == 299) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fwill start in &61 seconds&f."));
        }
        if (counter >= 300) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe &6Deathmatch &fhas started."));

            UHC.getInstance().getGameManager().getAlivePlayers().forEach(alive -> {
                Player player = Bukkit.getPlayer(alive);

                if (player != null) {
                    int x = new Random().nextInt(18);
                    int z = new Random().nextInt(18);

                    player.teleport(new Location(Bukkit.getWorld("Deathmatch"), x, Bukkit.getWorld("Deathmatch").getHighestBlockYAt(x, z), z));
                }
            });

            Bukkit.getWorld("Deathmatch").setGameRuleValue("naturalRegeneration", "false");

            UHC.getInstance().getSpectatorManager().getSpectators().forEach(spectators -> spectators.teleport(new Location(Bukkit.getWorld("Deathmatch"), 0, 95, 0)));

            UHC.getInstance().getGameManager().getModerators().forEach(spectators -> spectators.teleport(new Location(Bukkit.getWorld("Deathmatch"), 0, 95, 0)));

            if (UHC.getInstance().getGameManager().getHost() != null) UHC.getInstance().getGameManager().getHost().teleport(new Location(Bukkit.getWorld("Deathmatch"), 0, 95, 0));
            UHC.getInstance().getBorderManager().setBorder(21);
            isNow = true;
            cancel();
        }

        counter++;
        sbCounter--;
    }
}
