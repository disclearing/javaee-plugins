package me.redis.practice.team;

import lombok.Getter;
import lombok.Setter;

import me.redis.practice.enums.TeamStatus;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.match.IMatch;
import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class Team {
    private UUID uniqueId;
    private UUID leaderUuid;
    @Setter private TeamStatus status;
    @Setter private IMatch match;
    @Setter private boolean open;

    private List<UUID> members = new ArrayList<>();
    private List<UUID> invites = new ArrayList<>();
    private Map<UUID, Ladder> requests = new HashMap<>();

    public Team(Player leader) {
        this.uniqueId = UUID.randomUUID();
        this.leaderUuid = leader.getUniqueId();
        this.status = TeamStatus.IDLE;
        this.members.add(leader.getUniqueId());
    }

    public Player getLeader() {
        return Bukkit.getPlayer(leaderUuid);
    }

    public List<Player> getMembers() {
        List<Player> players = new ArrayList<>();
        Iterator<UUID> iterator = this.members.iterator();

        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Player p = Bukkit.getPlayer(uuid);

            if (p == null || !p.isOnline()) {
                iterator.remove();
            } else {
                players.add(p);
            }
        }

        return players;
    }

    public List<String> getMembersNames() {
        List<String> players = new ArrayList<>();

        for (Player player : getMembers()) {
            players.add(player.getName());
        }

        return players;
    }

    public void sendMessage(String message) {
        for (Player player : getMembers()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes(message));
        }
    }

    public boolean hasInvited(UUID uuid) {
        return this.invites.contains(uuid);
    }

    public void addInvite(UUID uuid) {
        this.invites.add(uuid);
    }

    public void removeInvite(UUID uuid) {
        this.invites.remove(uuid);
    }

    public boolean hasPlayer(UUID uuid) {
        return this.members.contains(uuid);
    }

    public void addPlayer(Player player) {
        this.members.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        this.members.remove(player.getUniqueId());
    }

    public boolean hasRequest(Team team) {
        return this.requests.containsKey(team.getUniqueId());
    }

    public void addRequest(Team sender, Ladder ladder) {
        new FancyMessage(
                "You have been sent a team duel request by ").color(ChatColor.GRAY)
                .then(sender.getLeader().getName()).color(ChatColor.AQUA)
                .then(" [Click to Accept]").color(ChatColor.GREEN).command("/duel accept " + sender.getLeader().getName())
                .send(this.getLeader());

        sender.getLeader().sendMessage(ChatColor.GRAY + "You have sent " + ChatColor.AQUA + this.getLeader().getName() + ChatColor.GRAY + " a team duel request.");
        this.requests.put(sender.getUniqueId(), ladder);
    }

    public Ladder getRequest(Team sender) {
        return this.requests.get(sender.getUniqueId());
    }

    public void removeRequest(Team party) {
        this.requests.remove(party.getUniqueId());
    }
}