package me.redis.practice.queue.type;

import lombok.Getter;
import lombok.Setter;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.enums.MatchType;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.match.type.TeamMatch;
import me.redis.practice.profile.Profile;
import me.redis.practice.queue.IQueue;
import me.redis.practice.queue.QueueData;
import me.redis.practice.team.Team;
import me.redis.practice.utils.PracticeUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TeamVsTeamQueue implements IQueue {
    @Getter private UUID identifier;
    @Getter private Ladder ladder;
    @Getter @Setter private int playingAmount = 0;
    @Getter private LinkedList<QueueData> searchList;
    @Getter private BukkitTask queueTask;

    public TeamVsTeamQueue(Ladder ladder) {
        this.identifier = UUID.randomUUID();
        this.ladder = ladder;
        this.searchList = new LinkedList<>();
        this.startTask();
    }

    @Override
    public String getName() {
        return ladder.getName() + " 2v2";
    }

    @Override
    public boolean isRanked() {
        return false;
    }

    @Override
    public int getQueueingAmount() {
        return this.searchList.size();
    }

    @Override
    public void addToQueue(Object object) {
        Team team = (Team) object;

        List<Player> players = team.getMembers();

        if (players.size() != 2) {
            team.sendMessage(ChatColor.RED + "You need to have only 2 members in order to queue.");
            return;
        }

        QueueData data = new QueueData(team.getUniqueId(), this, 0);

        for (Player p : players) {
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(p);

            profile.setCurrentQueue(this);
            profile.setQueueData(data);
            profile.setStatus(ProfileStatus.QUEUE);

            if (p.getUniqueId().equals(team.getLeaderUuid())) {
                PracticeUtils.resetPlayer(p);
                p.getInventory().setContents(PracticeUtils.getQueueInventory());
                p.updateInventory();
            }

            if (p.getGameMode() == GameMode.CREATIVE) {
                p.setGameMode(GameMode.SURVIVAL);
            }
        }

        this.searchList.offer(data);
        team.sendMessage(ChatColor.YELLOW + "Added to the " + ChatColor.GREEN + ladder.getName() + ChatColor.YELLOW  + " queue, please wait for another team.");
    }

    @Override
    public void removeFromQueue(Object object) {
        this.removeFromQueue(object, true, true, false);
    }

    private void removeFromQueue(Object object, boolean send, boolean clean, boolean teleport) {
        this.searchList.remove(object);

        QueueData participant = (QueueData) object;
        Team Team = Practice.getPlugin().getTeamManager().getTeam((UUID) participant.getObject());

        List<Player> players = Team.getMembers();

        for (Player p : players) {
            Profile prof = Practice.getPlugin().getProfileManager().getProfile(p);
            prof.setCurrentQueue(null);
            prof.setQueueData(null);
            prof.setStatus(ProfileStatus.LOBBY);

            if (clean) {
                PracticeUtils.resetPlayer(p);
                p.getPlayer().getInventory().setContents(PracticeUtils.getLobbyInventory());
                p.getPlayer().updateInventory();
            }

            if (send) {
                p.getPlayer().sendMessage(ChatColor.YELLOW + "You have left the queue.");
            }
        }
    }

    private void createMatch(QueueData data1, QueueData data2) {
        Arena arena = Practice.getPlugin().getArenaManager().getRandomArena();

        this.removeFromQueue(data1, false, true, false);
        this.removeFromQueue(data2, false, true, false);

        Team Team1 = Practice.getPlugin().getTeamManager().getTeam((UUID) data1.getObject());
        Team Team2 = Practice.getPlugin().getTeamManager().getTeam((UUID) data2.getObject());

        if (arena == null) {
            Team1.sendMessage(ChatColor.RED + "There are no available arenas, you have been removed from the queue.");
            Team2.sendMessage(ChatColor.RED + "There are no available arenas, you have been removed from the queue.");

            List<Player> players = new ArrayList<>();
            players.addAll(Team1.getMembers());
            players.addAll(Team2.getMembers());

            for (Player p : players) {
                Profile profile = Practice.getPlugin().getProfileManager().getProfile(p);

                profile.setStatus(ProfileStatus.LOBBY);
                profile.setCurrentQueue(null);
                profile.setQueueData(null);

                PracticeUtils.resetPlayer(p);

                if (p.getUniqueId().equals(Team1.getLeaderUuid()) || p.getUniqueId().equals(Team2.getLeaderUuid())) {
                    profile.getPlayer().getInventory().setContents(PracticeUtils.getTeamLeaderInventory());
                } else {
                    profile.getPlayer().getInventory().setContents(PracticeUtils.getTeamMemberInventory());
                }

                profile.getPlayer().updateInventory();
            }

            return;
        }

        this.playingAmount = this.playingAmount + Team1.getMembers().size() + Team2.getMembersNames().size();

        TeamMatch match = new TeamMatch(this, this.ladder, arena, MatchType.TWO_VS_TWO, Team1.getMembers(), Team2.getMembers());
        Practice.getPlugin().getMatchManager().getMatches().put(match.getIdentifier(), match);
    }

    private void startTask() {
        this.queueTask = new BukkitRunnable() {
            public void run() {
                if (playingAmount < 0) {
                    playingAmount = 0;
                }

                Iterator<QueueData> iterator = searchList.iterator();

                while (iterator.hasNext()) {
                    QueueData search = iterator.next();

                    if (!iterator.hasNext()) {
                        continue;
                    }

                    QueueData found = iterator.next();

                    createMatch(search, found);
                }
            }
        }.runTaskTimer(Practice.getPlugin(), 0L, 2L);
    }
}