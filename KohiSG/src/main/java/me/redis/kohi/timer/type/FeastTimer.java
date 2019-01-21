package me.redis.kohi.timer.type;

import me.redis.kohi.SurvivalGames;
import me.redis.kohi.timer.GlobalTimer;

import java.util.concurrent.TimeUnit;

public class FeastTimer extends GlobalTimer {
    private final SurvivalGames plugin;

    public FeastTimer(SurvivalGames plugin) {
        super("Feast", TimeUnit.MINUTES.toMillis(7));

        this.plugin = plugin;
    }

    @Override
    public String getScoreboardPrefix() {
        return "Feast";
    }
}
