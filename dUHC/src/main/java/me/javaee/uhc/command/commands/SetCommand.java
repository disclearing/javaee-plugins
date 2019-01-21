package me.javaee.uhc.command.commands;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import de.inventivegames.hologram.Hologram;
import de.inventivegames.hologram.HologramAPI;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.managers.LeaderboardNPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SetCommand extends BaseCommand {
    public SetCommand() {
        super("sbsj", Arrays.asList("asdfbs"), true, false);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Location kills1 = getFocusAngleLocation(new Location(Bukkit.getWorld("lobby"), -4, 100, 5), new Location(Bukkit.getWorld("lobby"), -6.5, 96, 12.5));
        Location kills2 = getFocusAngleLocation(new Location(Bukkit.getWorld("lobby"), -4, 100, 5), new Location(Bukkit.getWorld("lobby"), -3.5, 96, 12.5));
        Location kills3 = getFocusAngleLocation(new Location(Bukkit.getWorld("lobby"), -4, 100, 5), new Location(Bukkit.getWorld("lobby"), -9.5, 96, 11.5));
        Location kills4 = getFocusAngleLocation(new Location(Bukkit.getWorld("lobby"), -4, 100, 5), new Location(Bukkit.getWorld("lobby"), -0.5, 96, 11.5));
        Location kills5 = getFocusAngleLocation(new Location(Bukkit.getWorld("lobby"), -4, 100, 5), new Location(Bukkit.getWorld("lobby"), -12.5, 96, 9.5));

        int counter = 1;
        for (Map.Entry<UUID, Object> kills : ProfileUtils.getInstance().getSortedValues("kills", Sorts.descending("kills"), Filters.gte("kills", 1), 5).entrySet()) {

            if (counter == 1) {
                LeaderboardNPC npc = new LeaderboardNPC(kills.getKey(), kills1);

                Hologram hologram = HologramAPI.createHologram(kills1.add(0, 3, 0), ChatColor.translateAlternateColorCodes("&b&lTop #1"));
                hologram.spawn();

                hologram.addLineBelow(ChatColor.translateAlternateColorCodes("&6Kills&7: &f" + kills.getValue()));
            } else if (counter == 2) {
                LeaderboardNPC npc = new LeaderboardNPC(kills.getKey(), kills2);

                Hologram hologram = HologramAPI.createHologram(kills2.add(0, 3, 0), ChatColor.translateAlternateColorCodes("&e&lTop #2"));
                hologram.spawn();

                hologram.addLineBelow(ChatColor.translateAlternateColorCodes("&6Kills&7: &f" + kills.getValue()));
            } else if (counter == 3) {
                LeaderboardNPC npc = new LeaderboardNPC(kills.getKey(), kills3);

                Hologram hologram = HologramAPI.createHologram(kills3.add(0, 3, 0), ChatColor.translateAlternateColorCodes("&e&lTop #3"));
                hologram.spawn();

                hologram.addLineBelow(ChatColor.translateAlternateColorCodes("&6Kills&7: &f" + kills.getValue()));
            } else if (counter == 4) {
                LeaderboardNPC npc = new LeaderboardNPC(kills.getKey(), kills4);

                Hologram hologram = HologramAPI.createHologram(kills4.add(0, 3, 0), ChatColor.translateAlternateColorCodes("&7&lTop #4"));
                hologram.spawn();

                hologram.addLineBelow(ChatColor.translateAlternateColorCodes("&6Kills&7: &f" + kills.getValue()));
            } else if (counter == 5) {
                LeaderboardNPC npc = new LeaderboardNPC(kills.getKey(), kills5);

                Hologram hologram = HologramAPI.createHologram(kills5.add(0, 3, 0), ChatColor.translateAlternateColorCodes("&7&lTop #5"));
                hologram.spawn();

                hologram.addLineBelow(ChatColor.translateAlternateColorCodes("&6Kills&7: &f" + kills.getValue()));
            }

            counter++;
        }

        Location wins1 = getFocusAngleLocation(new Location(Bukkit.getWorld("lobby"), 6, 100, 6), new Location(Bukkit.getWorld("lobby"), -17.5, 96, 1.5));
        Location wins2 = getFocusAngleLocation(new Location(Bukkit.getWorld("lobby"), 6, 100, 6), new Location(Bukkit.getWorld("lobby"), -16.5, 96, 4.5));
        Location wins3 = getFocusAngleLocation(new Location(Bukkit.getWorld("lobby"), 6, 100, 6), new Location(Bukkit.getWorld("lobby"), -18.5, 96, -1.5));
        Location wins4 = getFocusAngleLocation(new Location(Bukkit.getWorld("lobby"), 6, 100, 6), new Location(Bukkit.getWorld("lobby"), -14.5, 96, 7.5));
        Location wins5 = getFocusAngleLocation(new Location(Bukkit.getWorld("lobby"), 6, 100, 6), new Location(Bukkit.getWorld("lobby"), -18.5, 97, -4.5));

        int rataCounter = 1;
        for (Map.Entry<UUID, Object> kills : ProfileUtils.getInstance().getSortedValues("winnedGames", Sorts.descending("winnedGames"), Filters.gte("winnedGames", 1), 5).entrySet()) {

            if (rataCounter == 1) {
                LeaderboardNPC npc = new LeaderboardNPC(kills.getKey(), wins1);

                Hologram hologram = HologramAPI.createHologram(wins1.add(0, 3, 0), ChatColor.translateAlternateColorCodes("&b&lTop #1"));
                hologram.spawn();

                hologram.addLineBelow(ChatColor.translateAlternateColorCodes("&6Wins&7: &f" + kills.getValue()));
            } else if (rataCounter == 2) {
                LeaderboardNPC npc = new LeaderboardNPC(kills.getKey(), wins2);

                Hologram hologram = HologramAPI.createHologram(wins2.add(0, 3, 0), ChatColor.translateAlternateColorCodes("&e&lTop #2"));
                hologram.spawn();

                hologram.addLineBelow(ChatColor.translateAlternateColorCodes("&6Wins&7: &f" + kills.getValue()));
            } else if (rataCounter == 3) {
                LeaderboardNPC npc = new LeaderboardNPC(kills.getKey(), wins3);

                Hologram hologram = HologramAPI.createHologram(wins3.add(0, 3, 0), ChatColor.translateAlternateColorCodes("&e&lTop #3"));
                hologram.spawn();

                hologram.addLineBelow(ChatColor.translateAlternateColorCodes("&6Wins&7: &f" + kills.getValue()));
            } else if (rataCounter == 4) {
                LeaderboardNPC npc = new LeaderboardNPC(kills.getKey(), wins4);

                Hologram hologram = HologramAPI.createHologram(wins4.add(0, 3, 0), ChatColor.translateAlternateColorCodes("&7&lTop #4"));
                hologram.spawn();

                hologram.addLineBelow(ChatColor.translateAlternateColorCodes("&6Wins&7: &f" + kills.getValue()));
            } else if (rataCounter == 5) {
                LeaderboardNPC npc = new LeaderboardNPC(kills.getKey(), wins5);

                Hologram hologram = HologramAPI.createHologram(wins5.add(0, 3, 0), ChatColor.translateAlternateColorCodes("&7&lTop #5"));
                hologram.spawn();

                hologram.addLineBelow(ChatColor.translateAlternateColorCodes("&6Wins&7: &f" + kills.getValue()));
            }

            rataCounter++;
        }
    }

    private Location getFocusAngleLocation(Location focusLocation, Location location) {
        float yaw = (float) (90 + (180 * Math.atan2(location.getZ() - focusLocation.getZ(), location.getX() - focusLocation.getX()) / Math.PI));
        location.setYaw(yaw);

        return location;
    }

    @Override
    public String getDescription() {
        return "Yeaheay";
    }
}
