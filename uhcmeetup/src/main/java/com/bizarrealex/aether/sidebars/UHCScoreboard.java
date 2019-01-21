package com.bizarrealex.aether.sidebars;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import me.javaee.meetup.Meetup;
import me.javaee.meetup.enums.GameState;
import me.javaee.meetup.handlers.Scenario;
import me.javaee.meetup.profile.ProfileUtils;
import me.javaee.meetup.tasks.GameTimeTask;
import me.javaee.meetup.utils.DurationFormatter;
import me.javaee.meetup.utils.StringCommon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UHCScoreboard implements BoardAdapter, Listener {
    public UHCScoreboard(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    public String getTitle(Player player) {
        return "&6&lSilex &7┃&c UHCMeetup";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        List<String> strings = new ArrayList<>();

        strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
        if (Meetup.getPlugin().getGameManager().getGameState() == GameState.WAITING) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Players&7: &f" + Bukkit.getOnlinePlayers().size()));
            strings.add(ChatColor.translateAlternateColorCodes('&', ""));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Gamemodes&7:"));
            for (Scenario scenario : Meetup.getPlugin().getScenarios()) {
                if (scenario.getVotes() != 0) {
                    if (scenario.getVotes() >= 5) {
                        strings.add(" " + scenario.getName() + "&7:&f " + "5/5");
                    } else {
                        strings.add(" " + scenario.getName() + "&7:&f " + scenario.getVotes() + "/5");
                    }
                }
            }
            strings.add("");
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Elo&7: &f") + ProfileUtils.getInstance().calculateColor(player) + ProfileUtils.getInstance().getProfile(player.getUniqueId()).getElo());
        } else if (Meetup.getPlugin().getGameManager().getGameState() == GameState.INGAME) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6GameTime&7:&f " + StringCommon.niceTime(GameTimeTask.getNumOfSeconds(), false)));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Players&7:&f " + Meetup.getPlugin().getGameManager().getAlivePlayers().size() + "/" + Meetup.getPlugin().getGameManager().getJoinedPlayers()));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Kills&7: &f" + ProfileUtils.getInstance().getProfile(player.getUniqueId()).getMatchKills()));
            strings.add((ChatColor.GOLD + "Border" + ChatColor.GRAY + ": " + ChatColor.WHITE + Meetup.getPlugin().getBorderShrinkTask().currentRadius + (Meetup.getPlugin().getBorderShrinkTask().isRanBefore() ? ChatColor.GRAY + " (" + ChatColor.RED + border() + ChatColor.GRAY + ")" : "")).replace("-400", "50"));
            if (Meetup.getPlugin().getTimerManager().getCombatTimer().getRemaining(player) > 0) {
                strings.add("");
                strings.add(ChatColor.RED + "No Clean" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatter.getRemaining(Meetup.getPlugin().getTimerManager().getCombatTimer().getRemaining(player), true));
            }
        } else if (Meetup.getPlugin().getGameManager().getGameState() == GameState.END) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Winner&7:&f " + Meetup.getPlugin().getGameManager().getAlivePlayers().get(0).getName()));
        } else if (Meetup.getPlugin().getGameManager().getGameState() == GameState.STARTING) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Players&7: &f" + Bukkit.getOnlinePlayers().size()));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Starting in&7: &f" + Meetup.getPlugin().getGameManager().getCountdown()));
            strings.add(ChatColor.translateAlternateColorCodes('&', ""));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Gamemodes&7:"));
            for (Scenario scenario : Meetup.getPlugin().getScenarios()) {
                if (scenario.getVotes() != 0) {
                    if (scenario.getVotes() >= 5) {
                        strings.add(" " + scenario.getName() + "&7:&f " + "5/5");
                    } else {
                        strings.add(" " + scenario.getName() + "&7:&f " + scenario.getVotes() + "/5");
                    }
                }
            }
            strings.add("");
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Elo&7: &f") + ProfileUtils.getInstance().calculateColor(player) + ProfileUtils.getInstance().getProfile(player.getUniqueId()).getElo());
        }
        strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));

        if (strings.size() == 2) {
            return null;
        }

        return strings;
    }

    public String border() {
        int time = (Meetup.getPlugin().getBorderShrinkTask().shrinkInterval * 60 - Meetup.getPlugin().getBorderShrinkTask().getCounter());
        String sb;

        if (time > 59) {
            time = ((Meetup.getPlugin().getBorderShrinkTask().shrinkInterval * 60 - Meetup.getPlugin().getBorderShrinkTask().getCounter()) / 60);
            sb = time + "m";
        } else {
            time = ((Meetup.getPlugin().getBorderShrinkTask().shrinkInterval * 60 - Meetup.getPlugin().getBorderShrinkTask().getCounter()));
            sb = time + "s";
        }

        return sb;
    }
}
