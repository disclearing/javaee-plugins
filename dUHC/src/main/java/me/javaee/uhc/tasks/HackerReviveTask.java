package me.javaee.uhc.tasks;

import me.javaee.uhc.UHC;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.listeners.misc.EndListener;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.InventorySerialization;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.UUID;

public class HackerReviveTask extends BukkitRunnable {
    @Override
    public void run() {
        UHC.getInstance().getGameManager().getKilledRevived().forEach(name -> {
            Player player = Bukkit.getPlayer(name);

            if (player != null) {
                if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                    UHC.getInstance().getSpectatorManager().getSpectators().remove(player);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setAllowFlight(false);
                    player.setCanPickupItems(true);
                    player.spigot().setCollidesWithEntities(true);

                    Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

                    player.sendMessage(ChatColor.RED + "Giving your items in 2 seconds.");
                    new BukkitRunnable() {
                        public void run() {
                            try {
                                player.getInventory().setContents(InventorySerialization.itemStackArrayFromBase64(profile.getDeathInventory()));
                            } catch (IOException e) {
                                player.sendMessage(ChatColor.RED + "Error while trying to give the player it's inventory. (" + profile.getDeathInventory() + ")");
                            }

                            try {
                                player.getInventory().setArmorContents(InventorySerialization.itemStackArrayFromBase64(profile.getDeathArmor()));
                            } catch (IOException e) {
                                player.sendMessage(ChatColor.RED + "Error while trying to give the player it's armor. (" + profile.getDeathArmor() + ")");
                            }

                            if (!profile.getDeathLocation().equalsIgnoreCase("null")) {
                                String[] location = profile.getDeathLocation().split(";");

                                double x = Double.parseDouble(location[0]);
                                double z = Double.parseDouble(location[1]);

                                player.teleport(new Location(Bukkit.getWorld("world"), x, Bukkit.getWorld("world").getHighestBlockYAt((int) x, (int) z), z));
                            }
                        }
                    }.runTaskLater(UHC.getInstance(), 20L * 2);

                    UHC.getInstance().getGameManager().getAlivePlayers().add(player.getUniqueId());

                    player.sendMessage(ChatColor.RED + "You have been revived.");

                    for (Player spectators : UHC.getInstance().getSpectatorManager().getSpectators()) {
                        player.hidePlayer(spectators);
                        spectators.showPlayer(player);
                    }

                    for (Player staff : UHC.getInstance().getStaffModeManager().getStaffModeList()) {
                        staff.showPlayer(player);
                        player.hidePlayer(staff);
                    }

                    for (UUID alive : UHC.getInstance().getGameManager().getAlivePlayers()) {
                        if (Bukkit.getPlayer(alive) != null) {
                            Bukkit.getPlayer(alive).showPlayer(player);
                            player.showPlayer(Bukkit.getPlayer(alive));
                        }
                    }

                    ProfileUtils.getInstance().getProfile(player.getUniqueId()).setDead(false);
                    ProfileUtils.getInstance().getProfile(player.getUniqueId()).setDeaths(ProfileUtils.getInstance().getProfile(player.getUniqueId()).getDeaths() - 1);
                    ProfileUtils.getInstance().getProfile(player.getUniqueId()).save(true);
                    UHC.getInstance().getGameManager().getKilledRevived().remove(player.getName());

                    if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 2) {
                        if (UHCTeam.getByUUID(player.getUniqueId()) != null) {
                            UHCTeam.getByUUID(player.getUniqueId()).setDtr(UHCTeam.getByUUID(player.getUniqueId()).getDtr() + 1);
                        } else {
                            UHCTeam team = new UHCTeam(player.getUniqueId());
                            UHC.getInstance().getTeams().add(team);
                            UHCTeam.getByUUID(player.getUniqueId()).setDtr(UHCTeam.getByUUID(player.getUniqueId()).getDtr() + 1);
                        }
                    }
                }
            }
        });
    }
}
