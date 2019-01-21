package me.redis.practice.profile;

import lombok.Getter;
import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager implements Listener {
    public ProfileManager() {
        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
    }

    @Getter
    private Map<UUID, Profile> profiles = new HashMap<>();

    public Profile getProfile(UUID uniqueId) {
        return profiles.get(uniqueId);
    }

    public Profile getNotCachedProfile(UUID uniqueId) {
        for (Object object : Practice.getPlugin().getProfilesCollection().find()) {
            Document document = (Document) object;

            if (document.get("uniqueId").equals(uniqueId)) {
                return new Profile(uniqueId);
            }
        }

        return null;
    }

    public Profile getProfile(Player player) {
        return getProfile(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        Profile profile = new Profile(event.getUniqueId());

        if (profile.getName() == null) {
            profile.setName(event.getName());
        }

        Practice.getPlugin().getLadderManager().getLadders().values().forEach(ladder -> {
            profile.getElo().putIfAbsent(ladder.getName(), 1000);
        });

        profile.save();
        profile.setStatus(ProfileStatus.LOBBY);

        Practice.getPlugin().getProfileManager().getProfiles().put(event.getUniqueId(), profile);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Practice.getPlugin(), () -> getProfile(event.getPlayer().getUniqueId()).save());
    }
}
