package me.redis.kohi;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.patterns.BlockChance;
import com.sk89q.worldedit.patterns.RandomFillPattern;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import lombok.Getter;
import lombok.Setter;
import me.redis.kohi.border.BorderManager;
import me.redis.kohi.chest.ChestInformation;
import me.redis.kohi.database.information.InformationManager;
import me.redis.kohi.database.profiles.ProfileManager;
import me.redis.kohi.game.GameManager;
import me.redis.kohi.listeners.DeathMessageListener;
import me.redis.kohi.listeners.GameListener;
import me.redis.kohi.listeners.SpectatorListener;
import me.redis.kohi.listeners.WaitingListener;
import me.redis.kohi.scoreboard.Aether;
import me.redis.kohi.scoreboard.sidebars.SurvivalGamesSidebar;
import me.redis.kohi.timer.TimerManager;
import me.redis.kohi.utils.MongoWrapper;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class SurvivalGames extends JavaPlugin {
    @Getter
    private static SurvivalGames plugin;

    private GameManager gameManager;
    private TimerManager timerManager;
    private ChestInformation chestInformation;
    private ChestInformation feastInformation;
    private BorderManager borderManager;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private MongoCollection profilesCollection;
    private MongoCollection informationCollection;
    private ProfileManager profileManager;
    private InformationManager informationManager;

    @Setter
    private boolean canJoin = false;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        try {
            FileUtils.deleteDirectory(new File("world"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        mongoClient = new MongoClient("127.0.0.1", 27017);
        mongoDatabase = mongoClient.getDatabase("SurvivalGames");

        profilesCollection = mongoDatabase.getCollection("Profiles");
        profileManager = new ProfileManager();

        informationCollection = mongoDatabase.getCollection("Information");
        informationManager = new InformationManager();

        gameManager = new GameManager();
        timerManager = new TimerManager(this);

        chestInformation = new ChestInformation(this, "loot.yml");
        chestInformation.load();

        feastInformation = new ChestInformation(this, "feast.yml");
        feastInformation.load();

        borderManager = new BorderManager();

        new Aether(this, new SurvivalGamesSidebar(this));
        registerListeners(new WaitingListener(), new SpectatorListener(), new GameListener(), new DeathMessageListener(this));
    }

    @Override
    public void onDisable() {
        getInformationManager().getInformation().save();
        getProfileManager().saveProfiles();
    }

    public void registerListeners(Listener... listener) {
        Arrays.stream(listener).forEach(event -> Bukkit.getPluginManager().registerEvents(event, this));
    }
}
