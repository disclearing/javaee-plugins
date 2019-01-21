package me.redis.practice.match.type;

import lombok.Getter;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.enums.MatchStatus;
import me.redis.practice.enums.MatchType;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.match.IMatch;
import me.redis.practice.match.cache.MatchCache;
import me.redis.practice.profile.Profile;
import me.redis.practice.queue.type.TeamVsTeamQueue;
import me.redis.practice.team.Team;
import me.redis.practice.utils.PracticeUtils;
import mkremins.fanciful.FancyMessage;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

public class TeamMatch implements IMatch {
    @Getter private UUID identifier;
    @Getter private TeamVsTeamQueue queue;
    @Getter private Ladder ladder;
    @Getter private Arena arena;
    @Getter private MatchStatus matchStatus;
    @Getter private MatchType matchType;
    @Getter private Timestamp startTimestamp;
    @Getter private Long startNano;
    @Getter private Map<UUID, Boolean> team1 = new HashMap<>();
    @Getter private Map<UUID, Boolean> team2 = new HashMap<>();
    @Getter private List<UUID> spectators = new ArrayList<>();
    @Getter private List<UUID> leftMatch = new ArrayList<>();

    public TeamMatch(TeamVsTeamQueue queue, Ladder ladder, Arena arena, MatchType matchType, List<Player> team1, List<Player> team2) {
        this.identifier = UUID.randomUUID();
        this.queue = queue;
        this.ladder = ladder;
        this.arena = arena;
        this.matchStatus = MatchStatus.STARTING;
        this.matchType = matchType;

        for (Player p : team1) {
            this.team1.put(p.getUniqueId(), true);

            for (Player p2 : team1) {
                p.showPlayer(p2);
            }

            for (Player p2 : team2) {
                p.showPlayer(p2);
            }

            PracticeUtils.resetPlayer(p);

            Profile profile = Practice.getPlugin().getProfileManager().getProfile(p);

            profile.setCurrentMatch(this);
            profile.setStatus(ProfileStatus.MATCH);
            profile.showKits(ladder);

            p.updateInventory();
            p.spigot().setCollidesWithEntities(true);
            p.setCanPickupItems(true);
            p.teleport(arena.getPos1());
            p.setMaximumNoDamageTicks(19);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePlaying in &a" + arena.getName() + " &emade by '&7" + StringUtils.join(arena.getAuthors(), ", ") + "&e'."));
        }

        for (Player p : team2) {
            this.team2.put(p.getUniqueId(), true);

            for (Player p2 : team1) {
                p.showPlayer(p2);
            }

            for (Player p2 : team2) {
                p.showPlayer(p2);
            }

            PracticeUtils.resetPlayer(p);

            Profile profile = Practice.getPlugin().getProfileManager().getProfile(p);

            profile.setCurrentMatch(this);
            profile.setStatus(ProfileStatus.MATCH);
            profile.showKits(ladder);

            p.updateInventory();
            p.spigot().setCollidesWithEntities(true);
            p.setCanPickupItems(true);
            p.teleport(arena.getPos2());
            p.setMaximumNoDamageTicks(19);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePlaying in &a" + arena.getName() + " &emade by '&7" + StringUtils.join(arena.getAuthors(), ", ") + "&e'."));
        }

        for (Player p : getPlayers()) {
            Practice.getPlugin().getEntityHider().hideAllPlayers(p);
        }

        for (Player p : getPlayers()) {
            for (Player p2 : getPlayers()) {
                Practice.getPlugin().getEntityHider().showEntity(p, p2);
                Practice.getPlugin().getEntityHider().showEntity(p2, p);
            }
        }

        new BukkitRunnable() {
            private int i = 5;

            public void run() {
                if (matchStatus == MatchStatus.FINISHED || matchStatus == MatchStatus.CANCELLED) {
                    this.cancel();
                    return;
                }

                if (this.i <= 0) {
                    this.cancel();

                    startMatch();
                    playSound(Sound.NOTE_PIANO, 2.0f);

                    return;
                }

                if (this.i != 1) {
                    sendMessage(ChatColor.GREEN + "Starting in " + ChatColor.YELLOW + this.i + ChatColor.GREEN + " seconds!");
                } else {
                    sendMessage(ChatColor.GREEN + "Starting in " + ChatColor.YELLOW + this.i + ChatColor.GREEN + " second!");
                }

                playSound(Sound.NOTE_PIANO, 1.0f);
                --this.i;
            }
        }.runTaskTimer(Practice.getPlugin(), 0L, 20L);
    }

    @Override
    public UUID getUniqueId() {
        return identifier;
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.addAll(this.getTeam1Players());
        players.addAll(this.getTeam2Players());
        return players;
    }

    @Override
    public List<Player> getTeam(Player player) {
        if (this.team1.containsKey(player.getUniqueId())) {
            return this.getTeam1Players();
        } else {
            return this.getTeam2Players();
        }
    }

    @Override
    public List<Player> getOpponents(Player player) {
        if (this.team1.containsKey(player.getUniqueId())) {
            return this.getTeam2Players();
        } else {
            return this.getTeam1Players();
        }
    }

    @Override
    public Player getOpponent(Player player) {
        return null;
    }

    @Override
    public int getOpponentsLeft(Player player) {
        if (this.team1.containsKey(player.getUniqueId())) {
            return Collections.frequency(new ArrayList<>(this.team2.values()), true);
        } else {
            return Collections.frequency(new ArrayList<>(this.team1.values()), true);
        }
    }

    @Override
    public void handleDeath(Player player, Location location, String deathMessage) {
        if (this.team1.containsKey(player.getUniqueId())) {
            if (!this.team1.get(player.getUniqueId())) {
                return;
            }
        } else {
            if (!this.team2.get(player.getUniqueId())) {
                return;
            }
        }

        if (deathMessage.contains("has left the match.")) {
            this.leftMatch.add(player.getUniqueId());
        } else {
            playSound(Sound.AMBIENCE_THUNDER, 10.0f);
        }

        if (this.team1.containsKey(player.getUniqueId())) {
            this.team1.replace(player.getUniqueId(), false);

            Set<Boolean> values = new HashSet<>(this.team1.values());
            boolean isUnique = values.size() == 1;

            if (isUnique) {
                this.endMatch(this.team2, this.team1);
            } else {
                for (Player p : this.getPlayers()) {
                    p.hidePlayer(player);
                }

                this.startSpectating(player);

                if (location != null) {
                    player.teleport(location.clone().add(0, 3, 0));
                }
            }
        } else if (this.team2.containsKey(player.getUniqueId())) {
            this.team2.replace(player.getUniqueId(), false);

            Set<Boolean> values = new HashSet<>(this.team2.values());
            boolean isUnique = values.size() == 1;

            if (isUnique) {
                this.endMatch(this.team1, this.team2);
            } else {
                for (Player p : this.getPlayers()) {
                    p.hidePlayer(player);
                }

                this.startSpectating(player);

                if (location != null) {
                    player.teleport(location.clone().add(0, 3, 0));
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline()) {
                            player.setHealth(20);
                            player.setFireTicks(0);
                        }
                    }
                }.runTaskLater(Practice.getPlugin(), 10L);
            }
        }
    }

    @Override
    public boolean isDead(Player player) {
        return this.team1.containsKey(player.getUniqueId()) ? !this.team1.get(player.getUniqueId()) : !this.team2.get(player.getUniqueId());
    }

    @Override
    public void sendMessage(String message) {
        for (Player p : getPlayers()) {
            p.sendMessage(message);
        }

        for (UUID uuid : this.spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).sendMessage(message);
            } else {
                this.spectators.remove(uuid);
            }
        }
    }

    public void sendMessage(FancyMessage fancyMessage) {
        for (Player p : getPlayers()) {
            fancyMessage.send(p);
        }

        for (UUID uuid : this.spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                fancyMessage.send(Bukkit.getPlayer(uuid));
            } else {
                this.spectators.remove(uuid);
            }
        }
    }

    private void playSound(Sound sound, float idk2) {
        for (Player p : getPlayers()) {
            p.playSound(p.getLocation(), sound, 10.0f, idk2);
        }

        for (UUID uuid : this.spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(), sound, 10.0F, idk2);
            } else {
                this.spectators.remove(uuid);
            }
        }
    }

    private List<Player> getTeam1Players() {
        List<Player> players = new ArrayList<>();

        for (UUID uuid : this.team1.keySet()) {
            Player p = Bukkit.getPlayer(uuid);

            if (p == null || !p.isOnline()) {
                continue;
            }

            players.add(p);
        }

        return players;
    }

    private List<Player> getTeam2Players() {
        List<Player> players = new ArrayList<>();

        for (UUID uuid : this.team2.keySet()) {
            Player p = Bukkit.getPlayer(uuid);

            if (p == null || !p.isOnline()) {
                continue;
            }

            players.add(p);
        }

        return players;
    }

    @Override
    public void cancelMatch(String cancelReason) {
        this.sendMessage(ChatColor.RED + "The match has been cancelled for: " + ChatColor.RED + cancelReason);

        if (queue != null) {
            queue.setPlayingAmount(queue.getPlayingAmount() - 2);
        }

        for (Player p : getPlayers()) {
            if (leftMatch.contains(p.getUniqueId())) continue;

            PracticeUtils.resetPlayer(p);

            Profile profile = Practice.getPlugin().getProfileManager().getProfile(p);
            profile.setStatus(ProfileStatus.LOBBY);
            profile.setCurrentMatch(null);

            if (profile.getTeam() != null) {
                if (profile.getTeam().getLeader() == p) {
                    p.getInventory().setContents(PracticeUtils.getTeamLeaderInventory());
                } else {
                    p.getInventory().setContents(PracticeUtils.getTeamMemberInventory());
                }
            } else {
                p.getInventory().setContents(PracticeUtils.getLobbyInventory());
            }

            p.updateInventory();
            p.setAllowFlight(false);
            p.setMaximumNoDamageTicks(19);

            p.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
        }

        cleanSpectators();

        Practice.getPlugin().getMatchManager().getMatches().remove(this.identifier);

        this.matchStatus = MatchStatus.CANCELLED;
    }

    private void startMatch() {
        this.startTimestamp = new Timestamp(System.currentTimeMillis());
        this.startNano = System.nanoTime();
        this.matchStatus = MatchStatus.ONGOING;

        sendMessage(ChatColor.GREEN + "Duel starting now!");
    }

    private void endMatch(Map<UUID, Boolean> winners, Map<UUID, Boolean> losers) {
        FancyMessage inventoriesClickables = new FancyMessage("Inventories (click to view): ").color(ChatColor.GOLD);

        if (queue != null) {
            queue.setPlayingAmount(queue.getPlayingAmount() - 2);
        }

        int i = 0;

        for (UUID winClick : winners.keySet()) {
            Player player = Bukkit.getPlayer(winClick);
            if (player == null || !player.isOnline()) {
                continue;
            }

            inventoriesClickables.then(player.getName() + ", ").color(ChatColor.YELLOW).command("/_ " + player.getName());
        }

        for (UUID loseClick : losers.keySet()) {
            Player player = Bukkit.getPlayer(loseClick);
            if (player == null || !player.isOnline()) {
                continue;
            }

            inventoriesClickables.then(player.getName() + ", ").color(ChatColor.YELLOW).command("/_ " + player.getName());
        }

        for (Entry<UUID, Boolean> entry : winners.entrySet()) {
            i++;

            Player player = Bukkit.getPlayer(entry.getKey());

            if (player == null || !player.isOnline()) {
                continue;
            }

/*            if (i == winners.size()) {
                winnerClickables.then(player.getName()).color(ChatColor.GREEN).command("/_ " + player.getName());
            } else {
                winnerClickables.then(player.getName() + ", ").color(ChatColor.GREEN).command("/_ " + player.getName());
            }*/

            if (entry.getValue()) {
                MatchCache.storeInventory(player, false);
            }

            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);
            profile.setUnrankedWins(profile.getUnrankedWins() + 1);
            profile.setStatus(ProfileStatus.LOBBY);
            profile.setCurrentMatch(null);
            profile.save();

            PracticeUtils.resetPlayer(player);

            if (profile.getTeam() != null) {
                if (profile.getTeam().getLeader() == player) {
                    player.getInventory().setContents(PracticeUtils.getTeamLeaderInventory());
                } else {
                    player.getInventory().setContents(PracticeUtils.getTeamMemberInventory());
                }
            } else {
                player.getInventory().setContents(PracticeUtils.getLobbyInventory());
            }

            player.setMaximumNoDamageTicks(19);
            player.setAllowFlight(false);
            player.updateInventory();

            Practice.getPlugin().getEntityHider().hideAllPlayers(player);
            player.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
        }

        i = 0;

        for (Entry<UUID, Boolean> entry : losers.entrySet()) {
            i++;

            Player player = Bukkit.getPlayer(entry.getKey());

            if (player == null || !player.isOnline()) {
                continue;
            }

           /* if (i == losers.size()) {
                loserClickables.then(player.getName()).color(ChatColor.RED).command("/_ " + player.getName());
            } else {
                loserClickables.then(player.getName() + ", ").color(ChatColor.RED).command("/_ " + player.getName());
            }*/

            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);
            profile.setUnrankedLosses(profile.getUnrankedLosses() + 1);
            profile.setStatus(ProfileStatus.LOBBY);
            profile.setCurrentMatch(null);
            profile.save();

            PracticeUtils.resetPlayer(player);

            if (profile.getTeam() != null) {
                if (profile.getTeam().getLeader() == player) {
                    player.getInventory().setContents(PracticeUtils.getTeamLeaderInventory());
                } else {
                    player.getInventory().setContents(PracticeUtils.getTeamMemberInventory());
                }
            } else {
                player.getInventory().setContents(PracticeUtils.getLobbyInventory());
            }

            player.setMaximumNoDamageTicks(19);
            player.setAllowFlight(false);
            player.updateInventory();

            Practice.getPlugin().getEntityHider().hideAllPlayers(player);
            player.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
        }

        //sendMessage(ChatColor.YELLOW + "Winners: " + Practice.getPlugin().getProfileManager().getProfile()); TODO
        this.sendMessage(inventoriesClickables);

        Practice.getPlugin().getMatchManager().getMatches().remove(this.identifier);

        cleanSpectators();

        this.matchStatus = MatchStatus.FINISHED;
    }

    private void startSpectating(Player player) {
        PracticeUtils.resetPlayer(player);
        player.updateInventory();
        player.setMaximumNoDamageTicks(19);
        player.setAllowFlight(true);
        player.setFlying(true);

        for (Player p : getPlayers()) {
            Practice.getPlugin().getEntityHider().hideEntity(p, player);
            Practice.getPlugin().getEntityHider().showEntity(player, p);
        }

        for (UUID uuid : this.spectators) {
            Player p = Bukkit.getPlayer(uuid);

            if (p != null && p.isOnline()) {
                Practice.getPlugin().getEntityHider().hideEntity(p, player);
                Practice.getPlugin().getEntityHider().hideEntity(player, p);
            }
        }
    }

    private void cleanSpectators() {
        Iterator<UUID> specIterator = spectators.iterator();

        while (specIterator.hasNext()) {
            Player p = Bukkit.getPlayer(specIterator.next());
            specIterator.remove();

            if (p != null) {
                Practice.getPlugin().getSpectatorManager().stopSpectating(p, false);
                p.sendMessage(ChatColor.RED + "The match has finished.");
            }
        }
    }
}