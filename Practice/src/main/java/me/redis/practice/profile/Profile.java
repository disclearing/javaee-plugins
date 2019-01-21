package me.redis.practice.profile;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import me.redis.practice.Practice;
import me.redis.practice.duel.DuelRequest;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.kit.Kit;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.match.IMatch;
import me.redis.practice.queue.IQueue;
import me.redis.practice.queue.QueueData;
import me.redis.practice.team.Team;
import me.redis.practice.utils.ItemBuilder;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.print.Doc;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

@Getter
public class Profile {
    private UUID uniqueId;
    @Setter private String name;

    private Map<String, Integer> elo = new HashMap<>();
    @Setter private List<Kit> kits = new ArrayList<>();
    private String status = "LOBBY";
    @Setter private IMatch currentMatch, spectatingMatch;
    @Setter private int matchesPlayed, rankedWins, rankedLosses, unrankedWins, unrankedLosses = 0;
    private Map<UUID, DuelRequest> duelRequests = new HashMap<>();
    @Setter private IQueue currentQueue;
    @Setter private QueueData queueData;
    @Setter private Team team;

    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;

        load();
    }

    public void load() {
        Document document = (Document) Practice.getPlugin().getProfilesCollection().find(Filters.eq("uniqueId", uniqueId)).first();

        if (document == null) return;

        uniqueId = (UUID) document.get("uniqueId");
        name = document.getString("name");
        elo = (Map<String, Integer>) document.get("elo");

        if (document.containsKey("kits")) {
            for (Document doc : (List<Document>) document.get("kits")) {
                Kit kit = new Kit(doc.getString("name"), Practice.getPlugin().getLadderManager().getLadder(doc.getString("ladder")), doc.getInteger("number"));
                kit.setInventory(doc.getString("inventory"));

                kits.add(kit);
            }
        }

        matchesPlayed = document.getInteger("matchesPlayed");
        rankedWins = document.getInteger("rankedWins");
        rankedLosses = document.getInteger("rankedLosses");
        unrankedWins = document.getInteger("unrankedWins");
        unrankedLosses = document.getInteger("unrankedLosses");
    }

    public void save() {
        Document document = new Document("_id", uniqueId);

        document.put("uniqueId", uniqueId);
        document.put("name", name);
        document.put("elo", elo);

        List<Document> documentKits = new ArrayList<>();
        for (Kit kit : kits) {
            Document kitDocument = new Document("name", kit.getName());
            kitDocument.put("ladder", kit.getLadder().getName());
            kitDocument.put("inventory", kit.getInventory());
            kitDocument.put("number", kit.getNumber());

            documentKits.add(kitDocument);
        }

        document.put("kits", documentKits);
        document.put("matchesPlayed", matchesPlayed);
        document.put("rankedWins", rankedWins);
        document.put("rankedLosses", rankedLosses);
        document.put("unrankedWins", unrankedWins);
        document.put("unrankedLosses", unrankedLosses);

        Bson filter = Filters.eq("uniqueId", uniqueId);
        FindIterable iterable = Practice.getPlugin().getProfilesCollection().find(filter);

        if (iterable.first() == null) {
            Practice.getPlugin().getProfilesCollection().insertOne(document);
        } else {
            Practice.getPlugin().getProfilesCollection().replaceOne(filter, document);
        }
    }

    public void addRequest(Player player, DuelRequest request) {
        duelRequests.put(player.getUniqueId(), request);
    }

    public void removeRequest(Player player) {
        duelRequests.remove(player.getUniqueId());
    }

    public boolean hasRequest(Player player) {
        return duelRequests.containsKey(player.getUniqueId());
    }

    public DuelRequest getRequest(Player player) {
        return duelRequests.get(player.getUniqueId());
    }

    public ProfileStatus getStatus() {
        return ProfileStatus.valueOf(status);
    }

    public void setStatus(ProfileStatus profileStatus) {
        status = profileStatus.name();
    }

    public void giveItems() {
        Player player = Bukkit.getPlayer(uniqueId);
        Inventory inventory = player.getInventory();

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});

        inventory.setItem(0, new ItemBuilder(Material.BOOK).setDisplayName("&6Kit Editor").setLore(" ").create());
        inventory.setItem(7, new ItemBuilder(Material.IRON_SWORD).setDisplayName("&9Un-Ranked Queue").setLore(" ").create());
        inventory.setItem(8, new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("&aRanked Queue").setLore(" ").create());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    public List<Kit> getKitsByLadder(Ladder ladder) {
        List<Kit> toReturn = new ArrayList<>();

        for (Kit kit : kits) {
            if (kit.getLadder() == ladder) {
                toReturn.add(kit);
            }
        }

        return toReturn;
    }

    public Kit getKitByLadderAndNumber(Ladder ladder, int number) {
        for (Kit kit : getKitsByLadder(ladder)) {
            if (kit.getNumber() == number) {
                return kit;
            }
        }

        return null;
    }

    public void showKits(Ladder ladder) {
        getPlayer().getInventory().addItem(new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName(ChatColor.GOLD + "Default " + ChatColor.GREEN + ladder.getName() + "'s kit").create());


        if (getKitByLadderAndNumber(ladder, 1) != null) {
            getPlayer().getInventory().setItem(2, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName(ChatColor.BLUE + "Custom " + ladder.getName() + " kit #1").create());
        }

        if (getKitByLadderAndNumber(ladder, 2) != null) {
            getPlayer().getInventory().setItem(3, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName(ChatColor.BLUE + "Custom " + ladder.getName() + " kit #2").create());
        }

        if (getKitByLadderAndNumber(ladder, 3) != null) {
            getPlayer().getInventory().setItem(4, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName(ChatColor.BLUE + "Custom " + ladder.getName() + " kit #3").create());
        }
    }
}
