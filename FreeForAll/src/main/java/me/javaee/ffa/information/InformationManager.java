package me.javaee.ffa.information;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import me.javaee.ffa.FFA;
import me.javaee.ffa.profiles.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class InformationManager {
    @Getter @Setter public Information information;
    @Getter private int nig = 1;

    public InformationManager() {
        setInformation(new Information());

        Bukkit.getScheduler().runTaskTimerAsynchronously(FFA.getPlugin(), () -> {
            Bukkit.getWorld("MAPFFA").setTime(1000);
        }, 20 * 5L, 20 * 5L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(FFA.getPlugin(), () -> {
            if (nig == 1) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSeason Dominators &7&o(kills)"));
                Bukkit.broadcastMessage("");

                int counter = 0;
                for (Map.Entry<UUID, Object> kills : FFA.getPlugin().getProfileManager().getSortedValues("kills", Sorts.descending("kills"), Filters.gte("kills", 1), 5).entrySet()) {
                    Profile profile = new Profile(kills.getKey());

                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&l" + (counter + 1) + ". &e" + profile.getName() + "&7: &f" + profile.getKills()));
                    counter++;
                }

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------"));
                nig = 0;
            } else if (nig == 0) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSeason Dominators &7&o(elo)"));
                Bukkit.broadcastMessage("");

                int counter = 0;
                for (Map.Entry<UUID, Object> kills : FFA.getPlugin().getProfileManager().getSortedValues("elo", Sorts.descending("elo"), Filters.gte("elo", 1), 5).entrySet()) {
                    Profile profile = new Profile(kills.getKey());

                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&l" + (counter + 1) + ". &e" + profile.getName() + "&7: &f" + profile.getElo()));
                    counter++;
                }

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------"));
                nig = 1;
            }
        }, 0L, 27 * 64 * 2L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(FFA.getPlugin(), () -> {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', getRandomAnnouncement()));
        }, 0L, 25 * 2 * 60L);
    }

    public String getRandomAnnouncement() {
        return (String) getInformation().getAnnounces().toArray()[new Random().nextInt(getInformation().getAnnounces().size())];
    }
}
