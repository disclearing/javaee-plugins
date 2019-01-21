package me.javaee.ffa.profiles;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.javaee.ffa.FFA;
import me.javaee.ffa.profiles.status.PlayerStatus;
import me.javaee.ffa.protocol.ClaimPillar;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class Profile {
    private UUID uniqueId;
    @Setter private String name;
    @Setter private int kills, deaths, killstreak;
    @Setter private int elo = 1000;
    @Setter private String kit;
    @Setter private ClaimPillar firstPillar;
    @Setter private ClaimPillar secondPillar;
    @Setter private PlayerStatus playerStatus = PlayerStatus.PLAYING;
    @Setter private boolean vanished;

    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;

        load();
    }

    public void load() {
        Document document = (Document) FFA.getPlugin().getProfilesCollection().find(Filters.eq("_id", uniqueId)).first();

        if (document == null) return;

        name = document.getString("name");
        kills = document.getInteger("kills");
        deaths = document.getInteger("deaths");
        kit = document.getString("kit");

        if (document.containsKey("elo")) {
            elo = document.getInteger("elo");
        }
    }

    public void save() {
        Document document = new Document("_id", uniqueId);

        document.put("name", name);
        document.put("kills", kills);
        document.put("deaths", deaths);
        document.put("kit", kit);
        document.put("elo", elo);

        Bson filter = Filters.eq("_id", uniqueId);
        FindIterable iterable = FFA.getPlugin().getProfilesCollection().find(filter);

        if (iterable.first() == null) {
            FFA.getPlugin().getProfilesCollection().insertOne(document);
        } else {
            FFA.getPlugin().getProfilesCollection().replaceOne(filter, document);
        }
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

    public ChatColor calculateColor() {
        if (elo < 1000) {
            return ChatColor.DARK_GRAY;
        } else if (elo < 1200) {
            return ChatColor.GRAY;
        } else if (elo < 1400) {
            return ChatColor.GOLD;
        } else if (elo < 1600) {
            return ChatColor.GREEN;
        } else if (elo < 1800) {
            return ChatColor.AQUA;
        } else {
            return ChatColor.DARK_PURPLE;
        }
    }
}
