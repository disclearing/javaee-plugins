package me.redis.practice.team;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamManager {
    @Getter private Map<UUID, Team> parties;

    public TeamManager() {
        this.parties = new HashMap<>();
    }

    public Team getTeam(UUID identifier) {
        if (this.parties.containsKey(identifier)) {
            return this.parties.get(identifier);
        }

        return null;
    }

    public void addTeam(Team Team) {
        this.parties.put(Team.getUniqueId(), Team);
    }

    public void removeTeam(Team Team) {
        this.parties.remove(Team.getUniqueId());
    }

    public int getTeamInvAmount() {
        if (parties.size() > 9) return 9;
        if (parties.size() > 18) return 18;
        if (parties.size() > 27) return 27;
        if (parties.size() > 36) return 36;
        if (parties.size() > 45) return 45;
        return 54;
    }
}