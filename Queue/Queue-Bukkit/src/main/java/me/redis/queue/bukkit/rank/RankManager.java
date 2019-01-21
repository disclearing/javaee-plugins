package me.redis.queue.bukkit.rank;

import lombok.Getter;
import me.redis.queue.bukkit.QueueBukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RankManager {
    private List<Rank> ranks = new ArrayList<>();

    public RankManager() {
        ConfigurationSection section = QueueBukkit.getPlugin().getConfig().getConfigurationSection("ranks");

        for (String rank : section.getKeys(false)) {
            String path = "ranks." + rank + ".";

            ranks.add(new Rank(rank, QueueBukkit.getPlugin().getConfig().getString(path + "node"), QueueBukkit.getPlugin().getConfig().getInt(path + "priority")));
        }
    }

    public Rank getRank(String permission) {
        return QueueBukkit.getPlugin().getRankManager().getRanks().stream().filter(rank -> rank.getPermission().equalsIgnoreCase(permission)).findAny().orElse(null);
    }

    public Rank getRank(Player player) {
        Rank playerRank = null;

        for (Rank rank : ranks) {
            if (player.hasPermission(rank.getPermission())) {
                if (playerRank == null) {
                    playerRank = rank;
                } else {
                    if (rank.getPriority() >= playerRank.getPriority()) {
                        continue;
                    }

                    playerRank = rank;
                }
            }
        }

        return playerRank;
    }
}
