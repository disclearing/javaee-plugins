package me.redis.queue.proxy.profile.rank;

import lombok.Getter;
import me.redis.queue.proxy.QueueProxy;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RankManager {
    private List<Rank> ranks = new ArrayList<>();

    public RankManager() {
        Configuration section = QueueProxy.getPlugin().getConfig().getSection("ranks");

        for (String rank : section.getKeys()) {
            String path = "ranks." + rank + ".";

            ranks.add(new Rank(rank, QueueProxy.getPlugin().getConfig().getString(path + "node"), QueueProxy.getPlugin().getConfig().getInt(path + "priority")));
        }
    }

    public Rank getRank(String name) {
        return QueueProxy.getPlugin().getRankManager().getRanks().stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }
}
