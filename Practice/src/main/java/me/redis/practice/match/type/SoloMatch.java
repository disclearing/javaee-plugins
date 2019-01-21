package me.redis.practice.match.type;

import lombok.Getter;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.match.IMatch;
import me.redis.practice.enums.MatchStatus;
import me.redis.practice.enums.MatchType;
import me.redis.practice.match.cache.MatchCache;
import me.redis.practice.profile.Profile;
import me.redis.practice.queue.type.SoloQueue;
import me.redis.practice.tournament.Tournament;
import me.redis.practice.utils.SerializationUtils;
import mkremins.fanciful.FancyMessage;
import net.minecraft.server.v1_7_R4.PacketPlayInAbilities;
import net.minecraft.server.v1_7_R4.PlayerAbilities;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.*;

public class SoloMatch implements IMatch {
    @Getter private SoloQueue queue;
    @Getter private UUID identifier;
    @Getter private Ladder ladder;
    @Getter private Arena arena;
    @Getter private Boolean isRanked;
    @Getter private MatchStatus matchStatus;
    @Getter private MatchType matchType;
    @Getter private Timestamp startTimestamp;
    @Getter private Long startNano;
    @Getter private List<UUID> spectators;
    @Getter private Player player1;
    @Getter private Player player2;
    @Getter private Map<Player, Integer> missedPotions;

    public SoloMatch(SoloQueue queue, Ladder ladder, Arena arena, Boolean isRanked, Player player1, Player player2) {
        this.queue = queue;
        this.identifier = UUID.randomUUID();
        this.ladder = ladder;
        this.arena = arena;
        this.isRanked = isRanked;
        this.spectators = new ArrayList<>();
        this.matchStatus = MatchStatus.STARTING;
        this.matchType = MatchType.ONE_VS_ONE;
        this.player1 = player1;
        this.player2 = player2;
        missedPotions = new HashMap<>();

        missedPotions.put(player1, 0);
        missedPotions.put(player2, 0);

        Practice.getPlugin().getEntityHider().hideAllPlayers(player1);
        Practice.getPlugin().getEntityHider().hideAllPlayers(player2);

        player1.setMaximumNoDamageTicks(19);
        player2.setMaximumNoDamageTicks(19);

        player1.teleport(arena.getPos1());
        player2.teleport(arena.getPos2());

        Profile profile1 = Practice.getPlugin().getProfileManager().getProfile(player1.getUniqueId());
        Profile profile2 = Practice.getPlugin().getProfileManager().getProfile(player2.getUniqueId());

        profile1.setCurrentMatch(this);
        profile2.setCurrentMatch(this);

        profile1.setStatus(ProfileStatus.MATCH);
        profile2.setStatus(ProfileStatus.MATCH);

        resetPlayer(player1);
        resetPlayer(player2);

        if (!ladder.getName().toLowerCase().equalsIgnoreCase("sumo")) {
            profile1.showKits(ladder);
            profile2.showKits(ladder);
        }

        player1.updateInventory();
        player2.updateInventory();

        player1.spigot().setCollidesWithEntities(true);
        player2.spigot().setCollidesWithEntities(true);

        player1.setCanPickupItems(true);
        player2.setCanPickupItems(true);

        if (isRanked) {
            player1.sendMessage(ChatColor.GREEN + ladder.getName() + ChatColor.YELLOW + " ranked match found: " + ChatColor.GREEN + player1.getName() + "(" + profile1.getElo().get(ladder.getName()) + ")" + ChatColor.YELLOW + " vs " + ChatColor.GREEN + player2.getName() + "(" + profile2.getElo().get(ladder.getName()) + ")");
            player2.sendMessage(ChatColor.GREEN + ladder.getName() + ChatColor.YELLOW + " ranked match found: " + ChatColor.GREEN + player1.getName() + ChatColor.YELLOW + " vs " + ChatColor.GREEN + player2.getName());
        } else {
            player1.sendMessage(ChatColor.GREEN + ladder.getName() + ChatColor.YELLOW + " unranked match found: " + ChatColor.GREEN + player1.getName() + ChatColor.YELLOW + " vs " + ChatColor.GREEN + player2.getName());
            player2.sendMessage(ChatColor.GREEN + ladder.getName() + ChatColor.YELLOW + " unranked match found: " + ChatColor.GREEN + player1.getName() + ChatColor.YELLOW + " vs " + ChatColor.GREEN + player2.getName());
        }

        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePlaying in &a" + arena.getName() + " &emade by '&7" + StringUtils.join(arena.getAuthors(), ", ") + "&e'."));
        player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePlaying in &a" + arena.getName() + " &emade by '&7" + StringUtils.join(arena.getAuthors(), ", ") + "&e'."));

        player1.showPlayer(player2);
        player2.showPlayer(player1);

        Practice.getPlugin().getEntityHider().showEntity(player1, player2);
        Practice.getPlugin().getEntityHider().showEntity(player2, player1);

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
        return Arrays.asList(player1, player2);
    }

    @Override
    public List<Player> getTeam(Player player) {
        return Collections.emptyList();
    }

    @Override
    public List<Player> getOpponents(Player player) {
        if (player.equals(this.player1)) {
            return Collections.singletonList(player2);
        }

        if (player.equals(this.player2)) {
            return Collections.singletonList(player1);
        }

        return Collections.emptyList();
    }

    @Override
    public Player getOpponent(Player player) {
        if (player.equals(this.player1)) {
            return player2;
        } else if (player.equals(this.player2)) {
            return player1;
        }

        return null;
    }

    @Override
    public int getOpponentsLeft(Player player) {
        return 1;
    }

    @Override
    public void handleDeath(Player player, Location location, String deathMessage) {
        if (location != null) {
            player.teleport(location.clone().add(0, 3, 0));
        }

        startSpectating(player);

        sendMessage(deathMessage);

        if (!deathMessage.contains("left the match")) {
            playSound(Sound.AMBIENCE_THUNDER, 10.0f);

            Practice.getPlugin().getEntityHider().hideEntity(this.player1, this.player2);
            Practice.getPlugin().getEntityHider().hideEntity(this.player2, this.player1);
        }

        MatchCache.storeInventory(player, true, missedPotions.get(player));

        if (player == this.player1) {
            this.endMatch(this.player2, this.player1);
        }

        if (player == this.player2) {
            this.endMatch(this.player1, this.player2);
        }

        if (Practice.getPlugin().getTournamentManager().getTournaments().size() > 0 && Practice.getPlugin().getTournamentManager().getTournaments().get(0) != null) {
            Tournament tournament = Practice.getPlugin().getTournamentManager().getTournaments().get(0);

            if (tournament.getMatches().contains(this)) {
                tournament.finishMatch();
            }
        }
    }

    @Override
    public boolean isDead(Player player) {
        return false;
    }

    @Override
    public void sendMessage(String message) {
        for (Player p : getPlayers()) {
            p.sendMessage(message);
        }

        for (UUID uuid : spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).sendMessage(message);
            } else {
                spectators.remove(uuid);
            }
        }
    }

    public void sendMessage(FancyMessage fancyMessage) {
        for (Player p : getPlayers()) {
            fancyMessage.send(p);
        }

        for (UUID uuid : spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                fancyMessage.send(Bukkit.getPlayer(uuid));
            } else {
                spectators.remove(uuid);
            }
        }
    }

    private void playSound(Sound sound, float idk2) {
        for (Player p : getPlayers()) {
            p.playSound(p.getLocation(), sound, 10.0f, idk2);
        }

        for (UUID uuid : spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(), sound, 10.0F, idk2);
            } else {
                spectators.remove(uuid);
            }
        }
    }

    @Override
    public void cancelMatch(String cancelReason) {
        matchStatus = MatchStatus.CANCELLED;

        sendMessage(ChatColor.RED + "The match has been cancelled for: " + ChatColor.RED + cancelReason);

        player1.hidePlayer(player2);
        player2.hidePlayer(player1);

        Profile firstProfile = Practice.getPlugin().getProfileManager().getProfile(player1.getUniqueId());
        Profile secondProfile = Practice.getPlugin().getProfileManager().getProfile(player2.getUniqueId());

        firstProfile.setStatus(ProfileStatus.LOBBY);
        secondProfile.setStatus(ProfileStatus.LOBBY);

        firstProfile.setCurrentMatch(null);
        secondProfile.setCurrentMatch(null);

        if (this.queue != null) {
            this.queue.setPlayingAmount(queue.getPlayingAmount() - 2);
        }

        resetPlayer(this.player1);
        resetPlayer(this.player2);

        player1.setMaximumNoDamageTicks(19);
        player2.setMaximumNoDamageTicks(19);

        firstProfile.giveItems();
        secondProfile.giveItems();

        player1.updateInventory();
        player2.updateInventory();


        player1.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
        player2.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());

        Practice.getPlugin().getMatchManager().getMatches().remove(identifier);

        cleanSpectators();
    }

    private void startMatch() {
        this.startTimestamp = new Timestamp(System.currentTimeMillis());
        this.startNano = System.nanoTime();
        this.matchStatus = MatchStatus.ONGOING;

        sendMessage(ChatColor.GREEN + "Duel starting now!");

        player1.setWalkSpeed(0.2F);

        player2.setWalkSpeed(0.2F);
    }

    private void endMatch(Player winner, Player loser) {
        this.matchStatus = MatchStatus.FINISHED;

        if (this.queue != null) {
            this.queue.setPlayingAmount(this.queue.getPlayingAmount() - 2);
        }

        MatchCache.storeInventory(winner, false, missedPotions.get(winner));

        winner.getInventory().clear();
        winner.getInventory().setArmorContents(null);
        winner.getActivePotionEffects().forEach(effect -> winner.removePotionEffect(effect.getType()));

        winner.setMaximumNoDamageTicks(19);
        loser.setMaximumNoDamageTicks(19);

        Profile winnerProfile = Practice.getPlugin().getProfileManager().getProfile(winner.getUniqueId());
        Profile loserProfile = Practice.getPlugin().getProfileManager().getProfile(loser.getUniqueId());

        winnerProfile.setStatus(ProfileStatus.LOBBY);
        loserProfile.setStatus(ProfileStatus.LOBBY);

        winnerProfile.setCurrentMatch(null);
        loserProfile.setCurrentMatch(null);

        winnerProfile.setMatchesPlayed(winnerProfile.getMatchesPlayed() + 1);
        loserProfile.setMatchesPlayed(loserProfile.getMatchesPlayed() + 1);

        if (isRanked) {
            winnerProfile.setRankedWins(winnerProfile.getRankedWins() + 1);
            loserProfile.setRankedLosses(loserProfile.getRankedLosses() + 1);

            int winnerElo = winnerProfile.getElo().get(ladder.getName());
            int loserElo = loserProfile.getElo().get(ladder.getName());

            int winnerNewElo = getNewRating(winnerElo, loserElo, 1);
            int loserNewElo = getNewRating(loserElo, winnerElo, 0);

            winnerProfile.getElo().put(ladder.getName(), winnerNewElo);
            loserProfile.getElo().put(ladder.getName(), loserNewElo);
        } else {
            winnerProfile.setUnrankedWins(winnerProfile.getUnrankedWins() + 1);
            loserProfile.setUnrankedLosses(loserProfile.getUnrankedLosses() + 1);
        }

        FancyMessage playerClickable;

        if (isRanked) {
            playerClickable = new FancyMessage("Inventories (click to view): ").color(ChatColor.GOLD)
                    .then(winner.getName()).color(ChatColor.YELLOW).command("/_ " + winner.getName())
                    .then(", ")
                    .then(loser.getName()).color(ChatColor.YELLOW).command("/_ " + loser.getName());
        } else {
            playerClickable = new FancyMessage("Inventories (click to view): ").color(ChatColor.GOLD)
                    .then(winner.getName()).color(ChatColor.YELLOW).command("/_ " + winner.getName())
                    .then(", ")
                    .then(loser.getName()).color(ChatColor.YELLOW).command("/_ " + loser.getName());
        }

        winnerProfile.save();
        loserProfile.save();

        resetPlayer(winner);
        resetPlayer(loser);

        Practice.getPlugin().getEntityHider().showEntity(winner, loser);
        Practice.getPlugin().getEntityHider().showEntity(loser, winner);

        for (UUID uuid : this.getSpectators()) {
            Player p = Bukkit.getPlayer(uuid);

            if (p == null || !p.isOnline()) {
                continue;
            }

            for (UUID uuid2 : this.getSpectators()) {
                Player p2 = Bukkit.getPlayer(uuid2);

                if (p2 == null || !p2.isOnline()) {
                    continue;
                }

                Practice.getPlugin().getEntityHider().showEntity(p, p2);
                Practice.getPlugin().getEntityHider().showEntity(p2, p);
            }

            Practice.getPlugin().getEntityHider().showEntity(winner, p);
            Practice.getPlugin().getEntityHider().showEntity(p, winner);
            Practice.getPlugin().getEntityHider().showEntity(loser, p);
            Practice.getPlugin().getEntityHider().showEntity(p, loser);
        }

        this.sendMessage(ChatColor.YELLOW + "Winner: " + winner.getName());
        this.sendMessage(playerClickable);
        if (isRanked) {
            this.sendMessage(ChatColor.YELLOW + "Elo Changes: " + ChatColor.GREEN + winner.getName() + "(" + winnerProfile.getElo().get(ladder.getName()) + ") " + ChatColor.RED + loser.getName() + "(" + loserProfile.getElo().get(ladder.getName()) + ")");
        }

        cleanSpectators();

        Practice.getPlugin().getMatchManager().getMatches().remove(identifier);

        new BukkitRunnable() {
            public void run() {
                if (winner.isOnline()) {
                    Profile profile = Practice.getPlugin().getProfileManager().getProfile(winner.getUniqueId());

                    if (profile != null && profile.getStatus() == ProfileStatus.LOBBY) {
                        winnerProfile.giveItems();
                        winner.updateInventory();

                        winner.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
                    }

                    winner.setFlying(false);
                    winner.setAllowFlight(false);

                    Practice.getPlugin().getEntityHider().hideAllPlayers(winner);
                }

                if (loser.isOnline()) {
                    Profile profile = Practice.getPlugin().getProfileManager().getProfile(loser.getUniqueId());

                    if (profile != null && profile.getStatus() == ProfileStatus.LOBBY) {
                        loserProfile.giveItems();
                        loser.updateInventory();
                        loser.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
                    }

                    loser.setFlying(false);
                    loser.setAllowFlight(false);
                    Practice.getPlugin().getEntityHider().hideAllPlayers(loser);
                }
            }
        }.runTaskLater(Practice.getPlugin(), 20L * 5);
    }

    private void startSpectating(Player player) {
        player.setAllowFlight(true);

        new BukkitRunnable() {
            public void run() {
                player.setFlying(true);
            }
        }.runTaskLater(Practice.getPlugin(), 10L);
    }

    private void cleanSpectators() {
        Iterator<UUID> specIterator = spectators.iterator();

        while(specIterator.hasNext()) {
            Player p = Bukkit.getPlayer(specIterator.next());

            if (p != null) {
                Practice.getPlugin().getSpectatorManager().stopSpectating(p, false);
            }

            specIterator.remove();
        }
    }

    public static void resetPlayer(Player player) {
        player.setCanPickupItems(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(1);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    public int getNewRating(int rating, int opponentRating, double score) {
        double kFactor = 32;
        double expectedScore = getExpectedScore(rating, opponentRating);
        return calculateNewRating(rating, score, expectedScore, kFactor);
    }

    private int calculateNewRating(int oldRating, double score, double expectedScore, double kFactor) {
        return oldRating + (int) (kFactor * (score - expectedScore));
    }

    private double getExpectedScore(int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, ((double) (opponentRating - rating) / 400.0)));
    }
}