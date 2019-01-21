package me.javaee.uhc.database.profile;

import com.google.common.collect.Maps;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.utils.DatabaseUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import javax.print.Doc;
import java.util.*;

@Getter public class ProfileUtils implements Listener {

    @Getter private static ProfileUtils instance = new ProfileUtils();
    private Map<UUID, Profile> profiles;

    private MongoCollection profileCollection;
    private MongoCollection kitsCollection;

    private ProfileUtils() {
    }

    public void setup() {
        this.profiles = Maps.newHashMap();
        this.profileCollection = DatabaseUtils.getInstance().getDatabase().getCollection(UHC.getInstance().getConfig().getString("DATABASES.MONGO.COLLECTIONS.PROFILES"));

        /*this.kits = Maps.newHashMap();*/
        this.kitsCollection = DatabaseUtils.getInstance().getDatabase().getCollection("kits");
        Bukkit.getPluginManager().registerEvents(this, UHC.getInstance());
    }

    public Map<UUID, Object> getSortedValues(String field, Bson order, @Nullable Bson filter, int limit) {
        Map<UUID, Object> result = new LinkedHashMap<>(limit);

        FindIterable<Document> iterable = profileCollection.find(Filters.exists(field)).sort(order).limit(limit);

        if (filter != null) {
            iterable.filter(filter);
        }

        for (Document document : iterable) {
            result.put(UUID.fromString(document.getString("uuid")), document.get(field));
        }

        return result;
    }

    public List<Profile> getAllProfiles() {
        List<Profile> list = new ArrayList<>();

        for (Object object : getProfileCollection().find()) {
            Document document = (Document) object;

            if (document != null) {
                list.add(new Profile(UUID.fromString(document.getString("uuid"))));
            }
        }

        return list;
    }

    public Profile getProfile(UUID uuid) {
        Profile profile = this.profiles.get(uuid);

        if (profile == null) {
            profile = new Profile(uuid);
            profiles.put(uuid, profile);
        }

        return profile;
    }

    public void saveProfile(Player p) {
        Profile profile = this.profiles.get(p.getUniqueId());
        if (profile != null) {
            profile.save(true);
        }
        this.profiles.remove(p.getUniqueId());
    }

    public void saveProfiles() {
        this.profiles.values().forEach(p -> p.save(false));
        this.profiles.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncJoinEvent(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            getProfile(event.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.saveProfile(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        this.saveProfile(e.getPlayer());
    }

}