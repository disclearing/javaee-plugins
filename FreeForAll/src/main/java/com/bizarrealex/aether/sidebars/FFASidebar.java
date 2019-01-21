package com.bizarrealex.aether.sidebars;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import me.javaee.ffa.FFA;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.profiles.status.PlayerStatus;
import me.javaee.ffa.utils.BukkitUtils;
import me.javaee.ffa.utils.DurationFormatter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FFASidebar implements BoardAdapter, Listener {
    public FFASidebar(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    public String getTitle(Player player) {
        return "&6&lSilex &c[Free For All]";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        List<String> strings = new ArrayList<>();
        Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

        double kills = profile.getKills();
        double deaths = profile.getDeaths();
        double kdr = kills / deaths;

        strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------&r"));

        if (FFA.getPlugin().getInformationManager().getInformation().isKothStarted()) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Koth"));
            strings.add(ChatColor.translateAlternateColorCodes('&', " &7» &eCoordinates &7(" + FFA.getPlugin().getInformationManager().getInformation().getKothCuboid().getCenter().getBlockX() + ", " + FFA.getPlugin().getInformationManager().getInformation().getKothCuboid().getCenter().getBlockZ() + ")"));
            strings.add(ChatColor.translateAlternateColorCodes('&', " &7» &eRemaining&7: &f" + BukkitUtils.niceTime(FFA.getPlugin().getKoth().getCapSeconds(), false)));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
        }

        strings.add(ChatColor.translateAlternateColorCodes('&', "&6Kills&7: &f" + profile.getKills()));
        strings.add(ChatColor.translateAlternateColorCodes('&', "&6Deaths&7: &f" + profile.getDeaths()));
        if (profile.getKillstreak() > 0) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Killstreak&7: &f" + profile.getKillstreak()));
        }
        strings.add(ChatColor.translateAlternateColorCodes('&', "&6KDR&7: &f" + (kills == 0 && deaths == 0 ? "N/A" : new DecimalFormat("#.##").format(kdr))));
        strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
        if (FFA.getPlugin().getInformationManager().getInformation().isRecording()) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&eAddress&7: &fsilexpvp.net"));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
        }

        if (FFA.getPlugin().getTimerManager().getTeleportTimer().getRemaining(player) > 0) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&c&lTeleportation&7: &f" + DurationFormatter.getRemaining(FFA.getPlugin().getTimerManager().getTeleportTimer().getRemaining(player), true)));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
        }

        if (profile.getPlayerStatus() == PlayerStatus.STAFF) {
            strings.add(ChatColor.translateAlternateColorCodes('&', " &7● &eVanished&7: &f" + (profile.isVanished() ? "true" : "false")));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
        }

        if (strings.size() == 2) {
            return null;
        }
        return strings;
    }
}
