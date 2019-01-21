package me.javaee.ffa.listeners;

import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.profiles.status.PlayerStatus;
import me.javaee.ffa.utils.ItemBuilder;
import me.javaee.ffa.utils.LocationUtils;
import me.javaee.ffa.utils.SerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

public class PlayerListeners implements Listener {

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.teleport(LocationUtils.getLocation(FFA.getPlugin().getInformationManager().getInformation().getLobbyLocation()).add(-.5, 0, -.5));

        player.setFoodLevel(20);
        player.setHealth(20);
        player.setNoDamageTicks(19);
        player.setSaturation(14);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        event.setJoinMessage(null);
        SerializationUtils.setKitToPlayer(player);
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Item || event.getEntity() instanceof Player || event.getEntity() instanceof Villager) {
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName() != null) {
            if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD.toString() + ChatColor.BOLD + "Golden Head")) {

                event.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
            }
        }
    }

    @EventHandler
    public void onSaturation(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        FFA.getPlugin().getProfileManager().getProfile(player).setDeaths(FFA.getPlugin().getProfileManager().getProfile(player).getDeaths() + 1);
        Bukkit.getScheduler().runTaskAsynchronously(FFA.getPlugin(), () -> {
            FFA.getPlugin().getProfileManager().getProfile(player).save();
        });

        event.getDrops().clear();
        event.getDrops().add(new ItemBuilder(Material.GOLDEN_APPLE).setDisplayName("&6&lGolden Head").create());
        event.getDrops().add(new ItemStack(Material.ARROW, 4));

        if (player.getKiller() != null) {
            FFA.getPlugin().getProfileManager().getProfile(event.getEntity().getKiller()).setKills(FFA.getPlugin().getProfileManager().getProfile(event.getEntity().getKiller()).getKills() + 1);

            if (!player.getKiller().getInventory().containsAtLeast(new ItemStack(Material.GOLDEN_APPLE), 6)) {
                event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 2));
            }
        }

        if (player.getKiller() != null) {
            FFA.getPlugin().getProfileManager().getProfile(player.getKiller()).setKillstreak(FFA.getPlugin().getProfileManager().getProfile(player.getKiller()).getKillstreak() + 1);

            int winnerElo = FFA.getPlugin().getProfileManager().getProfile(player.getKiller()).getElo();
            int loserElo = FFA.getPlugin().getProfileManager().getProfile(player).getElo();

            int winnerNewElo = (FFA.getPlugin().getProfileManager().getProfile(player.getKiller()).getNewRating(winnerElo, loserElo, 1));
            int loserNewElo = FFA.getPlugin().getProfileManager().getProfile(player).getNewRating(loserElo, winnerElo, 0);

            winnerNewElo = winnerNewElo - winnerElo >= 0 ? winnerNewElo : new Random().nextInt(5) + 1;

            FFA.getPlugin().getProfileManager().getProfile(player.getKiller()).setElo(winnerNewElo);
            FFA.getPlugin().getProfileManager().getProfile(player).setElo(loserNewElo);
            FFA.getPlugin().getProfileManager().getProfile(player).setKillstreak(0);

            player.getKiller().sendMessage(ChatColor.GREEN + "You now have " + winnerNewElo + ". " + ChatColor.GRAY + "(+" + (winnerNewElo - winnerElo) + ")");
            player.sendMessage(ChatColor.RED + "You now have " + loserNewElo + ". " + ChatColor.GRAY + "(-" + (loserElo - loserNewElo) + ")");

            if (winnerNewElo % 1000 == 0 && winnerNewElo != 1000) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a" + player.getKiller().getName() + "&e ha alcanzado los &a" + winnerNewElo + "&e de elo!"));
            }

            if (FFA.getPlugin().getProfileManager().getProfile(player.getKiller()).getKillstreak() % 5 == 0) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lSilex FFA&7] &f" + player.getKiller().getName() + " &6has reached a killstreak of &f" + FFA.getPlugin().getProfileManager().getProfile(player.getKiller()).getKillstreak() + "&6."));

                player.getKiller().getInventory().addItem(new ItemBuilder(Material.FLINT_AND_STEEL).setDurability((short) 60).create());
                if (player.getKiller().getInventory().contains(Material.QUARTZ_BLOCK)) {
                    player.getKiller().getInventory().addItem(new ItemStack(Material.QUARTZ_BLOCK, 64));
                }

                for (ItemStack item : player.getKiller().getInventory().getArmorContents()) {
                    if (item != null) item.setDurability((short) 0);
                }
            }

            Bukkit.getScheduler().runTaskAsynchronously(FFA.getPlugin(), () -> {
                FFA.getPlugin().getProfileManager().getProfile(player).save();
                FFA.getPlugin().getProfileManager().getProfile(player.getKiller()).save();
            });
        }

        if (FFA.getPlugin().getInformationManager().getInformation().getLobbyCuboid().contains(player.getLocation())) event.getDrops().clear();

        autoRespawn(event);
    }

    private void autoRespawn(PlayerDeathEvent event) {
        new BukkitRunnable() {
            public void run() {
                try {
                    Object nmsPlayer = event.getEntity().getClass().getMethod("getHandle", new Class[0]).invoke(event.getEntity(), new Object[0]);
                    Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
                    Class EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");
                    Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    Object mcserver = minecraftServer.get(con);
                    Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList", new Class[0]).invoke(mcserver, new Object[0]);
                    Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer, Integer.TYPE, Boolean.TYPE);
                    moveToWorld.invoke(playerlist, nmsPlayer, 0, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater(FFA.getPlugin(), 5);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(LocationUtils.getLocation(FFA.getPlugin().getInformationManager().getInformation().getLobbyLocation()).add(-.5, 0, -.5));
        SerializationUtils.setKitToPlayer(event.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Information information = FFA.getPlugin().getInformationManager().getInformation();

        if (information.getLobbyCuboid().contains(event.getPlayer().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Information information = FFA.getPlugin().getInformationManager().getInformation();

            if (information.getLobbyCuboid().contains(event.getEntity().getLocation())) {
                event.setCancelled(true);
            }

            if (event.getEntity().getFallDistance() > 50) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void DamageEvent(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Arrow)) {
            Arrow arrow = (Arrow) event.getDamager();
            if ((arrow.getShooter() instanceof Player)) {
                Player shooter = (Player) arrow.getShooter();

                Damageable damageable = (Damageable) event.getEntity();
                if ((damageable instanceof Player)) {
                    Player victim = (Player) damageable;
                    double victimHealth = damageable.getHealth();
                    int damage = (int) event.getFinalDamage();

                    if (!damageable.isDead()) {
                        int health = (int) (victimHealth - damage);

                        if (health > 0) {
                            shooter.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6" + victim.getName() + " &eis now at &c" + health + "❤"));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAsync(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        long onlinePlayers = Bukkit.getOnlinePlayers().stream().filter(online -> !online.hasPermission("batman.rank.join")).count();

        if (onlinePlayers > 60 && !player.hasPermission("batman.rank.join")) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, ChatColor.translateAlternateColorCodes('&', "&cLo siento, no puedes entrar a la modalidad debido a que hay una restricción de usuarios &7&o(Compra un rango en &7tienda.silexpvp.net&o)&e."));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            Profile profile = FFA.getPlugin().getProfileManager().getProfile(event.getPlayer().getUniqueId());
            if (profile.getPlayerStatus() == PlayerStatus.FROZEN) {
                Player player = event.getPlayer();

                player.sendMessage("");
                player.sendMessage(ChatColor.WHITE + "████" + ChatColor.RED + "█" + ChatColor.WHITE + "████");
                player.sendMessage(ChatColor.WHITE + "███" + ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█" + ChatColor.WHITE + "███" + ChatColor.DARK_RED + ChatColor.BOLD + " ATTENTION!");
                player.sendMessage(ChatColor.WHITE + "██" + ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.BLACK + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█" + ChatColor.WHITE + "██");
                player.sendMessage(ChatColor.WHITE + "██" + ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.BLACK + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█" + ChatColor.WHITE + "██" + ChatColor.RED + " You have been frozen by an Staff Member.");
                player.sendMessage(ChatColor.WHITE + "█" + ChatColor.RED + "█" + ChatColor.GOLD + "██" + ChatColor.BLACK + "█" + ChatColor.GOLD + "██" + ChatColor.RED + "█" + ChatColor.WHITE + "█" + ChatColor.RED + " If you log out, you will be " + ChatColor.DARK_RED + ChatColor.BOLD + "BANNED" + ChatColor.RED + ".");
                player.sendMessage(ChatColor.WHITE + "█" + ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + ChatColor.WHITE + "█");
                player.sendMessage(ChatColor.RED + "█" + ChatColor.GOLD + "███" + ChatColor.BLACK + "█" + ChatColor.GOLD + "███" + ChatColor.RED + "█ Connect to our TeamSpeak " + ChatColor.GRAY + ChatColor.UNDERLINE + "ts.silexpvp.net" + ChatColor.RED + ".");
                player.sendMessage(ChatColor.RED + "█████████");
                player.sendMessage("");

                event.setTo(from);
            }
        }
    }
}
