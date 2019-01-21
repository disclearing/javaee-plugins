package me.javaee.meetup.profile;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import me.javaee.meetup.Meetup;
import me.javaee.meetup.utils.DatabaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

@Getter public class ProfileUtils implements Listener {

    @Getter private static ProfileUtils instance = new ProfileUtils();
    private Map<UUID, Profile> profiles;

    private MongoCollection profileCollection;

    private ProfileUtils() {
    }

    public void setup() {
        this.profiles = Maps.newHashMap();
        this.profileCollection = DatabaseUtils.getInstance().getDatabase().getCollection("profiles");

        /*this.kits = Maps.newHashMap();*/
        Bukkit.getPluginManager().registerEvents(this, Meetup.getPlugin());
    }


    public Profile getProfile(UUID uuid) {
        Profile profile = this.profiles.get(uuid);

        if (profile == null) {
            profile = new Profile(uuid);
            profiles.put(uuid, profile);
        }
        return profile;
    }

    public void saveProfile(Player p) {
        Profile profile = this.profiles.get(p.getUniqueId());
        if (profile != null) {
            profile.save(true);
        }
        this.profiles.remove(p.getUniqueId());
    }

    public void saveProfiles() {
        this.profiles.values().forEach(p -> p.save(false));
        this.profiles.clear();
    }

    public int getNewRating(Player player, int opponentRating, double score) {
        double kFactor = 32;
        double expectedScore = getExpectedScore(getProfile(player.getUniqueId()).getElo(), opponentRating);
        return calculateNewRating(getProfile(player.getUniqueId()).getElo(), score, expectedScore, kFactor);
    }

    private int calculateNewRating(int oldRating, double score, double expectedScore, double kFactor) {
        return oldRating + (int) (kFactor * (score - expectedScore));
    }

    private double getExpectedScore (int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, ((double) (opponentRating - rating) / 400.0)));
    }

    public ChatColor calculateColor(Player player) {
        int elo = ProfileUtils.getInstance().getProfile(player.getUniqueId()).getElo();

        if (elo < 1000) {
            return ChatColor.DARK_GRAY;
        } else if (elo > 999 && elo < 1200) {
            return ChatColor.GRAY;
        } else if (elo > 1199 && elo < 1400) {
            return ChatColor.GOLD;
        } else if (elo > 1399 && elo < 1600) {
            return ChatColor.GREEN;
        } else if (elo > 1599 && elo < 1800) {
            return ChatColor.AQUA;
        } else {
            return ChatColor.DARK_PURPLE;
        }
    }

    public String calculateRank(Player player) {
        int elo = ProfileUtils.getInstance().getProfile(player.getUniqueId()).getElo();

        if (elo < 1000) {
            return "Bronze";
        } else if (elo > 999 && elo < 1200) {
            return "Silver";
        } else if (elo > 1199 && elo < 1400) {
            return "Gold";
        } else if (elo > 1399 && elo < 1600) {
            return "Emerald";
        } else if (elo > 1599 && elo < 1800) {
            return "Diamond";
        } else {
            return "Masters";
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.saveProfile(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        this.saveProfile(e.getPlayer());
    }

}