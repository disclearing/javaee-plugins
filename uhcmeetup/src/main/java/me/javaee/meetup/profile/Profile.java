package me.javaee.meetup.profile;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.javaee.meetup.Meetup;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@Getter
@Setter
public class Profile {
    private String ipAddress;
    private UUID uuid;
    private String lastName;
    private int kills;
    private int deaths;
    private int matchKills;
    private int winnedGames;
    private int totalGames;
    private int goldenApples;
    private int goldenHeads;
    private int rerolls = 1;
    private int elo = 1000;

    public Profile(UUID uuid) {
        this.uuid = uuid;

        Player player = Bukkit.getPlayer(this.uuid);
        if (player != null) {
            this.ipAddress = player.getAddress().getAddress().getHostAddress();
            this.lastName = player.getName();
        }

        this.load();
    }

    private void load() {
        MongoCollection profiles = ProfileUtils.getInstance().getProfileCollection();
        FindIterable<Document> it = profiles.find(Filters.eq("uuid", this.uuid.toString()));

        Meetup.getPlugin().getLogger().info("Loading profile data for " + this.uuid.toString());

        Document document = it.first();

        if (document == null) {
            Meetup.getPlugin().getLogger().info("Profile created for " + this.uuid.toString());
        } else {
            this.ipAddress = document.getString("ipAddress");
            this.lastName = document.getString("lastName");

            this.kills = document.getInteger("kills");
            this.deaths = document.getInteger("deaths");

            this.winnedGames = document.getInteger("winnedGames");
            this.totalGames = document.getInteger("totalGames");
            this.rerolls = document.getInteger("rerolls");

            this.elo = document.getInteger("elo");

            Meetup.getPlugin().getLogger().info("Profile data loaded for " + this.uuid.toString());
        }
    }

    public void save(boolean async) {
        if (!async) {
            MongoCollection profiles = ProfileUtils.getInstance().getProfileCollection();

            Document document = new Document();

            document.append("uuid", uuid.toString())
                    .append("ipAddress", ipAddress)
                    .append("lastName", lastName)
                    .append("kills", kills)
                    .append("deaths", deaths)
                    .append("winnedGames", winnedGames)
                    .append("totalGames", totalGames)
                    .append("goldenApples", goldenApples)
                    .append("goldenHeads", goldenHeads)
                    .append("elo", elo)
                    .append("rerolls", rerolls);

            Bson filter = Filters.eq("uuid", uuid.toString());

            FindIterable<Document> it = profiles.find(filter);

            if (it.first() == null) {
                profiles.insertOne(document);
                Meetup.getPlugin().getLogger().info("Saving data for " + uuid.toString());
            } else {
                Meetup.getPlugin().getLogger().info("Updating data for " + uuid.toString());
                profiles.replaceOne(filter, document);
            }
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    MongoCollection profiles = ProfileUtils.getInstance().getProfileCollection();
                    Document document = new Document();

                    document.append("uuid", uuid.toString())
                            .append("ipAddress", ipAddress)
                            .append("lastName", lastName)
                            .append("kills", kills)
                            .append("deaths", deaths)
                            .append("winnedGames", winnedGames)
                            .append("totalGames", totalGames)
                            .append("goldenApples", goldenApples)
                            .append("goldenHeads", goldenHeads)
                            .append("elo", elo)
                    .append("rerolls", rerolls);

                    Bson filter = Filters.eq("uuid", uuid.toString());

                    FindIterable<Document> it = profiles.find(filter);

                    if (it.first() == null) {
                        profiles.insertOne(document);
                        Meetup.getPlugin().getLogger().info("Saving data for " + uuid.toString());
                    } else {
                        Meetup.getPlugin().getLogger().info("Updating data for " + uuid.toString());
                        profiles.replaceOne(filter, document);
                    }
                }
            }.runTaskAsynchronously(Meetup.getPlugin());
        }
    }
}