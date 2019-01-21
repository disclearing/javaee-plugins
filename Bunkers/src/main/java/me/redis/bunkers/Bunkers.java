package me.redis.bunkers;

import com.bizarrealex.azazel.Azazel;
import com.bizarrealex.azazel.tab.example.ExampleTabAdapter;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.redis.bunkers.entity.EntityManager;
import me.redis.bunkers.game.GameManager;
import me.redis.bunkers.game.status.GameStatus;
import me.redis.bunkers.information.InformationManager;
import me.redis.bunkers.koth.Koth;
import me.redis.bunkers.listeners.DeathMessageListener;
import me.redis.bunkers.listeners.PlayerListeners;
import me.redis.bunkers.profiles.ProfileManager;
import me.redis.bunkers.protocol.ProtocolListener;
import me.redis.bunkers.scoreboard.Aether;
import me.redis.bunkers.scoreboard.sidebars.BunkersSidebar;
import me.redis.bunkers.spectator.Spectator;
import me.redis.bunkers.team.Team;
import me.redis.bunkers.team.TeamManager;
import me.redis.bunkers.timer.TimerManager;
import me.redis.bunkers.utils.command.CommandRegistrer;
import me.redis.bunkers.wand.WandManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
public class Bunkers extends JavaPlugin {
    @Getter private static Bunkers plugin;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private MongoCollection informationCollection;
    private InformationManager informationManager;

    private MongoCollection teamsCollection;
    private TeamManager teamManager;

    private MongoCollection profilesCollection;
    private ProfileManager profileManager;

    private WandManager wandManager;
    private GameManager gameManager;
    private Koth koth;
    private TimerManager timerManager;
    private Spectator spectatorManager;
    private EntityManager entityManager;
    private Azazel azazel;

    @Override
    public void onEnable() {
        plugin = this;

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                entity.remove();
            }

            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doDayLightCycle", "false");
            world.setTime(0);
        }

        /* Initialize Mongo first*/
        mongoClient = new MongoClient(getConfig().getString("DATABASE.AUTH.HOST"), getConfig().getInt("DATABASE.AUTH.PORT"));
        mongoDatabase = mongoClient.getDatabase(getConfig().getString("DATABASE.NAME"));

        informationCollection = mongoDatabase.getCollection(getConfig().getString("DATABASE.COLLECTIONS.INFORMATION"));
        teamsCollection = mongoDatabase.getCollection(getConfig().getString("DATABASE.COLLECTIONS.TEAMS"));
        profilesCollection = mongoDatabase.getCollection(getConfig().getString("DATABASE.COLLECTIONS.PROFILES"));
        /* Initialize Mongo first*/

        teamManager = new TeamManager();
        try {
            teamManager.nig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        informationManager = new InformationManager();
        profileManager = new ProfileManager();
        wandManager = new WandManager();
        gameManager = new GameManager();
        gameManager.setStatus(GameStatus.WAITING);

        koth = new Koth();
        timerManager = new TimerManager(this);
        spectatorManager = new Spectator();
        entityManager = new EntityManager();

        new CommandRegistrer();
        new ProtocolListener();

        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
        Bukkit.getPluginManager().registerEvents(new DeathMessageListener(), this);

        new Aether(this, new BunkersSidebar(this));
        azazel = new Azazel(this, new ExampleTabAdapter());
    }

    @Override
    public void onDisable() {
        teamManager.getTeams().values().forEach(Team::save);
    }
}
