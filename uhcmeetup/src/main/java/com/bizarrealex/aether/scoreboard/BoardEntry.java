package com.bizarrealex.aether.scoreboard;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Accessors(chain = true)
public class BoardEntry {
    
    @Getter private Board board;
    @Getter @Setter private String text;
    @Getter private String originalText;
    @Getter private String key;
    @Getter private Team team;

    public BoardEntry(Board board, String text) {
        this.board = board;
        this.text = text;
        this.originalText = text;
        this.key = board.getNewKey(this);

        setup();
    }

    public BoardEntry setup() {
        Scoreboard scoreboard = board.getScoreboard();

        text = ChatColor.translateAlternateColorCodes('&', text);

        String teamName = key;
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        if (scoreboard.getTeam(teamName) != null) {
            team = scoreboard.getTeam(teamName);
        } else {
            team = scoreboard.registerNewTeam(teamName);
        }

        if (!(team.getEntries().contains(key))) {
            team.addEntry(key);
        }

        if (!(board.getEntries().contains(this))) {
            board.getEntries().add(this);
        }

        return this;
    }

    public BoardEntry send(int position) {
        Objective objective = board.getObjective();

        if (text.length() > 16) {
            team.setPrefix(text.substring(0, 16));

            String suffix = ChatColor.getLastColors(team.getPrefix()) + text.substring(16, text.length());

            if (suffix.length() > 16) {

                if (suffix.length() - 2 <= 16) {
                    suffix = text.substring(16, text.length());
                    team.setSuffix(suffix.substring(0, suffix.length()));
                } else {
                    team.setSuffix(suffix.substring(0, 16));
                }

            } else {
                team.setSuffix(suffix);
            }
        } else {
            team.setSuffix("");
            team.setPrefix(text);
        }

        Score score = objective.getScore(key);
        score.setScore(position);

        return this;
    }

    public void remove() {
        board.getKeys().remove(key);
        board.getScoreboard().resetScores(key);
    }

}
