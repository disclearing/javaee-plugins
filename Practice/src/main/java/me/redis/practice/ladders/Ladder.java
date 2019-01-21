package me.redis.practice.ladders;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.redis.practice.Practice;
import me.redis.practice.queue.IQueue;
import me.redis.practice.queue.type.SoloQueue;
import me.redis.practice.utils.ItemBuilder;
import me.redis.practice.utils.SerializationUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class Ladder {
    private String name;
    @Setter private String icon = "STRING";
    @Setter private String defaultInventory;
    @Setter private boolean regainHealth = true;
    @Setter private boolean ranked = false;
    @Setter private Integer position = 0;

    public Ladder(String name) {
        this.name = name;

        load();
    }

    public void load() {
        Document document = (Document) Practice.getPlugin().getLaddersCollection().find(Filters.eq("name", name)).first();

        if (document == null) return;

        name = document.getString("name");
        icon = document.getString("icon");
        defaultInventory = document.getString("defaultInventory");
        regainHealth = document.getBoolean("regainHealth");
        ranked = document.getBoolean("ranked");
        position = document.getInteger("position");
    }

    public void save() {
        Document document = new Document("_id", name);

        document.put("name", name);
        document.put("icon", icon);
        document.put("defaultInventory", defaultInventory);
        document.put("regainHealth", regainHealth);
        document.put("ranked", ranked);
        document.put("position", position);

        Bson filter = Filters.eq("name", name);
        FindIterable iterable = Practice.getPlugin().getLaddersCollection().find(filter);

        if (iterable.first() == null) {
            Practice.getPlugin().getLaddersCollection().insertOne(document);
        } else {
            Practice.getPlugin().getLaddersCollection().replaceOne(filter, document);
        }

        check();
    }

    public void remove() {
        Document document = (Document) Practice.getPlugin().getLaddersCollection().find(Filters.eq("name", name)).first();

        if (document == null) return;

        Practice.getPlugin().getLaddersCollection().deleteOne(document);
    }

    public boolean isUsable() {
        return name != null && defaultInventory != null;
    }

    public void check() {
        Practice.getPlugin().getLadderManager().getLadders().put(name, this);

        for (IQueue queue : Practice.getPlugin().getQueueManager().getQueues().values()) {
            if (queue.getLadder() == this) {
                return;
            }
        }

        if (!isUsable()) return;

        if (isRanked()) {
            SoloQueue rankedQueue = new SoloQueue(this, true);
            Practice.getPlugin().getQueueManager().getQueues().put(rankedQueue.getIdentifier(), rankedQueue);

            SoloQueue unrankedQueue = new SoloQueue(this, false);
            Practice.getPlugin().getQueueManager().getQueues().put(unrankedQueue.getIdentifier(), unrankedQueue);
        } else {
            SoloQueue unrankedQueue = new SoloQueue(this, false);
            Practice.getPlugin().getQueueManager().getQueues().put(unrankedQueue.getIdentifier(), unrankedQueue);
        }
    }
}
