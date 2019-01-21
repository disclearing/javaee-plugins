package me.redis.practice.scoreboard.sidebars;

import me.redis.practice.Practice;
import me.redis.practice.enums.MatchStatus;
import me.redis.practice.enums.MatchType;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.match.IMatch;
import me.redis.practice.profile.Profile;
import me.redis.practice.scoreboard.scoreboard.Board;
import me.redis.practice.scoreboard.scoreboard.BoardAdapter;
import me.redis.practice.scoreboard.scoreboard.cooldown.BoardCooldown;
import me.redis.practice.utils.DurationFormatter;
import me.redis.practice.utils.TimeUtil;
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

    @Override
    public String getTitle(Player player) {
        return Practice.getPlugin().getConfig().getString("SCOREBOARD.SIDEBAR.TITLE");
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        List<String> strings = new ArrayList<>();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------&r"));

        if (Practice.getPlugin().getTimerManager().getRestartTimer().getRemaining() > 0) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&c&lServer restart&7: &f" + DurationFormatter.getRemaining(Practice.getPlugin().getTimerManager().getRestartTimer().getRemaining(), true)));
        }

        if (Practice.getPlugin().getTimerManager().getQueuesTimer().getRemaining() > 0) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&c&lQueues restart&7: &f" + DurationFormatter.getRemaining(Practice.getPlugin().getTimerManager().getQueuesTimer().getRemaining(), true)));
        }

        if (profile.getStatus() == ProfileStatus.LOBBY) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Online&7: &f" + Bukkit.getOnlinePlayers().size()));

            if (profile.getTeam() != null) {
                strings.add(ChatColor.translateAlternateColorCodes('&', "&aTeam Members&7:"));
                if (profile.getTeam().getMembersNames().size() > 5) {
                    strings.add(ChatColor.translateAlternateColorCodes('&', " &7- &e" + profile.getTeam().getMembersNames().size() + " more players..."));
                } else {
                    for (String name : profile.getTeam().getMembersNames()) {
                        if (profile.getTeam().getLeader().getName().equalsIgnoreCase(name)) {
                            strings.add(ChatColor.translateAlternateColorCodes('&', " &7- &d" + name));
                        } else {
                            strings.add(ChatColor.translateAlternateColorCodes('&', " &7- &e" + name));
                        }
                    }
                }
            }
        } else if (profile.getStatus() == ProfileStatus.MATCH) {
            String timer = profile.getCurrentMatch().getMatchStatus() == MatchStatus.STARTING ? "Starting..." : TimeUtil.formatElapsingNanoseconds(profile.getCurrentMatch().getStartNano());

            if (profile.getCurrentMatch().getMatchStatus() == MatchStatus.ONGOING) {
                strings.add(ChatColor.translateAlternateColorCodes('&', "&6Duration&7: &f" + timer));
            }

            if (profile.getCurrentMatch().getMatchType() == MatchType.ONE_VS_ONE) {
                strings.add(ChatColor.translateAlternateColorCodes('&', "&cOpponent&7: &f" + profile.getCurrentMatch().getOpponent(player).getName()));
            } else if (profile.getCurrentMatch().getMatchType() == MatchType.TEAM_VS_TEAM) {
                IMatch match = profile.getCurrentMatch();

                strings.add(ChatColor.RED + "Opponents:");
                for (Player members : match.getOpponents(player)) {
                    strings.add(" - " + ChatColor.YELLOW + members.getName());
                }
            }

            if (Practice.getPlugin().getTimerManager().getEnderpearlTimer().getRemaining(player.getUniqueId()) > 0) {
                strings.add(ChatColor.translateAlternateColorCodes('&', "&3Enderpearl&7: &f" + DurationFormatter.getRemaining(Practice.getPlugin().getTimerManager().getEnderpearlTimer().getRemaining(player), true)));
            }
        } else if (profile.getStatus() == ProfileStatus.SPECTATOR) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Spectating a match&7:"));
            for (Player match : profile.getSpectatingMatch().getPlayers()) {
                strings.add(ChatColor.translateAlternateColorCodes('&', " &7- &e" + match.getName()));
            }
        } else if (profile.getStatus() == ProfileStatus.QUEUE) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&6Queue Info"));
            strings.add(ChatColor.translateAlternateColorCodes('&', " &7- &e" + profile.getCurrentQueue().getLadder().getName() + " " + (profile.getCurrentQueue().isRanked() ? "&7(Ranked)" : "&7(Un-Ranked)")));
            if (profile.getCurrentQueue().isRanked()) {
                strings.add(ChatColor.translateAlternateColorCodes('&', " &7- &a[" + profile.getQueueData().getMinRange() + " -> " + profile.getQueueData().getMaxRange() + "]"));
            }
        }

        strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));

        if (strings.size() == 2) {
            return null;
        }

        return strings;
    }

    public String getHealth(Player player) {
        int health = ((int) player.getHealth() / 2);
        DecimalFormat format = new DecimalFormat("#");

        if (health >= 8) {
            return ChatColor.GREEN + format.format(health) + " ❤";
        } else if (health >= 6) {
            return ChatColor.YELLOW + format.format(health) + " ❤";
        } else if (health >= 4) {
            return ChatColor.RED + format.format(health) + " ❤";
        } else {
            return ChatColor.DARK_RED + format.format(health) + " ❤";
        }
    }
}
