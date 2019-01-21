package me.redis.kohi.game.states;

public enum GameState {
    WAITING("Waiting"),
    PLAYING("Playing"),
    ENDED("Ended");

    private String name;
    GameState(String name) {
        this.name = name;
    }

    public String getFormattedName() {
        return name;
    }

    public String getName() {
        return name();
    }
}
