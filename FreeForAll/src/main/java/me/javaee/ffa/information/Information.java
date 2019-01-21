package me.javaee.ffa.information;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.javaee.ffa.FFA;
import me.javaee.ffa.utils.Cuboid;
import me.javaee.ffa.utils.LocationUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Information {
    @Setter private String lobbyLocation, lobbyFirst, lobbySecond, kothFirstLocation, kothSecondLocation;
    @Setter private boolean kothStarted;
    @Setter private int kothSeconds = 600;
    @Setter private int season = 1;
    @Setter private boolean recording = false;
    private List<String> topKillsLocations = new ArrayList<>();
    private List<String> announces = new ArrayList<>();

    public Information() {
        load();
    }


    public void load() {
        Document document = (Document) FFA.getPlugin().getInformationCollection().find(Filters.eq("_id", "Information")).first();

        if (document == null) return;

        lobbyLocation = document.getString("lobbyLocation");
        lobbyFirst = document.getString("lobbyFirst");
        lobbySecond = document.getString("lobbySecond");
        kothFirstLocation = document.getString("kothFirstLocation");
        kothSecondLocation = document.getString("kothSecondLocation");
        kothSeconds = document.getInteger("kothSeconds");
        season = document.getInteger("season");
        if (document.containsKey("topKillsLocations")) {
            topKillsLocations = (List<String>) document.get("topKillsLocations");
        }

        if (document.containsKey("announces")) {
            announces = (List<String>) document.get("announces");
        }
    }

    public void save() {
        Document document = new Document("_id", "Information");

        document.put("lobbyLocation", lobbyLocation);
        document.put("lobbyFirst", lobbyFirst);
        document.put("lobbySecond", lobbySecond);
        document.put("kothFirstLocation", kothFirstLocation);
        document.put("kothSecondLocation", kothSecondLocation);
        document.put("kothSeconds", kothSeconds);
        document.put("season", season);
        document.put("topKillsLocations", topKillsLocations);
        document.put("announces", announces);

        Bson filter = Filters.eq("_id", "Information");
        FindIterable iterable = FFA.getPlugin().getInformationCollection().find(filter);

        if (iterable.first() == null) {
            FFA.getPlugin().getInformationCollection().insertOne(document);
        } else {
            FFA.getPlugin().getInformationCollection().replaceOne(filter, document);
        }

        FFA.getPlugin().getInformationManager().setInformation(this);
    }

    public Cuboid getKothCuboid() {
        return new Cuboid(LocationUtils.getLocation(kothFirstLocation), LocationUtils.getLocation(kothSecondLocation));
    }

    public Cuboid getLobbyCuboid() {
        return new Cuboid(LocationUtils.getLocation(lobbyFirst), LocationUtils.getLocation(lobbySecond));
    }

    public Location getKothCenter() {
        return getKothCuboid().getCenter();
    }
}
