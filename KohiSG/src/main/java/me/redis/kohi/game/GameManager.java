package me.redis.kohi.game;

import lombok.Getter;
import lombok.Setter;
import me.redis.kohi.SurvivalGames;
import me.redis.kohi.database.profiles.Profile;
import me.redis.kohi.database.profiles.status.PlayerStatus;
import me.redis.kohi.game.states.GameState;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutExperience;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class GameManager {
    private SurvivalGames survivalGames;

    @Setter private GameState gameState = GameState.WAITING;
    @Setter private BukkitTask countdown;
    @Setter private boolean startedCountdown;
    @Setter private long countdownTime = 60;
    @Setter private Player winner;
    @Setter private int worldBorder = 60;
    private List<UUID> playerList = new ArrayList<>();

    public GameManager() {
        survivalGames = SurvivalGames.getPlugin();

    }

    public void startCountdown() {
        startedCountdown = true;

        countdown = Bukkit.getScheduler().runTaskTimer(survivalGames, () -> {
            if (countdownTime % 30 == 0 && countdownTime > 0) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aGame starting in &e" + countdownTime + " &aseconds"));
            } else if (countdownTime <= 10 && countdownTime > 0) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aGame starting in &e" + countdownTime + " &aseconds"));

                if (countdownTime <= 5) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 100000, 100000);
                    }
                }
            } else if (countdownTime < 1) {
                startGame();

                Bukkit.getScheduler().cancelTask(countdown.getTaskId());
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
                PacketPlayOutExperience packet = new PacketPlayOutExperience(1, (int) countdownTime, (int) countdownTime);

                entityPlayer.playerConnection.sendPacket(packet);
            }

            countdownTime--;
        }, 20L, 20L);
    }

    public void startGame() {
        gameState = GameState.PLAYING;

        Bukkit.getOnlinePlayers().forEach(player -> {
            Profile profile = survivalGames.getProfileManager().getProfile(player);

            player.sendMessage(ChatColor.RED + "You have 3 minutes of pvp protection!");
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 30, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 3));
            player.teleport(new Location(Bukkit.getWorld("world"), 8, 65, 8));
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().addItem(new ItemStack(Material.COMPASS));

            profile.setPlayed(profile.getPlayed() + 1);
            profile.setPlayerStatus(PlayerStatus.PLAYING);

            survivalGames.getTimerManager().getProtection().setCooldown(player, player.getUniqueId());
            playerList.add(player.getUniqueId());
        });

        Bukkit.getScheduler().runTaskLater(survivalGames, () -> survivalGames.getTimerManager().getFeastTimer().setRemaining(TimeUnit.MINUTES.toMillis(7), true), 20 * 60 * 3);
    }

    public int getAlivePlayers() {
        int toReturn = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (SurvivalGames.getPlugin().getProfileManager().getProfile(player).getPlayerStatus() == PlayerStatus.PLAYING) {
                toReturn++;
            }
        }

        return toReturn;
    }
}
