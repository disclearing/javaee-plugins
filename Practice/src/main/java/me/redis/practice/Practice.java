package me.redis.practice;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.redis.practice.arena.Arena;
import me.redis.practice.arena.ArenaManager;
import me.redis.practice.kit.KitManager;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.ladders.LadderManager;
import me.redis.practice.listeners.ListenerRegisterer;
import me.redis.practice.match.MatchManager;
import me.redis.practice.profile.Profile;
import me.redis.practice.profile.ProfileManager;
import me.redis.practice.protocol.ProtocolHandler;
import me.redis.practice.queue.QueueManager;
import me.redis.practice.scoreboard.Aether;
import me.redis.practice.scoreboard.sidebars.UHCScoreboard;
import me.redis.practice.spectator.SpectatorManager;
import me.redis.practice.team.TeamManager;
import me.redis.practice.timer.TimerManager;
import me.redis.practice.tournament.TournamentManager;
import me.redis.practice.utils.EntityHider;
import me.redis.practice.utils.command.CommandRegistrer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Practice extends JavaPlugin {
    @Getter private static Practice plugin; // This one needs the @Getter withoout exception!

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private MongoCollection arenasCollection;
    private MongoCollection laddersCollection;
    private MongoCollection profilesCollection;

    private ArenaManager arenaManager;
    private LadderManager ladderManager;
    private ProfileManager profileManager;
    private MatchManager matchManager;
    private QueueManager queueManager;
    private TeamManager teamManager;
    private SpectatorManager spectatorManager;
    private KitManager kitManager;
    private TournamentManager tournamentManager;

    private EntityHider entityHider;
    private TimerManager timerManager;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        getServer().createWorld(new WorldCreator(getConfig().getString("WORLD.NAME")));

        /* Initialize Mongo first*/
        mongoClient = new MongoClient(getConfig().getString("DATABASE.AUTH.HOST"), getConfig().getInt("DATABASE.AUTH.PORT"));
        mongoDatabase = mongoClient.getDatabase(getConfig().getString("DATABASE.NAME"));

        arenasCollection = mongoDatabase.getCollection(getConfig().getString("DATABASE.COLLECTIONS.ARENA"));
        laddersCollection = mongoDatabase.getCollection(getConfig().getString("DATABASE.COLLECTIONS.LADDERS"));
        profilesCollection = mongoDatabase.getCollection(getConfig().getString("DATABASE.COLLECTIONS.PROFILES"));
        /* Initialize Mongo first*/

        entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        arenaManager = new ArenaManager();
        arenaManager.getArenasFromDatabase().forEach(Arena::save);

        ladderManager = new LadderManager();
		queueManager = new QueueManager();
		
        ladderManager.getLaddersFromDatabase().forEach(Ladder::save);

        profileManager = new ProfileManager();
        matchManager = new MatchManager();
        teamManager = new TeamManager();
        spectatorManager = new SpectatorManager();
        kitManager = new KitManager();
        timerManager = new TimerManager(this);
        tournamentManager = new TournamentManager();

        new ListenerRegisterer();
        new ProtocolHandler();
        new CommandRegistrer();

        new Aether(this, new UHCScoreboard(this));
    }

    @Override
    public void onDisable() {
        arenaManager.getArenas().values().forEach(Arena::save);
        ladderManager.getLadders().values().forEach(Ladder::save);
        profileManager.getProfiles().values().forEach(Profile::save);
    }
}
