package me.javaee.uhc.managers;

import de.inventivegames.hologram.Hologram;
import de.inventivegames.hologram.HologramAPI;
import lombok.Getter;
import lombok.Setter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.events.BorderShrinkSetEvent;
import me.javaee.uhc.events.GameStartEvent;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.tasks.*;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.Configurator;
import me.javaee.uhc.utils.ItemBuilder;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
@Getter
public class GameManager {
    @Setter private Player host = null;
    @Setter private Player winner = null;
    private List<String> whitelistedPlayers = new ArrayList<>();
    private GameState gameState;
    private ArrayList<Player> moderators = new ArrayList<>();
    private ArrayList<Player> helpers = new ArrayList<>();
    private boolean hasCountdownStart;
    private Integer countDownTask;
    private ArrayList<UUID> alivePlayers = new ArrayList<>();
    private int maxTeamSize = 0;
    private Map<UUID, Integer> kills = new TreeMap<>();
    private Map<UUID, Integer> diamonds = new TreeMap<>();
    private Map<UUID, ArrayList<String>> killNames = new HashMap<>();
    private List<String> killedRevived = new ArrayList<>();
    private List<String> suspendedPlayers = new ArrayList<>();
    int contador = 0;

    private List<UUID> joined = new ArrayList<>();

    @Setter
    private Integer countdown = 60;
    @Setter
    private int ultimaCount;
    @Setter
    private int joinedPlayers = 0;
    @Setter
    private int currentRadius2 = 2000;

    public void startCountdown() {
        this.hasCountdownStart = true;
        ultimaCount = countdown;
        countDownTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(UHC.getInstance(), () -> {
            countdown -= 1;
            if (countdown == ultimaCount - 1) {
                Bukkit.setWhitelist(true);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (UHC.getInstance().getGameManager().getHelpers().contains(player) || UHC.getInstance().getGameManager().getModerators().contains(player) || UHC.getInstance().getGameManager().getHost() == player) {
                        UHC.getInstance().getStaffModeManager().setStaffMode(player);
                    } else {
                        alivePlayers.add(player.getUniqueId());

                        kills.put(player.getUniqueId(), 0);
                        diamonds.put(player.getUniqueId(), 0);
                        killNames.put(player.getUniqueId(), new ArrayList<>());
                    }
                }

                scatterPlayers();
            }

            if (countdown < 6 && countdown > 0) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6The game will start in &f" + countdown + " seconds&6."));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.NOTE_PLING, 10F, 10F));
            }

            if (countdown < 1) {
                endCountdown();

                for (Player player : getModerators()) {
                    UHC.getInstance().getStaffModeManager().vanishFromAll(player);
                }

                for (Player player : getHelpers()) {
                    UHC.getInstance().getStaffModeManager().vanishFromAll(player);
                }

                if (getHost() != null) {
                    UHC.getInstance().getStaffModeManager().vanishFromAll(getHost());
                }

                Bukkit.getOnlinePlayers().forEach(online -> {
                    getModerators().forEach(mods -> {
                        if (getModerators().contains(online)) {
                            online.showPlayer(mods);
                            mods.showPlayer(online);
                        }

                        if (getHelpers().contains(online)) {
                            online.showPlayer(mods);
                            mods.showPlayer(online);
                        }

                        if (getHost() != null) {
                            getHost().showPlayer(mods);
                        }
                    });
                });
            }
        }, 0, 20L);
    }

    public void cancelTask() {
        Bukkit.getServer().getScheduler().cancelTask(countDownTask);

        this.countDownTask = 0;
    }

    public void scatterPlayers() {
        int teamSize = UHC.getInstance().getConfigurator().getIntegerOption(UHC.CONFIG_OPTIONS.TEAMSIZE.name()).getValue();

        new BukkitRunnable() {
            public void run() {
                if (teamSize <= 1) {
                    Player player = Bukkit.getPlayer(getAlivePlayers().get(contador));

                    if (player != null) {
                        player.teleport(UHC.getInstance().getGenerateSpawnsCommandHandler().scatterPoints.get(contador));
                        mountPlayerIntoEntity(player);

                        for (Player players : Bukkit.getOnlinePlayers()) {
                            players.showPlayer(player);
                        }
                    }
                    contador++;

                    if (contador >= getAlivePlayers().size()) {
                        cancel();
                    }
                } else {
                    if (teamSize >= 50) {
                        if (contador == UHC.getInstance().getTeams().size() - 1) {
                            cancel();
                        }

                        UHCTeam team = UHC.getInstance().getTeams().get(contador);

                        if (team.getName().equalsIgnoreCase("Red")) {
                            for (UUID player : team.getPlayerList()) {
                                if (Bukkit.getPlayer(player) != null) {
                                    Bukkit.getPlayer(player).teleport(new Location(Bukkit.getWorld("world"), -1000, Bukkit.getWorld("world").getHighestBlockYAt(-1000, -1000), -1000));

                                    mountPlayerIntoEntity(Bukkit.getPlayer(player));

                                    for (Player players : Bukkit.getOnlinePlayers()) {
                                        players.showPlayer(Bukkit.getPlayer(player));
                                    }

                                    team.setDtr(team.getDtr() + 1);
                                }
                            }
                        } else if (team.getName().equalsIgnoreCase("Blue")) {
                            for (UUID player : team.getPlayerList()) {
                                if (Bukkit.getPlayer(player) != null) {
                                    Bukkit.getPlayer(player).teleport(new Location(Bukkit.getWorld("world"), 1000, Bukkit.getWorld("world").getHighestBlockYAt(1000, 1000), 1000));

                                    mountPlayerIntoEntity(Bukkit.getPlayer(player));

                                    for (Player players : Bukkit.getOnlinePlayers()) {
                                        players.showPlayer(Bukkit.getPlayer(player));
                                    }

                                    team.setDtr(team.getDtr() + 1);
                                }
                            }
                        }

                        contador++;
                        return;
                    }

                    if (contador == UHC.getInstance().getTeams().size() - 1) {
                        cancel();
                    }

                    UHCTeam team = UHC.getInstance().getTeams().get(contador);

                    for (UUID player : team.getPlayerList()) {
                        if (Bukkit.getPlayer(player) != null) {
                            Bukkit.getPlayer(player).teleport(UHC.getInstance().getGenerateSpawnsCommandHandler().scatterPoints.get(contador));

                            mountPlayerIntoEntity(Bukkit.getPlayer(player));

                            for (Player players : Bukkit.getOnlinePlayers()) {
                                players.showPlayer(Bukkit.getPlayer(player));
                            }

                            team.setDtr(team.getDtr() + 1);
                        }
                    }

                    contador++;
                }
            }
        }.runTaskTimer(UHC.getInstance(), 3L, 3L);
    }

    public void endCountdown() {
        if (UHC.getInstance().getConfigurator().getIntegerOption("PVPTIME").getValue() == 20) {
            UHC.getInstance().getConfigurator().getIntegerOption("PVPTIME").setValue(20);
        }

        Boolean statLess = (Boolean) UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.STATLESS.name()).getValue();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equalsIgnoreCase("lobby") || player.getWorld().getName().equalsIgnoreCase("world_the_end")) {
                Random r = new Random();
                int low = 1;
                int high = 350;
                int result = r.nextInt(high - low) + low;

                player.teleport(UHC.getInstance().getGenerateSpawnsCommandHandler().scatterPoints.get(result));
            }

            player.setStatistic(Statistic.PLAYER_KILLS, 0);

            Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

            if (UHC.getInstance().getGameManager().getHelpers().contains(player) ||UHC.getInstance().getGameManager().getModerators().contains(player) || UHC.getInstance().getGameManager().getHost() == player) {
                player.setWhitelisted(true);
                player.setHealth(20);
                player.setFoodLevel(20);

                profile.setTotalGames(profile.getTotalGames() + 1);
                profile.setLateScattered(true);
            } else {
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);

                if (Scenario.getByName("BuildUHC").isEnabled()) {
                    Inventory inv = player.getInventory();

                    ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE, 6);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName("§6§lGolden Head");
                    itemStack.setItemMeta(itemMeta);

                    inv.setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, 1).build());
                    inv.setItem(1, new ItemStack(Material.FISHING_ROD));
                    inv.setItem(2, new ItemStack(Material.LAVA_BUCKET));
                    inv.setItem(29, new ItemStack(Material.LAVA_BUCKET));
                    inv.setItem(3, new ItemStack(Material.COBBLESTONE, 64));
                    inv.setItem(4, new ItemStack(Material.WATER_BUCKET));
                    inv.setItem(31, new ItemStack(Material.WATER_BUCKET));
                    inv.setItem(6, new ItemStack(Material.GOLDEN_APPLE, 16));
                    inv.setItem(5, itemStack);
                    inv.setItem(31, new ItemStack(Material.COBBLESTONE, 64));
                    inv.setItem(32, new ItemStack(Material.COOKED_BEEF, 16));
                    inv.setItem(7, new ItemStack(Material.DIAMOND_PICKAXE, 1));
                    inv.setItem(8, new ItemStack(Material.BOW));
                    inv.setItem(17, new ItemStack(Material.ARROW, 64));

                    player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                    player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                } else {
                    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
                }

                player.setWhitelisted(true);
                player.setHealth(20);
                player.setFoodLevel(20);

                if (!statLess) {
                    profile.setTotalGames(profile.getTotalGames() + 1);
                }
                profile.setLateScattered(true);
            }

            player.removePotionEffect(PotionEffectType.JUMP);
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        }
        cancelTask();
        hasCountdownStart = false;
        setJoinedPlayers(Bukkit.getOnlinePlayers().size() - UHC.getInstance().getStaffModeManager().getStaffModeList().size());
        new GameTimeTask().runTaskTimer(UHC.getInstance(), 20, 20);
        new BroadcastCommandsTask().runTaskTimerAsynchronously(UHC.getInstance(), 30 * 60, 30 * 60 * 2);

        Configurator.Option heal = UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.HEALTIME.name());
        Configurator.Option pvp = UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.PVPTIME.name());

        if (heal.getValue() != null && (int) heal.getValue() > 0) {
            new FinalHealTask().runTaskTimer(UHC.getInstance(), 0L, 20L);
        }

        if (pvp.getValue() != null && (int) pvp.getValue() > 0) {
            new ProtectionTask().runTaskTimer(UHC.getInstance(), 0L, 20L);
        }

        if (UHC.getInstance().getBorderShrink() != null && UHC.getInstance().getBorderShrink()) {
            BorderShrinkSetEvent event = new BorderShrinkSetEvent();
            UHC.getInstance().getServer().getPluginManager().callEvent(event);

            int startTime = UHC.getInstance().getBorderShrinkTask().startTime;
            UHC.getInstance().getBorderShrinkTask().runTaskTimer(UHC.getInstance(), startTime * 1200L, 20L);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            joined.add(player.getUniqueId());

            if (moderators.contains(player) || host == player) {
                player.setAllowFlight(true);
            } else {
                player.setAllowFlight(false);
            }

            if (villagers.containsKey(player)) {
                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(villagers.get(player));

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setHealth(20);
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                }

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6Participants have been healed."));
            }
        }.runTaskLater(UHC.getInstance(), 20L * 5);

        for (UHCTeam teams : UHC.getInstance().getTeams()) {
            for (UUID uuid : teams.getPlayerList()) {
                if (!joined.contains(uuid)) {
                    teams.setDtr(teams.getDtr() - 1);
                }
            }
        }

        UHC.getInstance().setGameStarted(true);
        setGameState(GameState.INGAME);

        GameStartEvent event = new GameStartEvent();
        Bukkit.getPluginManager().callEvent(event);

        UHC.getInstance().getServerInfo().setLastMatchName(UHC.getInstance().getUhcNumber());
        UHC.getInstance().getServerInfo().save();

        Bukkit.getOnlinePlayers().forEach(player -> {
            UHC.getInstance().getNpcs().forEach(npc -> {
                if (npc != null) {
                    npc.remove();
                }
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) npc).getHandle()));
            });
        });

        for (Hologram hologram : HologramAPI.getHolograms()) {
            hologram.despawn();
        }

        UHC.getInstance().getNpcs().clear();

        new CavesTask().runTaskTimer(UHC.getInstance(), 50L, 50L);
        Bukkit.getWorld("world").setTime(0);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    private HashMap<Player, Integer> villagers = new HashMap<>();

    public void mountPlayerIntoEntity(Player player) {
        WorldServer worldServer = ((CraftWorld) player.getLocation().getWorld()).getHandle();
        EntityBat bat = new EntityBat(worldServer);

        bat.setLocation(player.getLocation().getX() + 0.5, player.getLocation().getY() + 2, player.getLocation().getZ() + 0.5, 0, 0);
        bat.setHealth(bat.getMaxHealth());
        bat.setInvisible(true);
        bat.d(0);
        bat.setAsleep(true);
        bat.setAirTicks(10);
        bat.setSneaking(false);

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(bat);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity(0, ((CraftPlayer) player).getHandle(), bat);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(attach);

        villagers.put(player, bat.getId());
    }

    public boolean isPvpEnable() {
        return UHC.getInstance().getGameTimeTask().getSeconds() > (UHC.getInstance().getConfigurator().getIntegerOption("PVPTIME").getValue() * 60);
    }

    public void setTimeBombCounter(Location location) {
       /* final Integer[] counter = {30};

        Hologram hologram = HologramAPI.createHologram(location.add(0.5, 1.5, 0.5), ChatColor.GREEN.toString() + counter[0]);
        hologram.spawn();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (counter[0] > 19) {
                    hologram.setText(ChatColor.GREEN.toString() + counter[0]);
                } else if (counter[0] < 20 && counter[0] > 10) {
                    hologram.setText(ChatColor.YELLOW.toString() + counter[0]);
                } else if (counter[0] <= 10 && counter[0] > 5) {
                    hologram.setText(ChatColor.RED.toString() + counter[0]);
                } else if (counter[0] <= 5 && counter[0] > 0) {
                    hologram.setText(ChatColor.DARK_RED.toString() + counter[0]);
                } else if (counter[0] < 1) {
                    hologram.setText("");
                    hologram.despawn();
                    cancel();
                }

                counter[0]--;
            }
        }.runTaskTimer(UHC.getInstance(), 0L, 20L);*/
    }
}
