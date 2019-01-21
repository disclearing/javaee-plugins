package me.redis.practice.arena;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.redis.practice.Practice;
import me.redis.practice.utils.LocationUtils;
import me.redis.practice.utils.SerializationUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Getter
public class Arena {
    private String name;
    @Setter private String pos1;
    @Setter private String pos2;

    private List<String> authors = new ArrayList<>();

    @Setter private String ffaLocation1;
    @Setter private String ffaLocation2;

    @Setter private String firstCorner;
    @Setter private String secondCorner;

    public Arena(String name) {
        this.name = name;

        load();
    }

    public void load() {
        Document document = (Document) Practice.getPlugin().getArenasCollection().find(Filters.eq("name", name)).first();

        if (document == null) return;

        name = document.getString("name");
        pos1 = document.getString("pos1");
        pos2 = document.getString("pos2");
        if (document.getString("ffaLocation1") != null) {
            ffaLocation1 = document.getString("ffaLocation1");
        }

        if (document.getString("ffaLocation2") != null) {
            ffaLocation2 = document.getString("ffaLocation2");
        }

        if (document.getString("firstCorner") != null) {
            firstCorner = document.getString("firstCorner");
        }

        if (document.getString("secondCorner") != null) {
            secondCorner = document.getString("secondCorner");
        }

        authors = (List<String>) document.get("authors");
    }

    public void save() {
        Document document = new Document("_id", name);

        document.put("name", name);
        document.put("pos1", pos1);
        document.put("pos2", pos2);
        document.put("ffaLocation1", ffaLocation1);
        document.put("ffaLocation2", ffaLocation2);
        document.put("firstCorner", firstCorner);
        document.put("secondCorner", secondCorner);
        document.put("authors", authors);

        Bson filter = Filters.eq("name", name);
        FindIterable iterable = Practice.getPlugin().getArenasCollection().find(filter);

        if (iterable.first() == null) {
            Practice.getPlugin().getArenasCollection().insertOne(document);
        } else {
            Practice.getPlugin().getArenasCollection().replaceOne(filter, document);
        }

        check();
    }

    public void remove() {
        Document document = (Document) Practice.getPlugin().getArenasCollection().find(Filters.eq("name", name)).first();

        if (document == null) return;

        Practice.getPlugin().getArenasCollection().deleteOne(document);
    }

    public boolean isUsable() {
        return name != null && pos1 != null && pos2 != null && ffaLocation1 != null && ffaLocation2 != null;
    }

    public void check() {
        Practice.getPlugin().getArenaManager().getArenas().put(name, this);
    }

    public Location getPos1() {
        return LocationUtils.getLocation(pos1);
    }

    public Location getPos2() {
        return LocationUtils.getLocation(pos2);
    }
}
