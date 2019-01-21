package me.redis.practice.arena;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import me.redis.practice.Practice;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.regex.Pattern;

public class ArenaManager {
    @Getter private Map<String, Arena> arenas = new HashMap<>();

    public List<Arena> getArenasFromDatabase() {
        List<Arena> arenasNoCache = new ArrayList<>();

        for (Object object : Practice.getPlugin().getArenasCollection().find()) {
            Document document = (Document) object;

            if (document != null) {
                arenasNoCache.add(new Arena(document.getString("name")));
            }
        }

        return arenasNoCache;
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public Arena getRandomArena() {
        List<Arena> randomArena = new ArrayList<>();

        arenas.values().forEach(arena -> {
            if (!arena.getName().toLowerCase().contains("sumo")) randomArena.add(arena);
        });

        if (randomArena.isEmpty()) return null;

        Random random = new Random();
        return (Arena) randomArena.toArray()[random.nextInt(randomArena.size())];
    }

    public Arena getRandomSumoArena() {
        List<Arena> sumoArenas = new ArrayList<>();

        arenas.values().forEach(arena -> {
            if (arena.getName().toLowerCase().contains("sumo")) sumoArenas.add(arena);
        });

        if (sumoArenas.isEmpty()) return null;

        Random random = new Random();
        return (Arena) sumoArenas.toArray()[random.nextInt(sumoArenas.size())];
    }

    public Arena getRandomSpleefArena() {
        List<Arena> spleefArenas = new ArrayList<>();

        arenas.values().forEach(arena -> {
            if (arena.getName().toLowerCase().contains("spleef")) spleefArenas.add(arena);
        });

        if (spleefArenas.isEmpty()) return null;

        Random random = new Random();
        return (Arena) spleefArenas.toArray()[random.nextInt(spleefArenas.size())];
    }
}
