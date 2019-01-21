package me.redis.practice.match;

import me.redis.practice.arena.Arena;
import me.redis.practice.enums.MatchStatus;
import me.redis.practice.enums.MatchType;
import me.redis.practice.ladders.Ladder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface IMatch {

    UUID getUniqueId();
    
    Ladder getLadder();
    
    Arena getArena();
    
    MatchStatus getMatchStatus();

    MatchType getMatchType();

    List<Player> getPlayers();

    List<UUID> getSpectators();

    List<Player> getTeam(Player player);

    List<Player> getOpponents(Player player);

    Player getOpponent(Player player);

    Timestamp getStartTimestamp();

    Long getStartNano();

    int getOpponentsLeft(Player player);

    void sendMessage(String message);
    
    void handleDeath(Player player, Location location, String deathMessage);

    boolean isDead(Player player);
    
    void cancelMatch(String reason);

}