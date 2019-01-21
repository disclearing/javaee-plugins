package me.redis.practice.queue;

import lombok.Getter;
import me.redis.practice.Practice;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.match.IMatch;
import me.redis.practice.queue.type.SoloQueue;

import java.util.*;

public class QueueManager {
    @Getter private Map<UUID, IQueue> queues = new HashMap<>();

    public QueueManager() {
        initiateQueues();
    }

    private void initiateQueues() {
        for (Ladder ladder : Practice.getPlugin().getLadderManager().getLaddersFromDatabase()) {
            if (ladder.isRanked()) {
                SoloQueue rankedQueue = new SoloQueue(ladder, true);
                queues.put(rankedQueue.getIdentifier(), rankedQueue);

                SoloQueue unrankedQueue = new SoloQueue(ladder, false);
                queues.put(unrankedQueue.getIdentifier(), unrankedQueue);
            } else {
                SoloQueue unrankedQueue = new SoloQueue(ladder, false);
                queues.put(unrankedQueue.getIdentifier(), unrankedQueue);
            }
        }
    }

    public void restartQueues() {
        for (IMatch match : Practice.getPlugin().getMatchManager().getMatches().values()) {
            match.cancelMatch("Restarting queues...");
        }
    }
}