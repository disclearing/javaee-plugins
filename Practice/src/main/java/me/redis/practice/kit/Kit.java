package me.redis.practice.kit;

import lombok.Getter;
import lombok.Setter;
import me.redis.practice.ladders.Ladder;

@Getter
public class Kit {
    @Setter private String name;
    @Setter private String inventory;
    private Ladder ladder;
    private int number;

    public Kit(String name, Ladder ladder, int number) {
        this.name = name;
        this.ladder = ladder;
        this.number = number;
    }
}
