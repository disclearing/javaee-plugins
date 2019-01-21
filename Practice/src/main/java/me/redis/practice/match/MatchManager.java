package me.redis.practice.match;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MatchManager {
    @Getter private Map<UUID, IMatch> matches;

    public MatchManager() {
        this.matches = new HashMap<>();
    }
}