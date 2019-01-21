package me.javaee.uhc.database.profile;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.utils.MojangNameFetcher;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Profile {
    private String ipAddress;
    private UUID uuid;
    private String lastName;
    private boolean lateScattered = false;
    private int kills;
    private int deaths;
    private int matchKills;
    private int winnedGames;
    private int totalGames;
    private int goldenApples;
    private int goldenHeads;
    private int diamondsMined;
    private int goldMined;
    private int ironMined;
    private int redstoneMined;
    private int lapisMined;
    private int coalMined;
    private int matchDiamondsMined;
    private int matchGoldMined;
    private int matchIronMined;
    private int matchRedstoneMined;
    private int matchLapisMined;
    private int matchCoalMined;
    public boolean dead;
    public long aFKTimeLeft;
    private BukkitTask offlineTask;
    private boolean alerts = true;
    private boolean vanish = true;
    private boolean spectators = true;
    private boolean staffChat = false;
    private boolean teamChat = false;
    private int gamesHosted = 0;

    private String deathInventory = "null";
    private String deathArmor = "null";
    private String deathLocation = "null";

    public Profile(UUID uuid) {
        this.uuid = uuid;

        this.load();
    }

    private void load() {
        MongoCollection profiles = ProfileUtils.getInstance().getProfileCollection();
        FindIterable it = profiles.find(Filters.eq("uuid", this.uuid.toString()));
        Document document = (Document) it.first();

        if (document != null) {
            if (document.getString("ipAddress") != null) {
                this.ipAddress = document.getString("ipAddress");
            }

            if (document.getString("lastName") != null) {
                this.lastName = document.getString("lastName");
            }

            if (document.getInteger("kills") != null) {
                this.kills = document.getInteger("kills");
            }

            if (document.getInteger("deaths") != null) {
                this.deaths = document.getInteger("deaths");
            }

            if (document.getInteger("winnedGames") != null) {
                this.winnedGames = document.getInteger("winnedGames");
            }

            if (document.getInteger("totalGames") != null) {
                this.totalGames = document.getInteger("totalGames");
            }

            if (document.getInteger("diamondsMined") != null) {
                this.diamondsMined = document.getInteger("diamondsMined");
            }

            if (document.getInteger("goldMined") != null) {
                this.goldMined = document.getInteger("goldMined");
            }

            if (document.getInteger("redstoneMined") != null) {
                this.redstoneMined = document.getInteger("redstoneMined");
            }

            if (document.getInteger("lapisMined") != null) {
                this.lapisMined = document.getInteger("lapisMined");
            }

            if (document.getInteger("coalMined") != null) {
                this.coalMined = document.getInteger("coalMined");
            }

            if (document.getInteger("ironMined") != null) {
                this.ironMined = document.getInteger("ironMined");
            }

            if (document.getInteger("goldenApples") != null) {
                this.goldenApples = document.getInteger("goldenApples");
            }

            if (document.getInteger("goldenHeads") != null) {
                this.goldenHeads = document.getInteger("goldenHeads");
            }

            if (document.getBoolean("lateScattered") != null) {
                this.lateScattered = document.getBoolean("lateScattered", false);
            }

            if (document.getBoolean("dead") != null) {
                this.dead = document.getBoolean("dead", false);
            }

            if (document.getString("deathInventory") != null) {
                this.deathInventory = document.getString("deathInventory");
            }

            if (document.getString("deathArmor") != null) {
                this.deathArmor = document.getString("deathArmor");
            }

            if (document.getString("deathLocation") != null) {
                this.deathLocation = document.getString("deathLocation");
            }

            if (document.getInteger("matchKills") != null) {
                this.matchKills = document.getInteger("matchKills");
            }

            if (document.getInteger("gamesHosted") != null) {
                this.gamesHosted = document.getInteger("gamesHosted");
            }
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
                    .append("diamondsMined", diamondsMined)
                    .append("goldMined", goldMined)
                    .append("redstoneMined", redstoneMined)
                    .append("lapisMined", lapisMined)
                    .append("coalMined", coalMined)
                    .append("ironMined", ironMined)
                    .append("goldenApples", goldenApples)
                    .append("goldenHeads", goldenHeads)
                    .append("lateScattered", lateScattered)
                    .append("dead", dead);

            document.append("deathInventory", deathInventory);
            document.append("deathArmor", deathArmor);
            document.append("deathLocation", deathLocation);
            document.append("matchKills", matchKills);
            document.append("gamesHosted", gamesHosted);

            Bson filter = Filters.eq("uuid", uuid.toString());

            FindIterable<Document> it = profiles.find(filter);

            if (it.first() == null) {
                profiles.insertOne(document);
                //  UHC.getInstance().getLogger().info("Saving data for " + uuid.toString());
            } else {
                //  UHC.getInstance().getLogger().info("Updating data for " + uuid.toString());
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
                            .append("diamondsMined", diamondsMined)
                            .append("goldMined", goldMined)
                            .append("redstoneMined", redstoneMined)
                            .append("lapisMined", lapisMined)
                            .append("coalMined", coalMined)
                            .append("ironMined", ironMined)
                            .append("goldenApples", goldenApples)
                            .append("goldenHeads", goldenHeads)
                            .append("lateScattered", lateScattered)
                            .append("dead", dead);

                    document.append("deathInventory", deathInventory);
                    document.append("deathArmor", deathArmor);
                    document.append("deathLocation", deathLocation);
                    document.append("matchKills", matchKills);
                    document.append("gamesHosted", gamesHosted);

                    Bson filter = Filters.eq("uuid", uuid.toString());

                    FindIterable<Document> it = profiles.find(filter);

                    if (it.first() == null) {
                        profiles.insertOne(document);
                        // UHC.getInstance().getLogger().info("Saving data for " + uuid.toString());
                    } else {
                        //  UHC.getInstance().getLogger().info("Updating data for " + uuid.toString());
                        profiles.replaceOne(filter, document);
                    }
                }
            }.runTaskAsynchronously(UHC.getInstance());
        }
    }
}