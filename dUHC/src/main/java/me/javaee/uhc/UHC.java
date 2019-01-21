package me.javaee.uhc;

import com.bizarrealex.aether.Aether;
import com.bizarrealex.aether.sidebars.UHCScoreboard;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.JsonObject;
import litebans.api.Entry;
import litebans.api.Events;
import lombok.Getter;
import lombok.Setter;
import me.javaee.uhc.biome.WorldManager;
import me.javaee.uhc.border.BorderManager;
import me.javaee.uhc.combatlogger.CombatLoggerManager;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.command.commands.*;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.database.serverinfo.ServerInfo;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.events.GameEndEvent;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.handlers.SpawnsHandler;
import me.javaee.uhc.listeners.TeamListeners;
import me.javaee.uhc.listeners.misc.*;
import me.javaee.uhc.listeners.scenarios.*;
import me.javaee.uhc.listeners.stats.ConsumeListener;
import me.javaee.uhc.listeners.stats.FoundOresListener;
import me.javaee.uhc.managers.GameManager;
import me.javaee.uhc.managers.MineManager;
import me.javaee.uhc.menu.MenuListener;
import me.javaee.uhc.practice.PracticeManager;
import me.javaee.uhc.practice.commands.PracticeCommand;
import me.javaee.uhc.redis.RedisMessagingHandler;
import me.javaee.uhc.spectator.Spectator;
import me.javaee.uhc.spectator.SpectatorListener;
import me.javaee.uhc.staffmode.StaffMode;
import me.javaee.uhc.staffmode.StaffModeListener;
import me.javaee.uhc.tasks.BorderShrinkTask;
import me.javaee.uhc.tasks.GameTimeTask;
import me.javaee.uhc.tasks.HackerReviveTask;
import me.javaee.uhc.tasks.RedisDataTask;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.timer.TimerManager;
import me.javaee.uhc.utils.Configurator;
import me.javaee.uhc.utils.DatabaseUtils;
import me.javaee.uhc.utils.ItemBuilder;
import me.javaee.uhc.visualise.VisualiseHandler;
import me.javaee.uhc.visualise.WallBorderListener;
import net.badlion.worldborder.WorldBorder;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.WhiteList;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.JedisPool;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */

public class UHC extends JavaPlugin implements Listener {
    @Getter
    private static UHC instance;
    @Getter
    private TimerManager timerManager;
    @Getter
    private final List<Scenario> scenarios = new ArrayList<>();
    @Getter
    private final List<UHCTeam> teams = new ArrayList<>();
    @Getter
    private GameManager gameManager;
    @Getter
    private Spectator spectatorManager;
    BukkitTask nigger = null;
    @Getter
    private StaffMode staffModeManager;
    @Getter
    private WorldBorder worldBorder;
    @Getter
    private Configurator configurator;
    @Getter
    private WorldManager worldManager;
    @Getter
    private List<BaseCommand> commands;
    @Getter
    private CommandMap commandMap;
    @Getter
    BorderManager borderManager;
    @Getter
    private SpawnsHandler generateSpawnsCommandHandler;
    @Getter
    private PracticeManager practiceManager;
    @Getter
    private GameTimeTask gameTimeTask;
    @Getter
    Twitter[] twitters = new Twitter[1];
    @Getter
    private VisualiseHandler visualiseHandler;

    @Getter
    private static JedisPool pool;
    @Getter private static RedisMessagingHandler redisMessagingHandler;

    @Getter
    private MineManager mineManager;
    @Getter
    private ServerInfo serverInfo;
    @Getter
    private BungeeMessagerListener bungeeMessagerListener;

    @Getter
    @Setter
    private BorderShrinkTask borderShrinkTask;
    @Getter
    @Setter
    private Boolean borderShrink = true;
    @Getter
    @Setter
    private boolean gameStarted;
    @Getter
    @Setter
    private String uhcNumber = "1";
    @Getter
    @Setter
    private boolean deathmatch = false;
    @Getter
    private CombatLoggerManager combatLoggerManager;
    @Getter
    public final Map<UUID, ArrayList<UUID>> ignoredPlayers = new HashMap<>();
    @Getter
    private final ArrayList<Player> npcs = new ArrayList<>();
    @Getter
    public final Map<UUID, Integer> whitelisted = new HashMap<>();

    @Getter final List<UUID> champions = new ArrayList<>();
    @Getter final List<UUID> weekend = new ArrayList<>();
    @Getter @Setter private String minutes = "-1";

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getWorld("lobby");
        Bukkit.getWorld("lobby").loadChunk(0, 0);
        Bukkit.getWorld("lobby").setSpawnLocation(0, 105, -18, 0, 0);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        WhiteList whitelist = ((CraftServer) Bukkit.getServer()).getHandle().getWhitelist();
        whitelist.getValues().clear();

        twitters[0] = new TwitterFactory(new ConfigurationBuilder().setDebugEnabled(true)
                .setOAuthConsumerKey("5AEJd8U65ub3Xre403fA9N9wP")
                .setOAuthConsumerSecret("fQfYjL6bRGtplijLAldqRJKHvMXsqn3XqDZ4HTW560pmFtjVHt")
                .setOAuthAccessToken("915630276671352833-tEkEm0f9peWk50crK5ToVYysLTenDYf")
                .setOAuthAccessTokenSecret("rbQsFL30atqkNiU5FF5c2kmkGeyxpdpZjRxGv8V5EoiMa")
                .build()).getInstance();

        /*twitters[1] = new TwitterFactory(new ConfigurationBuilder().setDebugEnabled(true)
                .setOAuthConsumerKey("AzCUuZb69JultR4smMaOyLJwi")
                .setOAuthConsumerSecret("lFBUmYWc6jnDVyiPsZsCBHfOiMQgXJ9WOFPFnkj4jnwnKt7eRy")
                .setOAuthAccessToken("814589171973181441-GMMjLEPpMgr8PQCMIDjdUwxhJSPFj3U")
                .setOAuthAccessTokenSecret("B4legkb1q2PqmO3CAj0wpKeTEMwFa2RXwMqREgZrlGb04")
                .build()).getInstance();*/

        try {
            pool = new JedisPool("127.0.0.1");
            redisMessagingHandler = new RedisMessagingHandler();
        } catch (Exception exception) {
            getLogger().log(Level.SEVERE, "Could not connect to redis.");
            Bukkit.shutdown();
        }

       /*redisPublisher = new RedisPublisher();
        redisSubscriber = new RedisSubscriber();*/

        timerManager = new TimerManager(this);

        try {
            DatabaseUtils.getInstance().setup();
        } catch (Exception e) {
            e.printStackTrace();
            super.getLogger().severe("Could not connect to the MongoDB database.");
            super.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ProfileUtils.getInstance().setup();

        gameManager = new GameManager();
        gameManager.setGameState(GameState.WAITING);

        spectatorManager = new Spectator();
        Bukkit.getPluginManager().registerEvents(this, this);

        staffModeManager = new StaffMode();

        worldManager = new WorldManager();

        this.worldBorder = (WorldBorder) this.getServer().getPluginManager().getPlugin("WorldBorder");
        createSoloConfigurator();

        generateSpawnsCommandHandler = new SpawnsHandler();

        practiceManager = new PracticeManager();
        practiceManager.init();

        serverInfo = new ServerInfo(Bukkit.getServerName());

        gameTimeTask = new GameTimeTask();

        saveConfig();

        setScenarios();

        nigger = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (getGameManager().getGameState() != GameState.WAITING) {
                Bukkit.getScheduler().cancelTask(nigger.getTaskId());
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() < 47) {
                    for (Player npc : UHC.getInstance().getNpcs()) {
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) npc).getHandle()));
                    }
                }
            }
        }, 20L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!getConfigurator().getBooleanOption("DEBUG").getValue()) {
                    if (getGameManager().getGameState() == GameState.INGAME) {
                        Boolean statLess = (Boolean) UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.STATLESS.name()).getValue();

                        if (getConfigurator().getIntegerOption("TEAMSIZE").getValue() > 1) {
                            if (teams.size() == 1) {
                                ArrayList<String> teamList = new ArrayList<>();

                                for (UUID uuid : teams.get(0).getPlayerList()) {
                                    teamList.add(Bukkit.getOfflinePlayer(uuid).getName());

                                    if (!statLess) {
                                        ProfileUtils.getInstance().getProfile(uuid).setWinnedGames(ProfileUtils.getInstance().getProfile(uuid).getWinnedGames() + 1);
                                        ProfileUtils.getInstance().getProfile(uuid).save(true);
                                    }
                                }

                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&6&lCongratulations to &f&l" + StringUtils.join(teamList, ", ") + " &6&lfor winning the game!"));

                                UHC.getInstance().getGameManager().setWinner(Bukkit.getPlayer(UHC.getInstance().getGameManager().getAlivePlayers().get(0)));
                                UHC.getInstance().getGameManager().setGameState(GameState.END);
                                getConfigurator().getBooleanOption(CONFIG_OPTIONS.STATLESS.name()).setValue(true);
                                Bukkit.getPluginManager().callEvent(new GameEndEvent());
                            }
                        } else {
                            if (UHC.getInstance().getGameManager().getAlivePlayers().size() <= 1) {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&6&lCongratulations to &f&l" + Bukkit.getPlayer(UHC.getInstance().getGameManager().getAlivePlayers().get(0)).getName() + " &6&lfor winning the game!"));
                                Profile profile = ProfileUtils.getInstance().getProfile(getGameManager().getAlivePlayers().get(0));

                                UHC.getInstance().getGameManager().setWinner(Bukkit.getPlayer(UHC.getInstance().getGameManager().getAlivePlayers().get(0)));
                                if (!statLess) {
                                    profile.setWinnedGames(profile.getWinnedGames() + 1);
                                    profile.save(true);
                                }
                                UHC.getInstance().getGameManager().setGameState(GameState.END);
                                getConfigurator().getBooleanOption(CONFIG_OPTIONS.STATLESS.name()).setValue(true);

                                Firework f = getGameManager().getWinner().getWorld().spawn(getGameManager().getWinner().getLocation(), Firework.class);

                                FireworkMeta fm = f.getFireworkMeta();
                                fm.addEffect(FireworkEffect.builder()
                                        .flicker(false)
                                        .trail(true)
                                        .with(FireworkEffect.Type.STAR)
                                        .withColor(Color.ORANGE)
                                        .withFade(Color.YELLOW)
                                        .build());
                                fm.setPower(3);
                                f.setFireworkMeta(fm);

                                new BukkitRunnable() {
                                    public void run() {
                                        Firework f = getGameManager().getWinner().getWorld().spawn(getGameManager().getWinner().getLocation(), Firework.class);

                                        FireworkMeta fm = f.getFireworkMeta();
                                        fm.addEffect(FireworkEffect.builder()
                                                .flicker(false)
                                                .trail(true)
                                                .with(FireworkEffect.Type.BURST)
                                                .withColor(Color.ORANGE)
                                                .withFade(Color.YELLOW)
                                                .build());
                                        fm.setPower(3);
                                        f.setFireworkMeta(fm);
                                    }
                                }.runTaskLater(UHC.getInstance(), 10L);

                                new BukkitRunnable() {
                                    public void run() {
                                        Firework f = getGameManager().getWinner().getWorld().spawn(getGameManager().getWinner().getLocation(), Firework.class);

                                        FireworkMeta fm = f.getFireworkMeta();
                                        fm.addEffect(FireworkEffect.builder()
                                                .flicker(false)
                                                .trail(true)
                                                .with(FireworkEffect.Type.BURST)
                                                .withColor(Color.ORANGE)
                                                .withFade(Color.YELLOW)
                                                .build());
                                        fm.setPower(3);
                                        f.setFireworkMeta(fm);
                                    }
                                }.runTaskLater(UHC.getInstance(), 10L);

                                new BukkitRunnable() {
                                    public void run() {
                                        Firework f = getGameManager().getWinner().getWorld().spawn(getGameManager().getWinner().getLocation(), Firework.class);

                                        FireworkMeta fm = f.getFireworkMeta();
                                        fm.addEffect(FireworkEffect.builder()
                                                .flicker(false)
                                                .trail(true)
                                                .with(FireworkEffect.Type.BALL)
                                                .withColor(Color.ORANGE)
                                                .withFade(Color.YELLOW)
                                                .build());
                                        fm.setPower(2);
                                        f.setFireworkMeta(fm);
                                    }
                                }.runTaskLater(UHC.getInstance(), 15L);

                                new BukkitRunnable() {
                                    public void run() {
                                        Firework f = getGameManager().getWinner().getWorld().spawn(getGameManager().getWinner().getLocation(), Firework.class);

                                        FireworkMeta fm = f.getFireworkMeta();
                                        fm.addEffect(FireworkEffect.builder()
                                                .flicker(false)
                                                .trail(true)
                                                .with(FireworkEffect.Type.BALL)
                                                .withColor(Color.ORANGE)
                                                .withFade(Color.YELLOW)
                                                .build());
                                        fm.setPower(2);
                                        f.setFireworkMeta(fm);
                                    }
                                }.runTaskLater(UHC.getInstance(), 15L);

                                Bukkit.getPluginManager().callEvent(new GameEndEvent());
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 40L);

        borderManager = new BorderManager();
        /*Bukkit.getPluginManager().registerEvents(new GlassListener(borderManager), this);
        Bukkit.getPluginManager().registerEvents(new GlitchPreventListener(borderManager), this);*/

        combatLoggerManager = new CombatLoggerManager(this);
        visualiseHandler = new VisualiseHandler();
        Bukkit.getPluginManager().registerEvents(new WallBorderListener(this), this);

        UHC.getInstance().getWorldManager().createWorld("world");
        UHC.getInstance().getWorldManager().loadWorld(Bukkit.getWorld("world"), 2000, 1000);
        UHC.getInstance().getWorldManager().prepareSpawn();
        UHC.getInstance().getWorldManager().clearWater();

        Bukkit.getWorld("world");
        Bukkit.getWorld("world").setGameRuleValue("naturalRegeneration", "false");

        UHC.getInstance().getWorldManager().createNetherWorld();
        Bukkit.getWorld("world_nether").setGameRuleValue("naturalRegeneration", "false");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb world_nether set 1000 1000 0 0");

        new Aether(this, new UHCScoreboard(this));


        ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§6§lGolden Head");
        itemStack.setItemMeta(itemMeta);

        ShapedRecipe shapedRecipe = new ShapedRecipe(itemStack);
        shapedRecipe.shape("EEE", "ERE", "EEE");
        shapedRecipe.setIngredient('E', Material.GOLD_INGOT).setIngredient('R', Material.SKULL_ITEM, 3);

        ItemStack pedo = new ItemStack(Material.STONE, 1);
        ItemMeta pedoMeta = itemStack.getItemMeta();
        pedoMeta.setDisplayName("§cNice try!");
        pedo.setItemMeta(pedoMeta);

        ShapedRecipe pedoRecipe = new ShapedRecipe(itemStack);
        pedoRecipe.shape("EEE", "ERE", "EEE");
        pedoRecipe.setIngredient('E', Material.GOLD_BLOCK).setIngredient('R', Material.APPLE);


        Bukkit.getServer().addRecipe(shapedRecipe);
        Bukkit.getServer().addRecipe(pedoRecipe);

        /*Bukkit.getWorld("world_the_end").setGameRuleValue("naturalRegeneration", "false");*/

        Bukkit.getPluginCommand("list").setExecutor(new ListCommand());
        Bukkit.getPluginManager().registerEvents(new WaitingListener(), this);
        Bukkit.getPluginManager().registerEvents(new BarebonesListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathMessageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ConsumeListener(), this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new BowLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new RodLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new HasteyBoysListener(), this);
        Bukkit.getPluginManager().registerEvents(new CutCleanListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new NoFallListener(), this);
        Bukkit.getPluginManager().registerEvents(new TimeBombListener(), this);
        Bukkit.getPluginManager().registerEvents(new NetherScenarioListener(), this);
        Bukkit.getPluginManager().registerEvents(new DiamondLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new HorseLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new GoldLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new NoCleanListener(), this);
        Bukkit.getPluginManager().registerEvents(new AbsorptionLessListener(), this);
        Bukkit.getPluginManager().registerEvents(new ScatterListeners(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new StaffModeListener(), this);
        Bukkit.getPluginManager().registerEvents(new EndListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldGenerationListener(), this);
        Bukkit.getPluginManager().registerEvents(new ColdWeaponsListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShearsListener(), this);
        Bukkit.getPluginManager().registerEvents(new VanillaPlusListener(), this);
        Bukkit.getPluginManager().registerEvents(new DoubleOresListener(), this);
        Bukkit.getPluginManager().registerEvents(new TripleExperienceListener(), this);
        Bukkit.getPluginManager().registerEvents(new TimberListener(), this);
        Bukkit.getPluginManager().registerEvents(new NoEnchantsListener(), this);
        Bukkit.getPluginManager().registerEvents(new TeamListeners(), this);
        Bukkit.getPluginManager().registerEvents(new ScreensharedListener(), this);
        Bukkit.getPluginManager().registerEvents(new FoundOresListener(), this);
        Bukkit.getPluginManager().registerEvents(new LimitedEnchantsListener(), this);
        Bukkit.getPluginManager().registerEvents(new BloodDiamonds(), this);
        Bukkit.getPluginManager().registerEvents(new DeathmatchListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClimberScenario(), this);

        Bukkit.getPluginCommand("helpop").setExecutor(new HelpopCommand());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv load lobby");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv load deathmatch");

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

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

        mineManager = new MineManager();

        setCommands();
        addCommands();
        postCommands();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "borders 40 5 500 100");

        bungeeMessagerListener = new BungeeMessagerListener(this);
        Bukkit.getPluginManager().registerEvents(bungeeMessagerListener, this);

        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry entry) {
                if (entry.getType().equals("ban")) {
                    if (getGameManager().getGameState() == GameState.INGAME) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6Auto Respawn&7] ->"));

                        if (getGameManager().getKillNames().get(UUID.fromString(entry.getUuid())) != null) {
                            for (String a : getGameManager().getKillNames().get(UUID.fromString(entry.getUuid()))) {

                                OfflinePlayer offline = Bukkit.getOfflinePlayer(a);
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(" &7- &e" + offline.getName() + " has been revived."));
                                getGameManager().getKilledRevived().add(offline.getName());
                                offline.setWhitelisted(true);
                                getRedisMessagingHandler().sendMessage("uhc", "revived;" + offline.getName() + ";" + Bukkit.getOfflinePlayer(UUID.fromString(entry.getUuid())).getName());
                            }

                            if (getGameManager().getKillNames().get(UUID.fromString(entry.getUuid())).size() <= 0) {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7 - &eThe player has not killed anyone."));
                            } else {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&a&lPlayers has been revived because " + Bukkit.getOfflinePlayer(UUID.fromString(entry.getUuid())).getName() + " was banned for " + entry.getReason() + "."));
                            }
                        }
                    }
                }
            }
        });

        Bukkit.getWorld("lobby").getEntities().forEach(Entity::remove);

        VapeListener vapeListener = new VapeListener();
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "LOLIMAHCKER", vapeListener);
        this.getServer().getPluginManager().registerEvents(vapeListener, this);

        new HackerReviveTask().runTaskTimer(this, 0L, 20 * 3L);

        Bukkit.getWorld("lobby").setGameRuleValue("doDaylightCycle", "false");
        Bukkit.getWorld("lobby").setTime(0);

        Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sbsj"), 20 * 10L);

        new RedisDataTask().runTaskTimerAsynchronously(this, 20L, 20L);
    }

    @Override
    public void onDisable() {
        ProfileUtils.getInstance().getProfiles().values().forEach(profile -> {
            profile.setMatchKills(0);
            profile.setDead(false);
            profile.setDeathLocation("null");
            profile.setDeathInventory("null");
            profile.setDeathArmor("null");
        });

        ProfileUtils.getInstance().saveProfiles();
        DatabaseUtils.getInstance().getClient().close();

        JsonObject object = new JsonObject();

        object.addProperty("server", Bukkit.getServerName());
        getRedisMessagingHandler().publish("uhc:end", object.toString());

        getRedisMessagingHandler().unsubscribe();
        pool.close();

        if (!getServerInfo().isWorldGenerated()) {
            try {
                Runtime.getRuntime().exec("rm -r /home/" + Bukkit.getServerName() + "/world");
                Runtime.getRuntime().exec("rm -r /home/" + Bukkit.getServerName() + "/world_nether");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setCommands() {
        commands = new LinkedList<>();

        try {
            if (Bukkit.getServer() instanceof CraftServer) {
                Field field = CraftServer.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                commandMap = (CommandMap) field.get(Bukkit.getServer());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void postCommands() {
        for (BaseCommand command : commands) {
            commandMap.register(command.getCommand(), command);
        }
    }

    private void addCommands() {
        commands.add(new VipWhitelistCommand());
        commands.add(new SitCommand());
        commands.add(new HealthCommand());
        commands.add(new KillTopCommand());
        commands.add(new ReviveCommand());
        commands.add(new ScenarioCommand());
        commands.add(new HostCommand());
        commands.add(new HelperCommand());
        commands.add(new SilexChampionsCommand());
        commands.add(new ModeratorCommand());
        commands.add(new TeamCommand());
        commands.add(new BorderCommand());
        commands.add(new ConfigCommand());
        commands.add(new ForceStartCommand());
        commands.add(new ListCommandsCommand());
        commands.add(new BorderShrinkCommand());
        commands.add(new PracticeCommand());
        commands.add(new SendCoordinatesCommand());
        commands.add(new GiveAllCommand());
        commands.add(new BackpackCommand());
        commands.add(new TeamListCommand());
        commands.add(new CheckUniqueIDCommand());
        commands.add(new TeamChatCommand());
        commands.add(new TeleCommand());
        commands.add(new SkinCommand());
        commands.add(new ToggleSpectatorsCommand());
        commands.add(new AlertsCommand());
        commands.add(new SetSlotsCommand());
        commands.add(new PluginsCommand());
        commands.add(new NightVisionCommand());
        commands.add(new ScreenshareCommand());
        commands.add(new ReScatterCommand());
        //commands.add(new StaffChatCommand());
        commands.add(new DiamondTopCommand());
        commands.add(new LateJoinCommand());
        commands.add(new DatabaseCommand());
        commands.add(new EventGameWhitelist());
        commands.add(new MuteChatSpec());
        commands.add(new HideCommand());
        commands.add(new KillsCommand());
        commands.add(new TruceCommand());
        commands.add(new ReportCommand());
        commands.add(new RuletaCommand());
        commands.add(new SuspendCommand());
        commands.add(new CombatLoggerCommand());
        commands.add(new SquareCommand());
        commands.add(new SetCommand());
        commands.add(new GameWhitelistCommand());
        commands.add(new LagCommand());
        commands.add(new WorldLoaderCommand());

        // Bukkit.getPluginCommand("message").setExecutor(new MessageCommand());
        // Bukkit.getPluginCommand("reply").setExecutor(new ReplyCommand());
    }

    private void setScenarios() {
        Scenario cutClean = new Scenario("Cutclean", "CC", new ItemStack(Material.IRON_INGOT), "All the ores are smelted, all the food is cooked, gravel always drop flint, chickens always drop feathers, cows always drop leather");
        Scenario timeBomb = new Scenario("Timebomb", "TB", new ItemStack(Material.TNT), "Your items will be stored in a chest when you die, this chest will explode in 30 seconds");
        Scenario bowLess = new Scenario("Bowless", "BLess", new ItemStack(Material.BOW), "You are not allowed to use bows, you are not allowed to craft bows");
        Scenario rodLess = new Scenario("Rodless", "RLess", new ItemStack(Material.FISHING_ROD), "You are not allowed to use rods, you are not allowed to craft rods");
        Scenario hasteyBoys = new Scenario("HasteyBoys", "HBoys", new ItemStack(Material.ENCHANTED_BOOK, 1), "Tools will have efficiency 3 when crafted");
        Scenario fireLess = new Scenario("Fireless", "FLess", new ItemStack(Material.FIRE), "You can't take damage from fire");
        Scenario noFall = new Scenario("NoFall", "NFall", new ItemStack(Material.FEATHER), "You can't take fall damage");
        Scenario nether = new Scenario("Nether", "Nether", new ItemStack(Material.PORTAL), "You can teleport to the nether");
        Scenario horseLess = new Scenario("Horseless", "HLess", new ItemStack(Material.SADDLE), "You are not allowed to ride horses");
        Scenario diamondLess = new Scenario("Diamondless", "DiaLess", new ItemStack(Material.DIAMOND), "Mined diamonds will be transformed to air");
        Scenario goldLess = new Scenario("GoldLess", "GoldLess", new ItemStack(Material.GOLD_INGOT), "Mined gold will be transformed to air");
        Scenario noClean = new Scenario("NoClean", "NClean", new ItemStack(Material.SKULL_ITEM), "The killer of a player gets immunity for 20 seconds, The immunity will be loss if you punch anyone");
        Scenario pushBack = new Scenario("Event Game", "EventGame", new ItemStack(Material.WATCH), "This game is part of an event.");
        Scenario absorptionLess = new Scenario("AbsorptionLess", "AbsortLess", new ItemStack(Material.GOLDEN_APPLE), "Your absorption will be removed after eating a golden apple");
        Scenario coldWeapons = new Scenario("ColdWeapons", "CWeapons", new ItemStack(Material.SNOW_BALL), "You are not allowed to have flame or fire aspect");
        Scenario end = new Scenario("End", "End", new ItemStack(Material.ENDER_PORTAL_FRAME), "You can teleport to the end");
        Scenario bareBones = new Scenario("BareBones", "BBones", new ItemStack(Material.BONE), "The nether is disabled, you can't craft enchantment tables, anvils or golden apples, any ore will drop iron and when a player dies, they will drop 1 diamond, 1 golden apple, 32 arrows and 2 string");
        Scenario buildUHC = new Scenario("BuildUHC", "BuildUHC", new ItemStack(Material.LAVA_BUCKET), "All the players will get a kit");
        Scenario doubleOres = new Scenario("DoubleOres", "2x Ores", new ItemStack(Material.DIAMOND, 2), "Ores drops are 2x");
        Scenario vanillaPlus = new Scenario("Vanilla+", "V+", new ItemStack(Material.FLINT), "Flint drops higher and apple drops are higher");
        Scenario backPacks = new Scenario("BackPacks", "BP", new ItemStack(Material.ENDER_CHEST), "Your team has a inventory to save items");
        Scenario noEnchants = new Scenario("NoEnchants", "NEnchants", new ItemStack(Material.ENCHANTMENT_TABLE), "You can't enchant anything");
        Scenario timber = new Scenario("Timber", "Timber", new ItemStack(Material.LEAVES), "When you mine a tree all the wood goes to the floor!");
        Scenario tripleExp = new Scenario("Triple Experience", "3x Exp", new ItemStack(Material.EXP_BOTTLE), "All the experience is the triple");
        Scenario rush = new Scenario("Rush", "Rush", new ItemBuilder(Material.POTION).setDurability(8194).build(), "PvPTime: " + getConfigurator().getIntegerOption("PVPTIME").getValue() + "m, HealTime: " + getConfigurator().getIntegerOption("HEALTIME").getValue() + "m, Border: 20m");
        Scenario statLess = new Scenario("Statless", "Statless", new ItemBuilder(Material.BOOK).build(), "Your statistics will not be saved in the database");
        Scenario limitEnchants = new Scenario("Limited Enchants", "LimitEnch", new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DAMAGE_ALL).build(), "The max enchant you can get is sharpness 1, power 1 and protection 1");
        Scenario bloodDiamonds = new Scenario("Blood Diamonds", "BD", new ItemStack(Material.REDSTONE), "When you mine a diamond your max health will be affected by half a heart");
        Scenario hypixelHeads = new Scenario("Hypixel Heads", "HyHeads", new ItemBuilder(Material.SKULL_ITEM).setOwner("JavaEE").build(), "When you right click a head you will get Regeneration III for 4 seconds and Speed II for 20 seconds");
        Scenario climber = new Scenario("Climber", "CL", new ItemBuilder(Material.TRIPWIRE_HOOK).build(), "When you look at a block and hit it with the rod, you get pulled to there");
        Scenario luckyLeaves = new Scenario("Lucky Leaves", "LL", new ItemStack(Material.GOLDEN_APPLE), "Trees drops golden apples with " + UHC.getInstance().getConfigurator().getIntegerOption("LUCKYLEAVES").getValue() + "%!");
        Scenario bedDeath = new Scenario("Bed Death", "BD", new ItemStack(Material.BED), "You will appear death in the ground when you die.");
        Scenario weekend = new Scenario("Weekend Event", "Weekend Event", new ItemStack(Material.RED_ROSE), "This game is part of an event.");

        scenarios.add(cutClean);
        scenarios.add(timeBomb);
        scenarios.add(bowLess);
        scenarios.add(rodLess);
        scenarios.add(hasteyBoys);
        scenarios.add(fireLess);
        scenarios.add(noFall);
        scenarios.add(nether);
        scenarios.add(horseLess);
        scenarios.add(diamondLess);
        scenarios.add(goldLess);
        scenarios.add(noClean);
        scenarios.add(absorptionLess);
        scenarios.add(coldWeapons);
        scenarios.add(end);
        scenarios.add(weekend);
        scenarios.add(bareBones);
        scenarios.add(buildUHC);
        scenarios.add(doubleOres);
        scenarios.add(vanillaPlus);
        scenarios.add(backPacks);
        scenarios.add(pushBack);
        scenarios.add(noEnchants);
        scenarios.add(timber);
        scenarios.add(tripleExp);
        scenarios.add(rush);
        scenarios.add(statLess);
        scenarios.add(limitEnchants);
        scenarios.add(bloodDiamonds);
        scenarios.add(hypixelHeads);
        scenarios.add(climber);
        scenarios.add(luckyLeaves);
        scenarios.add(bedDeath);
    }

    public enum CONFIG_OPTIONS {
        DEBUFFS, FRIENDLYFIRE, LUCKYLEAVES, RANDOMBORDER, ENDERPEARLDAMAGE, DEATHKICK, BED, PRACTICE, VIPJOIN, CANJOIN, DEBUG, RADIUS, HEALTIME, PVPTIME, TEAMSIZE, MAPGENERATED, APPLERATE, SHEARS, STATLESS, STRENGTH, SPEED, STRENGTHLVL, SPEEDLVL
    }

    private void createSoloConfigurator() {
        this.configurator = new Configurator();

        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.PVPTIME.name(), ChatColor.GREEN + "PvP Time", 20, false, 0, 50);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.HEALTIME.name(), ChatColor.GREEN + "Heal Time", 10, false, 0, 50);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.RADIUS.name(), ChatColor.GREEN + "World Radius", 2000, true, 100, 5000);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.TEAMSIZE.name(), ChatColor.GREEN + "Team Size", 0, false, 0, 50);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.MAPGENERATED.name(), ChatColor.GREEN + "Generated Map", false, false);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.APPLERATE.name(), ChatColor.GREEN + "Apple Rate", 1, false, 1, 100);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.SHEARS.name(), ChatColor.GREEN + "Shears", true, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.STATLESS.name(), ChatColor.GREEN + "Statless", false, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.STRENGTH.name(), ChatColor.GREEN + "Strength", false, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.SPEED.name(), ChatColor.GREEN + "Speed", false, false);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.STRENGTHLVL.name(), ChatColor.GREEN + "Strength Amplifier", 1, true, 1, 2);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.SPEEDLVL.name(), ChatColor.GREEN + "Speed Amplifier", 1, true, 1, 2);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.DEBUFFS.name(), ChatColor.GREEN + "Debuffs", false, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.DEBUG.name(), ChatColor.GREEN + "Debug", false, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.CANJOIN.name(), ChatColor.GREEN + "Can Join", false, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.VIPJOIN.name(), ChatColor.GREEN + "Vip Join", false, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.PRACTICE.name(), ChatColor.GREEN + "Practice", true, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.BED.name(), ChatColor.GREEN + "Bomb beds", false, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.DEATHKICK.name(), ChatColor.GREEN + "Death kick", true, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.ENDERPEARLDAMAGE.name(), ChatColor.GREEN + "Enderpearl Damage", true, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.RANDOMBORDER.name(), ChatColor.GREEN + "Random 100x100 Border", false, false);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.FRIENDLYFIRE.name(), ChatColor.GREEN + "Friendly Fire", true, true);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.LUCKYLEAVES.name(), ChatColor.GREEN + "Lucky Leaves", 1, true, 1, 100);
    }
}
