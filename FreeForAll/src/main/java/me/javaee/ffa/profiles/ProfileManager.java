package me.javaee.ffa.profiles;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.utils.SerializationUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nullable;
import javax.print.Doc;
import java.util.*;

public class ProfileManager implements Listener {
    public ProfileManager() {
        Bukkit.getPluginManager().registerEvents(this, FFA.getPlugin());
    }

    @Getter
    private Map<UUID, Profile> profiles = new HashMap<>();

    public Profile getProfile(UUID uniqueId) {
        return profiles.get(uniqueId);
    }

    public List<Profile> getNotCachedProfiles() {
        List<Profile> toReturn = new ArrayList<>();

        for (Object object : FFA.getPlugin().getProfilesCollection().find()) {
            Document document = (Document) object;

            toReturn.add(new Profile((UUID) document.get("_id")));
        }

        return toReturn;
    }

    public Map<UUID, Object> getSortedValues(String field, Bson order, @Nullable Bson filter, int limit) {
        Map<UUID, Object> result = new LinkedHashMap<>(limit);

        FindIterable<Document> iterable = FFA.getPlugin().getProfilesCollection().find(Filters.exists(field)).sort(order).limit(limit);

        if (filter != null) {
            iterable.filter(filter);
        }

        for (Document document : iterable) {
            result.put((UUID) document.get("_id"), document.get(field));
        }

        return result;
    }

    public Profile getNotCachedProfile(UUID uniqueId) {
        for (Object object : FFA.getPlugin().getProfilesCollection().find()) {
            Document document = (Document) object;

            if (document.get("_id").equals(uniqueId)) {
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

        profile.save();
        FFA.getPlugin().getProfileManager().getProfiles().put(event.getUniqueId(), profile);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(FFA.getPlugin(), () -> getProfile(event.getPlayer().getUniqueId()).save());
    }
}
