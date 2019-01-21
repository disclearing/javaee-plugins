package me.redis.practice.tournament;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TournamentManager {
    @Getter private List<Tournament> tournaments = new ArrayList<>();
}
