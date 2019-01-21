package com.bizarrealex.aether;

import com.bizarrealex.aether.event.BoardCreateEvent;
import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.BoardEntry;
import lombok.Getter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.events.GameStartEvent;
import me.javaee.uhc.team.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static com.bizarrealex.aether.AetherOptions.defaultOptions;

/**
 * TODO: Add documentation to methods, etc
 * TODO: Fix inconsistent cooldown scores
 * TODO: Finish other board formats
 */

public class Aether implements Listener {

    @Getter
    private JavaPlugin plugin;
    @Getter
    private AetherOptions options;
    @Getter
    BoardAdapter adapter;

    public Aether(JavaPlugin plugin, BoardAdapter adapter, AetherOptions options) {
        this.options = options;
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        setAdapter(adapter);
        run();
    }

    public Aether(JavaPlugin plugin, BoardAdapter adapter) {
        this(plugin, adapter, defaultOptions());
    }

    public Aether(JavaPlugin plugin) {
        this(plugin, null, defaultOptions());
    }

    private void run() {
        new BukkitRunnable() {
            public void run() {
                if (adapter == null) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    Board board = Board.getByPlayer(player);
                    if (board != null) {
                        List<String> scores = adapter.getScoreboard(player, board, board.getCooldowns());
                        List<String> translatedScores = new ArrayList<>();

                        if (scores == null) {

                            if (!board.getEntries().isEmpty()) {

                                for (BoardEntry boardEntry : board.getEntries()) {
                                    boardEntry.remove();
                                }

                                board.getEntries().clear();
                            }

                            continue;
                        }

                        for (String line : scores) {
                            translatedScores.add(ChatColor.translateAlternateColorCodes('&', line));
                        }

                        if (!options.scoreDirectionDown()) {
                            Collections.reverse(scores);
                        }

                        Scoreboard scoreboard = board.getScoreboard();
                        Objective objective = board.getObjective();

                        if (!(objective.getDisplayName().equals(adapter.getTitle(player)))) {
                            objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', adapter.getTitle(player)));
                        }

                        outer:
                        for (int i = 0; i < scores.size(); i++) {
                            String text = scores.get(i);
                            int position;
                            if (options.scoreDirectionDown()) {
                                position = 15 - i;
                            } else {
                                position = i + 1;
                            }

                            Iterator<BoardEntry> iterator = new ArrayList<>(board.getEntries()).iterator();
                            while (iterator.hasNext()) {
                                BoardEntry boardEntry = iterator.next();
                                Score score = objective.getScore(boardEntry.getKey());

                                if (score != null && boardEntry.getText().equals(ChatColor.translateAlternateColorCodes('&', text))) {
                                    if (score.getScore() == position) {
                                        continue outer;
                                    }
                                }
                            }

                            int positionToSearch = options.scoreDirectionDown() ? 15 - position : position - 1;

                            iterator = board.getEntries().iterator();
                            while (iterator.hasNext()) {
                                BoardEntry boardEntry = iterator.next();
                                int entryPosition = scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(boardEntry.getKey()).getScore();

                                if (!options.scoreDirectionDown()) {
                                    if (entryPosition > scores.size()) {
                                        iterator.remove();
                                        boardEntry.remove();
                                    }
                                }

                            }

                            BoardEntry entry = board.getByPosition(positionToSearch);
                            if (entry == null) {
                                new BoardEntry(board, text).send(position);
                            } else {
                                entry.setText(text).setup().send(position);
                            }

                            if (board.getEntries().size() > scores.size()) {
                                iterator = board.getEntries().iterator();
                                while (iterator.hasNext()) {
                                    BoardEntry boardEntry = iterator.next();
                                    if ((!translatedScores.contains(boardEntry.getText())) || Collections.frequency(board.getBoardEntriesFormatted(), boardEntry.getText()) > 1) {
                                        iterator.remove();
                                        boardEntry.remove();
                                    }
                                }
                            }
                        }

                        player.setScoreboard(scoreboard);
                        updateTablist(player);
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 3L);
    }

    public void setAdapter(BoardAdapter adapter) {
        this.adapter = adapter;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Board board = Board.getByPlayer(player);

            if (board != null) {
                Board.getBoards().remove(board);
            }

            Bukkit.getPluginManager().callEvent(new BoardCreateEvent(new Board(player, this, options), player));
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (Board.getByPlayer(event.getPlayer()) == null) {
            Bukkit.getPluginManager().callEvent(new BoardCreateEvent(new Board(event.getPlayer(), this, options), event.getPlayer()));
        }

        event.getPlayer().setMaximumNoDamageTicks(19);

        setHealthAndList(event.getPlayer());
    }

    private static Team getExistingOrCreateNewTeam(String string, Scoreboard scoreboard, ChatColor prefix) {
        Team toReturn = scoreboard.getTeam(string);

        if (toReturn == null) {
            toReturn = scoreboard.registerNewTeam(string);
            toReturn.setPrefix(prefix + "");
        }

        return toReturn;
    }

    private static void updateTablist(Player target) {
        Team noClean = getExistingOrCreateNewTeam("clean", Board.getByPlayer(target).getScoreboard(), ChatColor.valueOf(UHC.getInstance().getConfig().getString("TAB_PREFIXES.NOCLEAN")));
        Team enemy = getExistingOrCreateNewTeam("enemy", Board.getByPlayer(target).getScoreboard(), ChatColor.valueOf(UHC.getInstance().getConfig().getString("TAB_PREFIXES.ENEMY")));
        Team spectator = getExistingOrCreateNewTeam("spectator", Board.getByPlayer(target).getScoreboard(), ChatColor.valueOf(UHC.getInstance().getConfig().getString("TAB_PREFIXES.SPECTATOR")));
        Team friendly = getExistingOrCreateNewTeam("members", Board.getByPlayer(target).getScoreboard(), ChatColor.valueOf(UHC.getInstance().getConfig().getString("TAB_PREFIXES.TEAM")));
        Team[] teams = new Team[] { noClean, enemy, spectator, friendly };

        for (Player online : Bukkit.getOnlinePlayers()) {
            UHCTeam friendTeam = UHCTeam.getByUUID(target.getUniqueId());
            UHCTeam enemyTeam = UHCTeam.getByUUID(online.getUniqueId());

            if (online == target) {
                if (!(friendly.hasEntry(target.getName()))) {
                    Stream.of(teams).forEach(team -> team.removeEntry(target.getName()));
                    friendly.addEntry(target.getName());
                }
                continue;
            }

            if (UHC.getInstance().getTimerManager().getNocleanTimer().getRemaining(online.getUniqueId()) > 0) {
                if (!(noClean.hasEntry(online.getName()))) {
                    Stream.of(teams).forEach(team -> team.removeEntry(target.getName()));
                    noClean.addEntry(online.getName());
                }
            } else {
                if (UHC.getInstance().getSpectatorManager().getSpectators().contains(online) || UHC.getInstance().getGameManager().getHelpers().contains(online) || UHC.getInstance().getGameManager().getModerators().contains(online) || UHC.getInstance().getGameManager().getHost() == online) {
                    if (!(spectator.hasEntry(online.getName()))) {
                        Stream.of(teams).forEach(team -> team.removeEntry(target.getName()));
                        spectator.addEntry(online.getName());
                    }
                } else {
                    if (!(enemy.hasEntry(online.getName()))) {
                        Stream.of(teams).forEach(team -> team.removeEntry(target.getName()));
                        enemy.addEntry(online.getName());
                    }
                }

                if (friendTeam != null && enemyTeam != null) {
                    if (friendTeam == enemyTeam) {
                        if (friendly.hasEntry(online.getName())) continue;

                        if (!(friendly.hasEntry(online.getName()))) {
                            Stream.of(teams).forEach(team -> team.removeEntry(target.getName()));
                            friendly.addEntry(online.getName());
                        }
                    }
                }
            }
        }
    }

    public void setHealthAndList(Player player) {
        if (Board.getByPlayer(player).getScoreboard() != null) {
            Scoreboard scoreboard = Board.getByPlayer(player).getScoreboard();

            if (scoreboard.getObjective("namehealth") == null) {
                scoreboard.registerNewObjective("namehealth", "health");
            }

            Objective objective = scoreboard.getObjective("namehealth");

            if (scoreboard.getObjective("tabhealth") == null) {
                scoreboard.registerNewObjective("tabhealth", "health");
            }

            Objective tab = scoreboard.getObjective("tabhealth");

            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(ChatColor.DARK_RED + " ❤");

            tab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Board board = Board.getByPlayer(event.getPlayer());
        if (board != null) {
            Board.getBoards().remove(board);
        }
    }

}
