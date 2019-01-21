package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.UHC;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.events.ScenarioDisableEvent;
import me.javaee.uhc.events.ScenarioEnableEvent;
import me.javaee.uhc.handlers.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class NetherScenarioListener implements Listener {

    @EventHandler
    public void onTeleport(PlayerPortalEvent event) {
        if (!Scenario.getByName("Nether").isEnabled()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Nether " + ChatColor.WHITE + "is disabled.");
        }
    }

    @EventHandler
    public void onScenarioEnable(ScenarioEnableEvent event) {
        if (event.getScenario().getName().equalsIgnoreCase("Nether")) {
            UHC.getInstance().getConfigurator().getBooleanOption("BED").setValue(true);
        }

        if (event.getScenario().getName().equalsIgnoreCase("Weekend Event")) {
            Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
                for (Profile profile : ProfileUtils.getInstance().getAllProfiles()) {
                    if (profile.getWinnedGames() > 0) {
                        UHC.getInstance().getChampions().add(profile.getUuid());
                        try {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&e'&7" + profile.getLastName() + "&e' has been whitelisted with " + profile.getWinnedGames() + " win/s."));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (UHC.getInstance().getChampions().contains(profile.getUuid())) continue;

                    if (profile.getKills() > 50) {
                        UHC.getInstance().getChampions().add(profile.getUuid());
                        try {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&e'&7" + profile.getLastName() + "&e' has been whitelisted with " + profile.getKills() + " kill/s."));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @EventHandler
    public void onScenarioEnable(ScenarioDisableEvent event) {
        if (event.getScenario().getName().equalsIgnoreCase("Nether")) {
            UHC.getInstance().getConfigurator().getBooleanOption("BED").setValue(false);
        }
    }
}
