package me.redis.kohi.scoreboard.sidebars;

import me.redis.kohi.SurvivalGames;
import me.redis.kohi.database.information.Information;
import me.redis.kohi.game.states.GameState;
import me.redis.kohi.scoreboard.board.Board;
import me.redis.kohi.scoreboard.board.BoardAdapter;
import me.redis.kohi.scoreboard.board.cooldown.BoardCooldown;
import me.redis.kohi.utils.DurationFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SurvivalGamesSidebar implements BoardAdapter, Listener {
    private Information information = SurvivalGames.getPlugin().getInformationManager().getInformation();
    private SurvivalGames survivalGames = SurvivalGames.getPlugin();

    public SurvivalGamesSidebar(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String getTitle(Player player) {
        return "&6&lSilexSG &c[Season #1]";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        List<String> lines = new ArrayList<>();

        if (survivalGames.getGameManager().getGameState() == GameState.WAITING) {
            lines.add("&e&lRemaining&7: &f" + Bukkit.getOnlinePlayers().size());
        } else if (survivalGames.getGameManager().getGameState() == GameState.PLAYING) {
            lines.add("&e&lRemaining&7: &f" + survivalGames.getGameManager().getAlivePlayers());

            if (SurvivalGames.getPlugin().getTimerManager().getProtection().getRemaining(player) > 0) {
                lines.add("&c&lProtection&7: &f" + DurationFormatter.getRemaining(SurvivalGames.getPlugin().getTimerManager().getProtection().getRemaining(player), true));
            }

            if (SurvivalGames.getPlugin().getTimerManager().getFeastTimer().getRemaining() > 0) {
                lines.add("&d&lFeast&7: &f" + DurationFormatter.getRemaining(SurvivalGames.getPlugin().getTimerManager().getFeastTimer().getRemaining(), false));
            }

            if (SurvivalGames.getPlugin().getBorderManager().isStarted()) {
                lines.add("&3&lBorder&7: &f" + SurvivalGames.getPlugin().getBorderManager().getRadius() + " (" + SurvivalGames.getPlugin().getBorderManager().getSeconds() + "s)");
            }

            if (SurvivalGames.getPlugin().getTimerManager().getEnderpearlTimer().getRemaining(player) > 0) {
                lines.add("&3&lEnderpearl&7: &f" + DurationFormatter.getRemaining(SurvivalGames.getPlugin().getTimerManager().getEnderpearlTimer().getRemaining(player), true));
            }
        } else if (survivalGames.getGameManager().getGameState() == GameState.ENDED) {
            lines.add("&e&lWinner&7: &f" + survivalGames.getGameManager().getWinner().getName());
        }

        if (information.isBars()) {
            if (!lines.isEmpty()) {
                lines.add(0, "&7&m----------------------");
                lines.add(lines.size(), "&7&m----------------------");
            } else {
                return null;
            }
        } else {
            if (lines.isEmpty()) return null;
        }

        return lines;
    }
}
