package me.redis.practice.enums;

public enum ProfileStatus {
    LOBBY("Lobby"),
    QUEUE("Queueing"),
    MATCH("Match"),
    SPECTATOR("Spectating"),
    EDITING("Editing");


    private String toString;

    ProfileStatus(String toString) {
        this.toString = toString;
    }

    @Override
    public String toString() {
        return this.toString;
    }
}
