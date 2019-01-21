package me.redis.practice.ladders;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static com.mongodb.client.model.Sorts.descending;

public class LadderManager {
    @Getter private Map<String, Ladder> ladders = new LinkedHashMap<>();

    public Ladder getLadder(String name) {
        return ladders.get(name);
    }

    public List<Ladder> getLaddersFromDatabase() {
        List<Ladder> laddersNoCache = new ArrayList<>();

        for (Object object : Practice.getPlugin().getLaddersCollection().find()) {
            Document document = (Document) object;

            if (document != null) {
                laddersNoCache.add(new Ladder(document.getString("name")));
            }
        }

        return laddersNoCache;
    }
}
