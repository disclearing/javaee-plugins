package me.javaee.uhc.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.javaee.uhc.UHC;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class DatabaseUtils {

    @Getter
    private static DatabaseUtils instance = new DatabaseUtils();
    private MongoClient client;
    private MongoDatabase database;

    private DatabaseUtils() {
    }

    public void setup() {
        String uri = "mongodb://" + UHC.getInstance().getConfig().getString("DATABASES.MONGO.HOST");
        String name = UHC.getInstance().getConfig().getString("DATABASES.MONGO.NAME");

        this.client = new MongoClient(new MongoClientURI(uri));
        this.database = this.client.getDatabase(name);
    }

}