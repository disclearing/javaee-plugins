package me.javaee.meetup;

import com.bizarrealex.aether.Aether;
import com.bizarrealex.aether.sidebars.UHCScoreboard;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import lombok.Getter;
import lombok.Setter;
import me.javaee.meetup.border.BorderManager;
import me.javaee.meetup.border.GlassListener;
import me.javaee.meetup.border.GlitchPreventListener;
import me.javaee.meetup.commands.*;
import me.javaee.meetup.enums.GameState;
import me.javaee.meetup.generator.WorldGenerator;
import me.javaee.meetup.handlers.Scenario;
import me.javaee.meetup.handlers.SpawnsHandler;
import me.javaee.meetup.kit.KitManager;
import me.javaee.meetup.listeners.*;
import me.javaee.meetup.listeners.scenarios.*;
import me.javaee.meetup.managers.GameManager;
import me.javaee.meetup.menu.MenuListener;
import me.javaee.meetup.profile.ProfileUtils;
import me.javaee.meetup.redis.RedisPublisher;
import me.javaee.meetup.redis.RedisSubscriber;
import me.javaee.meetup.spectator.Spectator;
import me.javaee.meetup.spectator.SpectatorListener;
import me.javaee.meetup.tasks.BorderShrinkTask;
import me.javaee.meetup.timer.TimerManager;
import me.javaee.meetup.utils.DatabaseUtils;
import net.minecraft.server.v1_8_R3.*;
import net.silexpvp.nightmare.Nightmare;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.*;
import java.util.logging.Level;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class Meetup extends JavaPlugin {
    @Getter
    private static Meetup plugin;
    @Getter
    private GameManager gameManager;
    @Getter
    private SpawnsHandler spawnsHandler;
    @Getter
    private WorldGenerator worldGenerator;
    @Getter
    private Spectator spectatorManager;
    @Getter
    @Setter
    private BorderShrinkTask borderShrinkTask;
    @Getter
    BorderManager borderManager;
    @Getter
    @Setter
    private Boolean borderShrink = true;
    @Getter
    KitManager kitManager;
    @Getter
    List<Scenario> scenarios = new ArrayList<>();
    @Getter
    private TimerManager timerManager;
    @Getter
    private List<Player> mounted = new ArrayList<>();
    @Getter
    private List<Player> voted = new ArrayList<>();
    @Getter
    @Setter
    private Boolean started = false;

    @Getter
    private JedisPool pool;
    @Getter
    private RedisPublisher redisPublisher;
    @Getter
    private RedisSubscriber redisSubscriber;

    @Override
    public void onEnable() {
        plugin = this;

        try {
            DatabaseUtils.getInstance().setup();
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Could not connect to the MongoDB database.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            pool = new JedisPool(InetAddress.getLocalHost().getHostAddress().equalsIgnoreCase("144.217.10.216") ? "127.0.0.1" : "144.217.10.216");
        } catch (Exception exception) {
            getLogger().log(Level.SEVERE, "Could not connect to redis.");
            Bukkit.getLogger().warning("Redis database not found!");
            Bukkit.shutdown();
        }

        redisPublisher = new RedisPublisher();
        redisSubscriber = new RedisSubscriber();

        ProfileUtils.getInstance().setup();

        gameManager = new GameManager();
        spawnsHandler = new SpawnsHandler();
        worldGenerator = new WorldGenerator();
        spectatorManager = new Spectator();
        kitManager = new KitManager();
        addScenarios();
        timerManager = new TimerManager(this);

        Bukkit.setWhitelist(false);

        borderManager = new BorderManager();
        Bukkit.getPluginManager().registerEvents(new GlassListener(borderManager), this);
        Bukkit.getPluginManager().registerEvents(new GlitchPreventListener(borderManager), this);

        new Aether(this, new UHCScoreboard(this));

        Bukkit.getPluginManager().registerEvents(new WorldGenerator(), this);
        Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathMessageListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldGenerationListener(), this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);

        Bukkit.getPluginManager().registerEvents(new AbsorptionLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new BowLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new NoCleanListener(), this);
        Bukkit.getPluginManager().registerEvents(new RodLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new StaffModeListener(), this);

        Bukkit.getPluginCommand("mlg").setExecutor(new MLGCommand());
        Bukkit.getPluginCommand("setkit").setExecutor(new SetKitCommand());
        Bukkit.getPluginCommand("vote").setExecutor(new VoteCommand());
        Bukkit.getPluginCommand("truce").setExecutor(new TruceCommand());
        // Bukkit.getPluginCommand("staffchat").setExecutor(new StaffChatCommand());
        Bukkit.getPluginCommand("reroll").setExecutor(new RerollCommand());
        Nightmare.getPlugin().registerCommand(new StaffModeCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        getWorldGenerator().swapBiomes();
        //getWorldGenerator().scanArena();

        new BukkitRunnable() {
            public void run() {
                getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
                getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + "world" + " set " + "125" + " 0 0");
                getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + "world" + " fill 5000");
                getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");

                Bukkit.getWorld("world").setGameRuleValue("naturalRegeneration", "false");
            }
        }.runTaskLaterAsynchronously(this, 20L);

        new BukkitRunnable() {
            public void run() {
                if (getGameManager().getGameState() == GameState.INGAME) {
                    if (getGameManager().getAlivePlayers().size() <= 1) {
                        getGameManager().setGameState(GameState.END);

                        if (Bukkit.getPlayer(getGameManager().getAlivePlayers().get(0).getUniqueId()) != null) {
                            getGameManager().setWinner(Bukkit.getPlayer(getGameManager().getWinner().getUniqueId()));

                            MLGCommand.allowedMLGPlayers.add(getGameManager().getWinner().getUniqueId());
                            getGameManager().getWinner().sendMessage(ChatColor.BOLD.toString() + ChatColor.YELLOW + "You have 10 seconds to type /mlg to try the MLG water bucket challenge!");
                            getGameManager().setWinner(getGameManager().getWinner());
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &6Congratulations to &f" + getGameManager().getWinner().getName() + "&6 for winning the Meetup!"));
                            ProfileUtils.getInstance().getProfile(getGameManager().getWinner().getUniqueId()).setWinnedGames(ProfileUtils.getInstance().getProfile(getGameManager().getWinner().getUniqueId()).getWinnedGames() + 1);
                            ProfileUtils.getInstance().getProfile(getGameManager().getWinner().getUniqueId()).setRerolls(ProfileUtils.getInstance().getProfile(getGameManager().getWinner().getUniqueId()).getRerolls() + 1);
                            ProfileUtils.getInstance().getProfile(getGameManager().getWinner().getUniqueId()).save(true);
                            cancel();

                            Meetup.getPlugin().getServer().getScheduler().runTask(Meetup.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    // Do MLG after 10 seconds
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            // MLG
                                            MLGCommand.doMLG();
                                        }
                                    }.runTaskLater(Meetup.this, 20L * 10);
                                }
                            });

                            Meetup.getPlugin().getServer().getScheduler().runTask(Meetup.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.shutdown();
                                        }
                                    }.runTaskLater(Meetup.this, 20L * 60);
                                }
                            });
                        } else {
                            Meetup.getPlugin().getServer().getScheduler().runTask(Meetup.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.shutdown();
                                        }
                                    }.runTaskLater(Meetup.this, 20L * 60);
                                }
                            });
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 20L, 20L);

        new BukkitRunnable() {
            public void run() {
                if (!getStarted()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOnGround()) {
                            spawnEntity(player);
                            mounted.add(player);
                        }
                    }
                } else {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &6The meetup has commenced!"));
                    getGameManager().setGameState(GameState.INGAME);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (villagers.containsKey(player)) {
                            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(villagers.get(player));

                            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                        }
                    }

                    cancel();
                }
            }
        }.runTaskTimer(this, 20L, 20L);

        Meetup.getPlugin().setBorderShrinkTask(new BorderShrinkTask(1, 2, 25, 125, -1, -1));
        Bukkit.broadcastMessage(ChatColor.GREEN + "Empieza a los " + 1 + ", de " + 25 + " bloques cada " + 3 + " minutos hasta " + 25 + "x" + 25);

        // PACKETS INTERCEPT STUFF

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(
                new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                            String sound = event.getPacket().getStrings().read(0);

                            if (StringUtils.containsIgnoreCase(sound, "wither")) {
                                event.setCancelled(true);
                            }
                        }
                    }
                });

        protocolManager.addPacketListener(
                new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (getGameManager().getGameState() != GameState.INGAME) {
                            if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                                event.getPacket().getBooleans().write(0, false);
                                event.setCancelled(true);
                            }
                        }
                    }
                }
        );
        // END
        getGameManager().setGameState(GameState.LOADING);

        VapeListener vapeListener = new VapeListener();
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "LOLIMAHCKER", vapeListener);
        this.getServer().getPluginManager().registerEvents(vapeListener, this);
        try {
            setMaxPlayers(20);
        } catch (ReflectiveOperationException e) {
            //e.printStackTrace();
        }
    }

    private HashMap<Player, Integer> villagers = new HashMap<>();

    public void spawnEntity(Player player) {
        WorldServer worldServer = ((CraftWorld) player.getLocation().getWorld()).getHandle();
        EntityBat villager = new EntityBat(worldServer);

        villager.setLocation(player.getLocation().getX(), player.getLocation().getY() + 4, player.getLocation().getZ(), 0, 0);
        villager.setHealth(villager.getMaxHealth());
        villager.setInvisible(true);
        villager.d(0);
        villager.setAsleep(true);

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(villager);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity(0, ((CraftPlayer) player).getHandle(), villager);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(attach);

        villagers.put(player, villager.getId());
    }

    private void addScenarios() {
        new Scenario("Timebomb", new ItemStack(Material.TNT), "Your items will be stored in a chest when you die, this chest will explode in 30 seconds");
        new Scenario("Fireless", new ItemStack(Material.FIRE), "You can't take damage from fire");
        new Scenario("NoClean", new ItemStack(Material.SKULL_ITEM), "The killer of a player gets immunity for 20 seconds, The immunity will be loss if you punch anyone");
        new Scenario("AbsortionLess", new ItemStack(Material.GOLDEN_APPLE), "Your absorption will be removed after eating a golden apple");
        new Scenario("Rodless", new ItemStack(Material.FISHING_ROD), "You are not allowed to use rods, you are not allowed to craft rods");
        new Scenario("Bowless", new ItemStack(Material.BOW), "You are not allowed to use bows, you are not allowed to craft bows");
        new Scenario("OP", new ItemStack(Material.DRAGON_EGG), "All the enchantments go 2 levels up");
        new Scenario("No Scenarios", new ItemStack(Material.BEDROCK), "No Gamemodes");
    }

    public static void setMaxPlayers(int maxPlayers)
            throws ReflectiveOperationException {
        String bukkitversion = Bukkit.getServer().getClass().getPackage()
                .getName().substring(23);
        Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer")
                .getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
        Field maxplayers = playerlist.getClass().getSuperclass()
                .getDeclaredField("maxPlayers");
        maxplayers.setAccessible(true);
        maxplayers.set(playerlist, maxplayers);
    }

    @Override
    public void onDisable() {
        plugin = null;

        ProfileUtils.getInstance().saveProfiles();
        DatabaseUtils.getInstance().getClient().close();
    }

    public void changeArmor(net.minecraft.server.v1_8_R3.ItemStack item) {
        Random randy = new Random();

        if (item != null && item.getItem().getName().toLowerCase().contains("helmet")) {
            if (randy.nextInt(4) == 0) {
                item.setItem(Items.LEATHER_HELMET);
            } else if (randy.nextInt(4) == 1) {
                item.setItem(Items.IRON_HELMET);
            } else if (randy.nextInt(4) == 2) {
                item.setItem(Items.GOLDEN_HELMET);
            } else if (randy.nextInt(4) == 3) {
                item.setItem(Items.CHAINMAIL_HELMET);
            } else if (randy.nextInt(4) == 4) {
                item.setItem(Items.DIAMOND_HELMET);
            }
            item.setData(0);
        } else if (item != null && item.getItem().getName().toLowerCase().contains("chestplate")) {
            if (randy.nextInt(4) == 0) {
                item.setItem(Items.LEATHER_CHESTPLATE);
            } else if (randy.nextInt(4) == 1) {
                item.setItem(Items.IRON_CHESTPLATE);
            } else if (randy.nextInt(4) == 2) {
                item.setItem(Items.GOLDEN_CHESTPLATE);
            } else if (randy.nextInt(4) == 3) {
                item.setItem(Items.CHAINMAIL_CHESTPLATE);
            } else if (randy.nextInt(4) == 4) {
                item.setItem(Items.DIAMOND_CHESTPLATE);
            }
            item.setData(0);
        } else if (item != null && item.getItem().getName().toLowerCase().contains("leggings")) {
            if (randy.nextInt(4) == 0) {
                item.setItem(Items.LEATHER_LEGGINGS);
            } else if (randy.nextInt(4) == 1) {
                item.setItem(Items.IRON_LEGGINGS);
            } else if (randy.nextInt(4) == 2) {
                item.setItem(Items.GOLDEN_LEGGINGS);
            } else if (randy.nextInt(4) == 3) {
                item.setItem(Items.CHAINMAIL_LEGGINGS);
            } else if (randy.nextInt(4) == 4) {
                item.setItem(Items.DIAMOND_LEGGINGS);
            }
            item.setData(0);
        } else if (item != null && item.getItem().getName().toLowerCase().contains("boots")) {
            if (randy.nextInt(4) == 0) {
                item.setItem(Items.LEATHER_BOOTS);
            } else if (randy.nextInt(4) == 1) {
                item.setItem(Items.IRON_BOOTS);
            } else if (randy.nextInt(4) == 2) {
                item.setItem(Items.GOLDEN_BOOTS);
            } else if (randy.nextInt(4) == 3) {
                item.setItem(Items.CHAINMAIL_BOOTS);
            } else if (randy.nextInt(4) == 4) {
                item.setItem(Items.DIAMOND_BOOTS);
            }
            item.setData(0);
        } else if (item != null && item.getItem().getName().toLowerCase().contains("sword")) {
            if (randy.nextInt(4) == 0) {
                item.setItem(Items.STONE_SWORD);
            } else if (randy.nextInt(4) == 1) {
                item.setItem(Items.GOLDEN_SWORD);
            } else if (randy.nextInt(4) == 2) {
                item.setItem(Items.WOODEN_SWORD);
            } else if (randy.nextInt(4) == 3) {
                item.setItem(Items.IRON_SWORD);
            } else if (randy.nextInt(4) == 4) {
                item.setItem(Items.DIAMOND_BOOTS);
            }
            item.setData(0);
        }
    }
}
