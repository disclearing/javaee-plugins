package me.javaee.uhc.database.serverinfo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.utils.DatabaseUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
@Getter @Setter public class ServerInfo {
    private String serverName;
    private boolean worldGenerated;
    private String lastMatchName;

    MongoDatabase database = DatabaseUtils.getInstance().getDatabase();
    MongoCollection<Document> collection = database.getCollection("serverInfo");

    public ServerInfo(String serverName) {
        this.serverName = serverName;

        this.load();
    }

    private void load() {
        UHC.getInstance().getLogger().info("We are loading the server info.");

        FindIterable<Document> it = collection.find(Filters.eq("serverName", serverName));
        Document document = it.first();

        if (document == null) {
            UHC.getInstance().getLogger().info("The server info has been created.");
        } else {
            this.serverName = document.getString("serverName");
            this.worldGenerated = document.getBoolean("worldGenerated");

            if (document.containsKey("lastMatchName")) {
                lastMatchName = document.getString("lastMatchName");
            }

            UHC.getInstance().getLogger().info("The server info has been loaded.");
        }
    }

    public void save() {
        Document document = new Document();

        document.append("serverName", serverName);
        document.append("worldGenerated", worldGenerated);
        document.append("lastMatchName", lastMatchName);

        Bson filter = Filters.eq("serverName", serverName);

        FindIterable<Document> it = collection.find(filter);

        if (it.first() == null) {
            collection.insertOne(document);
            UHC.getInstance().getLogger().info("Saving the server info's data.");
        } else {
            UHC.getInstance().getLogger().info("Updating the server info's data.");
            collection.replaceOne(filter, document);
        }
    }
}
