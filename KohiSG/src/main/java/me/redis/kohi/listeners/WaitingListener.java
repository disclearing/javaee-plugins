package me.redis.kohi.listeners;

import me.redis.kohi.SurvivalGames;
import me.redis.kohi.database.profiles.Profile;
import me.redis.kohi.database.profiles.status.PlayerStatus;
import me.redis.kohi.game.states.GameState;
import me.redis.kohi.schematic.SchematicPopulator;
import me.redis.kohi.tasks.RoadProcessor;
import me.redis.kohi.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class WaitingListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(null); // Every time stuff!

        player.setHealth(20);
        player.setSaturation(14F);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setMaximumNoDamageTicks(19);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (Bukkit.getOnlinePlayers().size() >= 6 && !SurvivalGames.getPlugin().getGameManager().isStartedCountdown()) {
            SurvivalGames.getPlugin().getGameManager().startCountdown();
        }

        Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);
        profile.setPlayerStatus(PlayerStatus.SPECTATING);

        for (Player online : Bukkit.getOnlinePlayers()) {
            Profile onlineProfile = SurvivalGames.getPlugin().getProfileManager().getProfile(online);

            if (onlineProfile.getPlayerStatus() == PlayerStatus.PLAYING) {
                online.hidePlayer(player);
            }

            player.showPlayer(online);
        }

        Bukkit.getScheduler().runTaskLater(SurvivalGames.getPlugin(), () -> {
            player.setHealth(20);
            player.setSaturation(14F);
            player.setAllowFlight(true);
            player.setFlying(true);
        }, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        SurvivalGames.getPlugin().getGameManager().getPlayerList().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        event.getWorld().setSpawnLocation(0, 0, 0);
        event.getWorld().getPopulators().add(new SchematicPopulator(SurvivalGames.getPlugin().getChestInformation()));
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        new RoadProcessor(event.getWorld().getSpawnLocation(), 10000000, 5).run();
    }

    @EventHandler
    public void onPlayerInitialSpawn(PlayerSpawnLocationEvent event) {
        event.setSpawnLocation(new Location(event.getPlayer().getWorld(), 4.0D, 67.0D, 4.0D));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp((int) (event.getDroppedExp() * 5D));
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        if (!SurvivalGames.getPlugin().isCanJoin()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "The server is loading... " + ChatColor.GRAY + "(You need to wait " + SurvivalGames.getPlugin().getGameManager().getWorldBorder() + " more seconds...)");
        }
    }
}
