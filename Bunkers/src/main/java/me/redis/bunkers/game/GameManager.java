package me.redis.bunkers.game;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.events.GameStatusChangeEvent;
import me.redis.bunkers.game.status.GameStatus;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.profiles.status.PlayerStatus;
import me.redis.bunkers.tasks.GameTimeTask;
import me.redis.bunkers.tasks.KothTask;
import me.redis.bunkers.team.Team;
import me.redis.bunkers.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager {
    @Getter private Bunkers bunkers = Bunkers.getPlugin();
    @Getter private GameStatus status;
    @Getter @Setter private Boolean started = false;
    @Getter @Setter private long gameTime;
    @Getter @Setter private boolean event = false;
    @Getter private int scoreboard = 0;
    @Getter @Setter private String eventName = "Custom Event";
    @Getter @Setter private Team winnerTeam;

    public GameManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Bunkers.getPlugin(), () -> {
            if (event) {
                if (scoreboard == 0) {
                    scoreboard = 1;
                } else if (scoreboard == 1) {
                    scoreboard = 0;
                }
            }
        }, 20L, 20 * 3L);
    
    }

    public void setStatus(GameStatus status) {
        Bukkit.getPluginManager().callEvent(new GameStatusChangeEvent(this.status, status));
        this.status = status;
    }

    public boolean canBePlayed() {
        return bunkers.getTeamManager().canBePlayed();
    }

    @Getter private final int[] cooldown = {30};
    public void startCooldown() {
        setStatus(GameStatus.STARTING);
        setStarted(true);

        new BukkitRunnable() {
            @Override public void run() {
                if (cooldown[0] <= 0) {
                    startGame();

                    cancel();
                } else {
                    if (cooldown[0] % 5 == 0) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe match will start in &9" + cooldown[0] + " seconds&e."));
                    }
                }

                cooldown[0]--;
            }
        }.runTaskTimer(Bunkers.getPlugin(), 20L, 20L);
    }

    public void startGame() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
                Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);
                Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);

                player.teleport(LocationUtils.getLocation(team.getSpawnLocation()));
                player.getInventory().clear();
                player.getInventory().setItem(0, new ItemStack(Material.STONE_PICKAXE));
                player.getInventory().setItem(1, new ItemStack(Material.STONE_AXE));
                setStatus(GameStatus.PLAYING);
                profile.setStatus(PlayerStatus.PLAYING);
                profile.setGamesPlayed(profile.getGamesPlayed() + 1);
                profile.setBalance(500);
                profile.save();
                team.setDtr(team.getMembers().size());

                player.sendMessage(ChatColor.GREEN + "The match has started...");
            } else {
                player.kickPlayer(ChatColor.RED + "You must have a team to play.");
            }
        }

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Bunkers.getPlugin(), () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

                if (profile.getStatus() == PlayerStatus.PLAYING) {
                    profile.setBalance(profile.getBalance() + 3);
                }
            });
        }, 0L, 20 * 3L);

        new GameTimeTask().runTaskTimerAsynchronously(Bunkers.getPlugin(), 20L, 20L);
        Bukkit.getScheduler().runTaskLater(Bunkers.getPlugin(), () -> new KothTask().runTaskTimer(Bunkers.getPlugin(), 20L, 20L), 20 * 60 * 5);
    }
}
