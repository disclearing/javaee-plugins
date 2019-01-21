package me.javaee.meetup.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

@Getter
public class DatabaseUtils {

    @Getter
    private static DatabaseUtils instance = new DatabaseUtils();
    private MongoClient client;
    private MongoDatabase database;

    private DatabaseUtils() {
    }

    public void setup() {
        String uri = "mongodb://127.0.0.1";
        String name = "Meetup";

        this.client = new MongoClient(new MongoClientURI(uri));
        this.database = this.client.getDatabase(name);
    }

}