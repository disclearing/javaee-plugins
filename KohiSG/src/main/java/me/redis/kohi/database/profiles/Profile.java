package me.redis.kohi.database.profiles;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.redis.kohi.SurvivalGames;
import me.redis.kohi.database.profiles.status.PlayerStatus;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

@Getter
public class Profile {
    private UUID uniqueId;

    @Setter private String name, killedInventory, killedLocation;
    @Setter private int kills, deaths, wins, played, matchKills;
    @Setter private PlayerStatus playerStatus = PlayerStatus.SPECTATING;

    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;

        load();
    }

    public void load() {
        Document document = (Document) SurvivalGames.getPlugin().getProfilesCollection().find(Filters.eq("_id", uniqueId)).first();

        if (document == null) return;

        name = document.getString("name");
        kills = document.getInteger("kills");
        deaths = document.getInteger("deaths");
        wins = document.getInteger("wins");
        played = document.getInteger("played");
    }

    public void save() {
        Document document = new Document("_id", uniqueId); 

		// The "_id", uniqueId means that the id of the document will be the uuid of the player...
        // You first initialize a new document.

        document.put("name", name);
        document.put("kills", kills);
        document.put("deaths", deaths);
        document.put("wins", wins);
        document.put("played", played);

        // Now you put the values into the document. (each field has a value)

        Bson filter = Filters.eq("_id", uniqueId);
        FindIterable iterable = SurvivalGames.getPlugin().getProfilesCollection().find(filter);

        // You now search for documents with id == player#getUniqueId(), for that we use the find() feature

        // If no document was found with that id, we insert a new one..
        if (iterable.first() == null) {
            SurvivalGames.getPlugin().getProfilesCollection().insertOne(document);
        } else {
        	// Else, if the document is found, we replace it.
            SurvivalGames.getPlugin().getProfilesCollection().replaceOne(filter, document);
        }

        //And, done!
    }

    public void addWin() {
        wins++;
    }
}
