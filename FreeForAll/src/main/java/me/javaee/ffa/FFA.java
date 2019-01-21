package me.javaee.ffa;

import com.bizarrealex.aether.Aether;
import com.bizarrealex.aether.sidebars.FFASidebar;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.javaee.ffa.combatlogger.CombatLoggerManager;
import me.javaee.ffa.commands.ResetKitCommand;
import me.javaee.ffa.commands.SaveKitCommand;
import me.javaee.ffa.information.InformationManager;
import me.javaee.ffa.koth.Koth;
import me.javaee.ffa.listeners.DeathMessageListener;
import me.javaee.ffa.listeners.PlayerListeners;
import me.javaee.ffa.listeners.WorldListeners;
import me.javaee.ffa.profiles.ProfileManager;
import me.javaee.ffa.profiles.tops.TopsManager;
import me.javaee.ffa.staff.StaffManager;
import me.javaee.ffa.timer.TimerManager;
import me.javaee.ffa.utils.FreeForAllDater;
import me.javaee.ffa.utils.LocationUtils;
import me.javaee.ffa.utils.command.CommandRegistrer;
import me.javaee.ffa.wand.WandManager;
import org.bukkit.Bukkit;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

@Getter
public class FFA extends JavaPlugin {
    @Getter private static FFA plugin;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection profilesCollection;
    private ProfileManager profileManager;
    private MongoCollection informationCollection;
    private InformationManager informationManager;
    private WandManager wandManager;
    private Koth koth;
    private TimerManager timerManager;
    private StaffManager staffManager;
    private TopsManager topsManager;
   // private FreeForAllDater dater;

    @Override
    public void onEnable() {
        plugin = this;

        /* Initialize Mongo first*/
        mongoClient = new MongoClient("127.0.0.1", 27017);
        mongoDatabase = mongoClient.getDatabase("FreeForAll");

        profilesCollection = mongoDatabase.getCollection("Profiles");
        profileManager = new ProfileManager();

        informationCollection = mongoDatabase.getCollection("Information");
        informationManager = new InformationManager();
        /* Initialize Mongo first*/

        wandManager = new WandManager();
        koth = new Koth();
        timerManager = new TimerManager(this);
        staffManager = new StaffManager();

        new CombatLoggerManager(this);
        new Aether(this, new FFASidebar(this));

        registerListeners(new WorldListeners(), new PlayerListeners(), new DeathMessageListener(this));
        new CommandRegistrer();
        topsManager = new TopsManager();

        for (Entity entity : Bukkit.getWorld("MAPFFA").getEntities()) {
            entity.remove();
        }

       // dater = new FreeForAllDater(this);
    }

    @Override
    public void onDisable() {
     //   dater.update();
    }

    public void registerListeners(Listener... listeners) {
        Stream.of(listeners).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }
}
