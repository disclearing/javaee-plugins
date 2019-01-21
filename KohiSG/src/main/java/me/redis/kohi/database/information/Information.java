package me.redis.kohi.database.information;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.redis.kohi.SurvivalGames;
import org.bson.Document;
import org.bson.conversions.Bson;

@Getter
public class Information {
    @Setter private String serverName;
    @Setter private int minPlayers = 5;
    @Setter private boolean bars = false;
    @Setter private int exceptionsHandled;
    @Setter private String downloadUrl;

    public Information() {
        load();
    }


    public void load() {
        Document document = (Document) SurvivalGames.getPlugin().getInformationCollection().find(Filters.eq("_id", "Information")).first();

        if (document == null) return;

        serverName = document.getString("serverName");
        minPlayers = document.getInteger("minPlayers");
        bars = document.getBoolean("bars");

        if (document.containsKey("downloadUrl")) {
            downloadUrl = document.getString("downloadUrl");
        }

        if (document.containsKey("exceptionsHandled")) {
            exceptionsHandled = document.getInteger("exceptionsHandled");
        }
    }

    public void save() {
        Document document = new Document("_id", "Information");

        document.put("serverName", serverName);
        document.put("minPlayers", minPlayers);
        document.put("bars", bars);
        document.put("downloadUrl", downloadUrl);
        document.put("exceptionsHandled", exceptionsHandled);

        Bson filter = Filters.eq("_id", "Information");
        FindIterable iterable = SurvivalGames.getPlugin().getInformationCollection().find(filter);

        if (iterable.first() == null) {
            SurvivalGames.getPlugin().getInformationCollection().insertOne(document);
        } else {
            SurvivalGames.getPlugin().getInformationCollection().replaceOne(filter, document);
        }

        SurvivalGames.getPlugin().getPlugin().getInformationManager().setInformation(this);
    }
}
