package com.bizarrealex.aether.sidebars;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import me.javaee.uhc.UHC;
import me.javaee.uhc.command.commands.WorldLoaderCommand;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.tasks.DeathmatchTask;
import me.javaee.uhc.tasks.GameTimeTask;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.Configurator;
import me.javaee.uhc.utils.DurationFormatter;
import me.javaee.uhc.utils.StringCommon;
import net.badlion.worldborder.WorldFillTask;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.*;

public class UHCScoreboard implements BoardAdapter, Listener {
    public UHCScoreboard(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private DecimalFormat decimalFormat = new DecimalFormat("#.#");

    @Override
    public String getTitle(Player player) {
        return "&6&lSilex &c[UHC #" + UHC.getInstance().getUhcNumber() + "]";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        List<String> strings = new ArrayList<>();

        if (WorldLoaderCommand.generating) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&7Percentage: &f" + WorldFillTask.percetage));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&7Loaded Chunks: &f" + WorldFillTask.loadedChunks));
            strings.add(ChatColor.translateAlternateColorCodes('&', "&7Started Since: &f" + DurationFormatUtils.formatDuration(System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime(), "mm:ss", true)));
            strings.add(ChatColor.GRAY + "Memory" + ": " + ChatColor.WHITE + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L) + "/" + Runtime.getRuntime().totalMemory() / 1048576L + "MB");
            strings.add(ChatColor.GRAY + "TPS" + ChatColor.GRAY + ": " + ChatColor.WHITE + (Bukkit.spigot().getTPS()[0] >= 19.99 ? "*20" : decimalFormat.format(Bukkit.spigot().getTPS()[0])));
        } else {
            if (UHC.getInstance().getGameManager().getModerators().contains(player) || UHC.getInstance().getGameManager().getHost() == player) {
                strings.add(ChatColor.translateAlternateColorCodes("&7&m----------------------"));
                strings.add(ChatColor.GOLD + "Ticks Per Second" + ChatColor.GRAY + ": " + ChatColor.WHITE + (Bukkit.spigot().getTPS()[0] >= 19.99 ? "*20" : decimalFormat.format(Bukkit.spigot().getTPS()[0])));
                strings.add(ChatColor.GOLD + "Memory" + ChatColor.GRAY + ": " + ChatColor.WHITE + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L) + "/" + Runtime.getRuntime().totalMemory() / 1048576L + "MB");
                strings.add(ChatColor.GOLD + "Vanished" + ChatColor.GRAY + ": " + ChatColor.WHITE + (ProfileUtils.getInstance().getProfile(player.getUniqueId()).isVanish() ? "True" : "False"));
            }
            strings.add(ChatColor.translateAlternateColorCodes("&7&m----------------------"));
            Configurator.Option teamSize = UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.TEAMSIZE.name());

            if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
                if (UHC.getInstance().getPracticeManager().getPracticePlayers().contains(player)) {
                    strings.add(ChatColor.GOLD + "Gamemodes" + ChatColor.GRAY + ":");
                    for (Scenario scenario : UHC.getInstance().getScenarios()) {
                        if (scenario.isEnabled()) {
                            strings.add(" - " + WordUtils.capitalizeFully(scenario.getName()));
                        }
                    }
                    strings.add("");
                    strings.add(ChatColor.translateAlternateColorCodes("&6Warriors&7:&f " + UHC.getInstance().getPracticeManager().getPracticePlayers().size()));
                    strings.add(ChatColor.translateAlternateColorCodes("&6Killstreak&7: &f" + UHC.getInstance().getPracticeManager().getKillStreak().get(player)));
                    if (UHC.getInstance().getGameManager().isHasCountdownStart()) {
                        if (UHC.getInstance().getGameManager().getCountdown() > 60) {
                            strings.add(ChatColor.GOLD + "Starting in" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatUtils.formatDuration(UHC.getInstance().getGameManager().getCountdown() * 1000, "mm:ss"));
                        } else {
                            strings.add(ChatColor.GOLD + "Starting in" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatUtils.formatDuration(UHC.getInstance().getGameManager().getCountdown() * 1000, "ss"));
                        }
                    }
                    if (UHC.getInstance().getTimerManager().getNocleanTimer().getRemaining(player) > 0) {
                        strings.add(ChatColor.RED + ChatColor.BOLD.toString() + "No Clean" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatter.getRemaining(UHC.getInstance().getTimerManager().getNocleanTimer().getRemaining(player), true));
                    }
                } else {
                    strings.add(ChatColor.GOLD + "Teams" + ChatColor.GRAY + ": " + ChatColor.WHITE + (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() < 2 ? "FFA" : "To" + UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue()));
                    strings.add("");
                    strings.add(ChatColor.GOLD + "Gamemodes" + ChatColor.GRAY + ":");
                    for (Scenario scenario : UHC.getInstance().getScenarios()) {
                        if (scenario.isEnabled()) {
                            strings.add(" - " + WordUtils.capitalizeFully(scenario.getName()));
                        }
                    }
                    strings.add("");
                    strings.add(ChatColor.GOLD + "Participants" + ChatColor.GRAY + ": " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());

                    if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 50) {
                        strings.add("");
                        strings.add(ChatColor.RED + UHCTeam.getByName("Red").getDisplayName() + ChatColor.GRAY + ": " + ChatColor.WHITE + UHCTeam.getByName("Red").getPlayerList().size());
                        strings.add(ChatColor.BLUE + UHCTeam.getByName("Blue").getDisplayName() + ChatColor.GRAY + ": " + ChatColor.WHITE + UHCTeam.getByName("Blue").getPlayerList().size());
                    }

                    if (UHC.getInstance().getGameManager().isHasCountdownStart()) {
                        if (UHC.getInstance().getGameManager().getCountdown() > 60) {
                            strings.add(ChatColor.GOLD + "Starting in" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatUtils.formatDuration(UHC.getInstance().getGameManager().getCountdown() * 1000, "mm:ss"));
                        } else {
                            strings.add(ChatColor.GOLD + "Starting in" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatUtils.formatDuration(UHC.getInstance().getGameManager().getCountdown() * 1000, "ss"));
                        }
                    }
                }
            } else if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
                strings.add(ChatColor.GOLD + "Game Time" + ChatColor.GRAY + ": " + ChatColor.WHITE + StringCommon.niceTime(GameTimeTask.getNumOfSeconds(), false));
                strings.add(ChatColor.GOLD + "Remaining" + ChatColor.GRAY + ": " + ChatColor.WHITE + UHC.getInstance().getGameManager().getAlivePlayers().size() + "/" + UHC.getInstance().getGameManager().getJoinedPlayers());

                if (!DeathmatchTask.isNow) {
                    strings.add((ChatColor.GOLD + "Border" + ChatColor.GRAY + ": " + ChatColor.WHITE + UHC.getInstance().getBorderShrinkTask().currentRadius + (UHC.getInstance().getBorderShrinkTask().isRanBefore() ? ChatColor.GRAY + " (" + ChatColor.RED + border() + ChatColor.GRAY + ")" : "")).replace("-400", "50"));
                } else {
                    strings.add(ChatColor.GOLD + "Deathmatch" + ChatColor.GRAY + ": " + ChatColor.WHITE + "20x20");
                }

                if (UHC.getInstance().getGameManager().getKills().get(player.getUniqueId()) == null) {
                    strings.add(ChatColor.GOLD + "Kills" + ChatColor.GRAY + ": " + ChatColor.WHITE + "0");
                } else {
                    strings.add(ChatColor.GOLD + "Kills" + ChatColor.GRAY + ": " + ChatColor.WHITE + UHC.getInstance().getGameManager().getKills().get(player.getUniqueId()));
                }

                if (UHC.getInstance().isDeathmatch()) {
                    if (DeathmatchTask.sbCounter > 0) {
                        strings.add(ChatColor.GOLD + "Deathmatch" + ChatColor.GRAY + ": " + ChatColor.WHITE + StringCommon.niceTime(DeathmatchTask.sbCounter, false));
                    }
                }

                if (teamSize.getValue() != null && (int) teamSize.getValue() > 1) {
                    strings.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------" + ChatColor.BLUE);
                    strings.add(ChatColor.translateAlternateColorCodes("&6Teams Left&7: &f" + UHC.getInstance().getTeams().size()));
                    if (UHCTeam.getByUUID(player.getUniqueId()) != null) {
                        strings.add(ChatColor.translateAlternateColorCodes("&6Team Kills&7: &f" + UHCTeam.getByUUID(player.getUniqueId()).getKills()));
                    }
                }

                if (GameTimeTask.getNumOfSeconds() < GameTimeTask.healTime || GameTimeTask.getNumOfSeconds() < GameTimeTask.pvpTime) {
                    strings.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------" + ChatColor.BLUE);
                }

                if (GameTimeTask.getNumOfSeconds() < GameTimeTask.healTime) {
                    strings.add(ChatColor.GOLD + "Final Heal" + ChatColor.GRAY + ": " + ChatColor.WHITE + StringCommon.niceTime((GameTimeTask.healTime - GameTimeTask.getNumOfSeconds()), false));
                }
                if (GameTimeTask.getNumOfSeconds() < GameTimeTask.pvpTime) {
                    if (!UHC.getInstance().getGameManager().isPvpEnable()) {
                        strings.add(ChatColor.GOLD + "PvP Protection" + ChatColor.GRAY + ": " + ChatColor.WHITE + StringCommon.niceTime((GameTimeTask.pvpTime - GameTimeTask.getNumOfSeconds()), false));
                    }
                }

                if (UHC.getInstance().getTimerManager().getNocleanTimer().getRemaining(player) > 0) {
                    strings.add("");
                    strings.add(ChatColor.RED + "No Clean" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatter.getRemaining(UHC.getInstance().getTimerManager().getNocleanTimer().getRemaining(player), true));
                }
            } else if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
                if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() <= 1) {
                    strings.add(ChatColor.translateAlternateColorCodes("&6Scattering&7: &f" + UHC.getInstance().getGameManager().getContador() + "/" + UHC.getInstance().getGameManager().getAlivePlayers().size()));
                } else {
                    strings.add(ChatColor.translateAlternateColorCodes("&6Scattering&7:"));
                    strings.add(ChatColor.translateAlternateColorCodes("&e Teams&7: &f" + UHC.getInstance().getGameManager().getContador() + "/" + UHC.getInstance().getTeams().size()));
                    strings.add("");
                }

                if (UHC.getInstance().getGameManager().getCountdown() > 60) {
                    strings.add(ChatColor.GOLD + "Starting in" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatUtils.formatDuration(UHC.getInstance().getGameManager().getCountdown() * 1000, "mm:ss"));
                } else {
                    strings.add(ChatColor.GOLD + "Starting in" + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatUtils.formatDuration(UHC.getInstance().getGameManager().getCountdown() * 1000, "ss"));
                }

                strings.add("");
                strings.add(ChatColor.translateAlternateColorCodes("&4&lDO NOT RELOG"));
            } else {
                if (teamSize.getValue() != null && (int) teamSize.getValue() > 1) {
                    if ((int) teamSize.getValue() >= 50) {
                        strings.add(ChatColor.GOLD + "Winner Team" + ChatColor.GRAY + ": ");
                        strings.add(ChatColor.YELLOW + " " + UHCTeam.getByUUID(UHC.getInstance().getGameManager().getAlivePlayers().get(0)).getDisplayName());
                    } else {
                        strings.add(ChatColor.GOLD + "Winners" + ChatColor.GRAY + ": ");

                        for (UUID teamPlayers : UHCTeam.getByUUID(UHC.getInstance().getGameManager().getAlivePlayers().get(0)).getPlayerList()) {
                            strings.add(ChatColor.translateAlternateColorCodes(" - " + Bukkit.getOfflinePlayer(teamPlayers).getName()));
                            strings.add(ChatColor.translateAlternateColorCodes("  Kills: " + ProfileUtils.getInstance().getProfile(teamPlayers).getMatchKills()));
                        }
                    }
                } else {
                    strings.add(ChatColor.GOLD + "Winner" + ChatColor.GRAY + ": " + ChatColor.WHITE + UHC.getInstance().getGameManager().getWinner().getName());
                    strings.add(ChatColor.YELLOW + " Kills" + ChatColor.GRAY + ": " + ChatColor.WHITE + ProfileUtils.getInstance().getProfile(UHC.getInstance().getGameManager().getWinner().getUniqueId()).getMatchKills());
                }
            }
        }

        strings.add(ChatColor.translateAlternateColorCodes("&7&m----------------------"));

        if (strings.size() == 2) {
            return null;
        }

        return strings;
    }

    public String border() {
        int time = (UHC.getInstance().getBorderShrinkTask().borderShrinkInterval * 60 - UHC.getInstance().getBorderShrinkTask().getCounter());
        String sb;

        if (time > 59) {
            time = ((UHC.getInstance().getBorderShrinkTask().borderShrinkInterval * 60 - UHC.getInstance().getBorderShrinkTask().getCounter()) / 60);
            sb = time + "m";
        } else {
            time = ((UHC.getInstance().getBorderShrinkTask().borderShrinkInterval * 60 - UHC.getInstance().getBorderShrinkTask().getCounter()));
            sb = time + "s";
        }

        return sb;
    }
}
