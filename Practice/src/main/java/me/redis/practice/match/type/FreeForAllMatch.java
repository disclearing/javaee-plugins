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
import me.redis.practice.utils.Cuboid;
import me.redis.practice.utils.LocationUtils;
import me.redis.practice.utils.PracticeUtils;
import mkremins.fanciful.FancyMessage;

import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

public class FreeForAllMatch implements IMatch {
    @Getter private UUID uniqueId;
    @Getter private Ladder ladder;
    @Getter private Arena arena;
    @Getter private MatchStatus matchStatus;
    @Getter private MatchType matchType;
    @Getter private Timestamp startTimestamp;
    @Getter private Long startNano;
    @Getter private Map<UUID, Boolean> alive = new HashMap<>();
    @Getter private List<UUID> spectators = new ArrayList<>();
    @Getter private List<UUID> leftMatch = new ArrayList<>();
    @Getter private Map<Player, Integer> missedPotions;

    public FreeForAllMatch(Ladder ladder, Arena arena, List<Player> players) {
        this.uniqueId = UUID.randomUUID();
        this.ladder = ladder;
        this.arena = arena;
        this.matchStatus = MatchStatus.STARTING;
        this.matchType = MatchType.TEAM_VS_TEAM;
        missedPotions = new HashMap<>();

        int i = 0;

        for (Player member : players) {
            for (Player p2 : players) {
                member.showPlayer(p2);
            }

            this.alive.put(member.getUniqueId(), true);

            PracticeUtils.resetPlayer(member);

            Profile profile = Practice.getPlugin().getProfileManager().getProfile(member);

           /* if (profile.getStatus() == ProfileStatus.EDITING) {
                ManagerHandler.getKitEditManager().getEditKits().remove(p.getUniqueId());
            }*/ //TODO

            profile.setCurrentMatch(this);
            profile.setStatus(ProfileStatus.MATCH);

            profile.showKits(ladder);

            member.updateInventory();
            member.spigot().setCollidesWithEntities(true);
            member.setCanPickupItems(true);
            member.setMaximumNoDamageTicks(19);
            missedPotions.put(member, 0);

            Cuboid cuboid = new Cuboid(LocationUtils.getLocation(arena.getFfaLocation1()), LocationUtils.getLocation(arena.getFfaLocation2()));

            double x =  Math.random() * (cuboid.getUpperX() - cuboid.getLowerX()) + cuboid.getLowerX();
            double z = Math.random() * (cuboid.getUpperZ() - cuboid.getLowerZ()) + cuboid.getLowerZ();
            World world = cuboid.getWorld();
            Location location = new Location(world, x, cuboid.getLowerY(), z);

            member.teleport(location);

            member.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePlaying in &a" + arena.getName() + " &emade by '&7" + StringUtils.join(arena.getAuthors(), ", ") + "&e'."));
        }

        for (Player showMembers : getPlayers()) {
            Practice.getPlugin().getEntityHider().hideAllPlayers(showMembers);
        }

        for (Player showMembers : getPlayers()) {
            for (Player showMembersAgain : getPlayers()) {
                Practice.getPlugin().getEntityHider().showEntity(showMembers, showMembersAgain);
                Practice.getPlugin().getEntityHider().showEntity(showMembersAgain, showMembers);
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
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        if(!this.alive.isEmpty()) {
            Iterator<UUID> uuidIterator = this.alive.keySet().iterator();

            while(uuidIterator.hasNext()) {
                Player p = Bukkit.getPlayer(uuidIterator.next());

                if(p == null) {
                    uuidIterator.remove();
                }
                else {
                    players.add(p);
                }
            }
        }

        return players;
    }

    @Override
    public List<Player> getTeam(Player player) {
        return Collections.emptyList();
    }

    @Override
    public List<Player> getOpponents(Player player) {
        List<Player> enemies = new ArrayList<>();

        for (Player p : getPlayers()) {
            if (player != p) enemies.add(p);
        }

        return enemies;
    }

    @Override
    public Player getOpponent(Player player) {
        return null;
    }

    @Override
    public int getOpponentsLeft(Player player) {
        return alive.size() - 1;
    }

    @Override
    public void handleDeath(Player player, Location location, String deathMessage) {
        if (this.isDead(player)) {
            return;
        }

        this.alive.replace(player.getUniqueId(), false);

        this.sendMessage(deathMessage);

        if (deathMessage.contains("has left the match.")) {
            this.leftMatch.add(player.getUniqueId());
        } else {
            playSound(Sound.AMBIENCE_THUNDER, 10.0f);
        }

        MatchCache.storeInventory(player, true, missedPotions.get(player));

        int alive = 0;

        for (Boolean bool : this.alive.values()) {
            if (bool) {
                alive++;
            }
        }

        if (alive == 1) {
            this.endMatch(getLastAlive());
        }
        else {
            for (Player p : this.getPlayers()) {
                p.hidePlayer(player);
            }

            player.setMaximumNoDamageTicks(19);

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

    @Override
    public boolean isDead(Player player) {
        return !this.alive.get(player.getUniqueId());
    }

    private Player getLastAlive() {
        for (Entry<UUID, Boolean> entry : this.alive.entrySet()) {
            if (entry.getValue()) return Bukkit.getPlayer(entry.getKey());
        }

        return null;
    }

    @Override
    public void sendMessage(String message) {
        for (Player p : getPlayers()) {
            p.sendMessage(message);
        }

        for (UUID uuid : this.spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).sendMessage(message);
            }
            else {
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
            }
            else {
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
            }
            else {
                this.spectators.remove(uuid);
            }
        }
    }

    @Override
    public void cancelMatch(String cancelReason) {
        this.sendMessage(ChatColor.DARK_RED + "The match has been canceled for: " + ChatColor.RED + cancelReason);

        for (Player player : getPlayers()) {
            if (this.leftMatch.contains(player.getUniqueId())) continue;

            PracticeUtils.resetPlayer(player);

            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);
            profile.setStatus(ProfileStatus.LOBBY);
            profile.setCurrentMatch(null);

            if (profile.getTeam() != null) {
                if (profile.getTeam().getLeader() == player) {
                    player.getInventory().setContents(PracticeUtils.getTeamLeaderInventory());
                }
                else {
                    player.getInventory().setContents(PracticeUtils.getTeamMemberInventory());
                }
            }
            else {
                player.getInventory().setContents(PracticeUtils.getLobbyInventory());
            }

            player.setMaximumNoDamageTicks(19);
            player.setAllowFlight(false);
            player.updateInventory();

            Practice.getPlugin().getEntityHider().showAllPlayers(player);
            player.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
        }

        cleanSpectators();

        Practice.getPlugin().getMatchManager().getMatches().remove(this.uniqueId);

        this.matchStatus = MatchStatus.CANCELLED;
    }

    private void startMatch() {
        this.startTimestamp = new Timestamp(System.currentTimeMillis());
        this.startNano = System.nanoTime();
        this.matchStatus = MatchStatus.ONGOING;

        sendMessage(ChatColor.GREEN + "Duel starting now!");
    }

    private void endMatch(Player winner) {
        MatchCache.storeInventory(winner, false, missedPotions.get(winner));

        Profile winnerProfile = Practice.getPlugin().getProfileManager().getProfile(winner);

        winnerProfile.setUnrankedWins(winnerProfile.getUnrankedWins() + 1);
        winnerProfile.save();

        FancyMessage winnerClickables = new FancyMessage("Winner: " + winner.getName()).color(ChatColor.YELLOW).command("/_ " + winner.getName());
        FancyMessage loserClickables = new FancyMessage("Inventories (click to view): ").color(ChatColor.GOLD);

        int alive = 0;
        int i = 0;

        for (Entry<UUID, Boolean> entry : this.alive.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());

            if (player == null) {
                this.alive.remove(entry.getKey());
                continue;
            }

            if (entry.getValue()) {
                alive++;
                MatchCache.storeInventory(player, false, missedPotions.get(player));
            } else {
                i++;

                player.getInventory().setContents(PracticeUtils.getLobbyInventory());
                player.updateInventory();
                player.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
                Practice.getPlugin().getEntityHider().hideAllPlayers(player);

                for (Player p : getPlayers()) {
                    loserClickables.then(p.getName() + ", ").color(ChatColor.YELLOW).command("/_ " + player.getName());
                }
            }

            Profile data = Practice.getPlugin().getProfileManager().getProfile(player);

            if (player == winner) {
                data.setUnrankedWins(data.getUnrankedWins() + 1);
            }
            else {
                data.setUnrankedLosses(data.getUnrankedLosses() + 1);
            }

            data.setStatus(ProfileStatus.LOBBY);
            data.setCurrentMatch(null);
            data.save();

            PracticeUtils.resetPlayer(player);

            if (data.getTeam() != null) {
                if (data.getTeam().getLeader() == player) {
                    player.getInventory().setContents(PracticeUtils.getTeamLeaderInventory());
                }
                else {
                    player.getInventory().setContents(PracticeUtils.getTeamMemberInventory());
                }
            }
            else {
                player.getInventory().setContents(PracticeUtils.getLobbyInventory());
            }

            player.setMaximumNoDamageTicks(19);
            player.setAllowFlight(false);
            player.updateInventory();

            Practice.getPlugin().getEntityHider().hideAllPlayers(player);
            player.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
        }

        this.sendMessage(winnerClickables);
        this.sendMessage(loserClickables);

        Practice.getPlugin().getMatchManager().getMatches().remove(this.uniqueId);

        cleanSpectators();

        this.matchStatus = MatchStatus.FINISHED;
    }

    private void startSpectating(Player player) {
        PracticeUtils.resetPlayer(player);
        player.updateInventory();
        player.setMaximumNoDamageTicks(19);
        player.setAllowFlight(true);
        player.setFlying(true);

        Practice.getPlugin().getProfileManager().getProfile(player).setSpectatingMatch(this);

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

        Practice.getPlugin().getProfileManager().getProfile(player).setStatus(ProfileStatus.SPECTATOR);
    }

    private void cleanSpectators() {
        Iterator<UUID> specIterator = spectators.iterator();

        while(specIterator.hasNext()) {
            Player p = Bukkit.getPlayer(specIterator.next());
            specIterator.remove();

            if (p != null) {
                Practice.getPlugin().getSpectatorManager().stopSpectating(p, false);
                p.sendMessage(ChatColor.RED + "The match has finished.");
            }
        }
    }
}