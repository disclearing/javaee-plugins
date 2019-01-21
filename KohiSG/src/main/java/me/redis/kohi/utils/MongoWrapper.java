package me.redis.kohi.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.redis.kohi.SurvivalGames;
import me.redis.kohi.database.information.InformationManager;
import me.redis.kohi.database.profiles.ProfileManager;
import org.bukkit.Bukkit;

@Getter
public class MongoWrapper {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private MongoCollection informationCollection;
    private InformationManager informationManager;

    private MongoCollection profilesCollection;
    private ProfileManager profileManager;

    public MongoWrapper() {
        mongoClient = new MongoClient(SurvivalGames.getPlugin().getConfig().getString("MONGODB.CLIENT.ADDRESS"), SurvivalGames.getPlugin().getConfig().getInt("MONGODB.CLIENT.PORT"));
        mongoDatabase = mongoClient.getDatabase(SurvivalGames.getPlugin().getConfig().getString("MONGODB.DATABASE.NAME"));

        informationCollection = mongoDatabase.getCollection(SurvivalGames.getPlugin().getConfig().getString("MONGODB.COLLECTIONS.INFORMATION"));
        informationManager = new InformationManager();

        profilesCollection = mongoDatabase.getCollection(SurvivalGames.getPlugin().getConfig().getString("MONGODB.COLLECTIONS.PROFILES"));
        profileManager = new ProfileManager();
    }
}
