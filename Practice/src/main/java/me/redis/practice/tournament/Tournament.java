package me.redis.practice.tournament;

import lombok.Getter;
import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.match.IMatch;
import me.redis.practice.match.type.SoloMatch;
import me.redis.practice.profile.Profile;
import me.redis.practice.tournament.types.TournamentTypes;
import me.redis.practice.utils.PracticeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class Tournament {
    private Ladder ladder;
    private TournamentTypes type;
    private int maxPlayers;
    private List<Player> players;
    private List<IMatch> matches;
    int counter = 11;

    public Tournament(Ladder ladder, TournamentTypes type, int maxPlayers) {
        this.ladder = ladder;
        this.type = type;
        this.maxPlayers = maxPlayers;

        players = new ArrayList<>();
        matches = new ArrayList<>();

        Practice.getPlugin().getTournamentManager().getTournaments().add(this);
    }

    public void startTournament() {
        new BukkitRunnable() {
            public void run() {
                if (counter <= 0) {
                    if (players.size() <= 1) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe tournament could not be started."));
                        cancel();
                        return;
                    }

                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe tournament has started with &a" + players.size() + " &eplayers."));
                    generateRandomMatch();

                    cancel();
                    return;
                }

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe tournament will start in &a" + counter + "seconds&e..."));
                counter--;
            }
        }.runTaskTimerAsynchronously(Practice.getPlugin(), 0L, 20L);
    }

    public void generateRandomMatch() {
        List<Player> generatedPlayers = new ArrayList<>(players);

        int a = new Random(generatedPlayers.size()).nextInt();
        Player firstPlayer = generatedPlayers.get(a);
        generatedPlayers.remove(generatedPlayers.get(a));

        int b = new Random(generatedPlayers.size()).nextInt();
        Player secondPlayer = generatedPlayers.get(b);
        generatedPlayers.remove(generatedPlayers.get(b));

        Profile firstProfile = Practice.getPlugin().getProfileManager().getProfile(firstPlayer);
        Profile secondProfile = Practice.getPlugin().getProfileManager().getProfile(secondPlayer);

        if (firstProfile.getStatus() == ProfileStatus.SPECTATOR) {
            PracticeUtils.resetPlayer(firstPlayer);
            firstProfile.setStatus(ProfileStatus.LOBBY);
        }

        if (secondProfile.getStatus() == ProfileStatus.SPECTATOR) {
            PracticeUtils.resetPlayer(secondPlayer);
            secondProfile.setStatus(ProfileStatus.LOBBY);
        }

        if (firstProfile.getStatus() != ProfileStatus.LOBBY) {
            players.remove(firstPlayer);
            Bukkit.broadcastMessage(ChatColor.RED + firstPlayer.getName() + " has been removed for the tournament.");

            finishMatch();
            return;
        }

        if (secondProfile.getStatus() != ProfileStatus.LOBBY) {
            players.remove(secondPlayer);
            Bukkit.broadcastMessage(ChatColor.RED + firstPlayer.getName() + " has been removed for the tournament.");

            finishMatch();
            return;
        }

        new SoloMatch(null, ladder, Practice.getPlugin().getArenaManager().getRandomArena(), false, firstPlayer, secondPlayer);
        players.forEach(player -> {
            if (player != firstPlayer && player != secondPlayer) {
                Practice.getPlugin().getSpectatorManager().startSpectating(player, firstPlayer);
            }
        });
    }

    public void finishMatch() {
        final int[] starter = {6};

        new BukkitRunnable() {
            public void run() {
                if (starter[0] <= 0) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eA tournament match has started."));
                    generateRandomMatch();
                    cancel();
                    return;
                }

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe match will start in &a" + starter[0] + "seconds&e."));
                starter[0]--;
            }
        }.runTaskTimerAsynchronously(Practice.getPlugin(), 0L, 20L);
    }
}
