package me.javaee.uhc.listeners.misc;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitServerInterface;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import me.javaee.uhc.UHC;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.events.GameEndEvent;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.Configurator;
import me.javaee.uhc.utils.FakePlayer;
import me.javaee.uhc.utils.InventorySerialization;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */

public class EndListener implements Listener {
    private DoubleChestInventory lastDeathInventory;

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            Player player = event.getEntity();
            Configurator.Option teamSize = UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.TEAMSIZE.name());
            Boolean statLess = (Boolean) UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.STATLESS.name()).getValue();

            if (Scenario.getByName("Bed Death").isEnabled()) {
                FakePlayer fakePlayer = new FakePlayer(player, player.getLocation());
                fakePlayer.setSleeping(player.getLocation());
            }

            Profile playerProfile = ProfileUtils.getInstance().getProfile(player.getUniqueId());
            if (UHCTeam.getByUUID(player.getUniqueId()) != null) {
                UHCTeam.getByUUID(player.getUniqueId()).setDtr(UHCTeam.getByUUID(player.getUniqueId()).getDtr() - 1);

                if (UHCTeam.getByUUID(player.getUniqueId()).getDtr() < 1) {
                    UHC.getInstance().getTeams().remove(UHCTeam.getByUUID(player.getUniqueId()));
                }
            }

            InventorySerialization.saveInventoryToProfile(playerProfile, player.getInventory().getContents());
            InventorySerialization.saveArmorToProfile(playerProfile, player.getInventory().getArmorContents());
            playerProfile.setDeathLocation(player.getLocation().getX() + ";" + player.getLocation().getZ());
            playerProfile.setDead(true);
            playerProfile.save(true);

            if (!Scenario.getByName("TimeBomb").isEnabled()) {
                player.getWorld().getBlockAt(player.getLocation()).setType(Material.NETHER_FENCE);
                player.getWorld().getBlockAt(player.getLocation().add(0.0D, 1.0D, 0.0D)).setType(Material.SKULL);

                Block block = player.getWorld().getBlockAt(player.getLocation().add(0.0D, 1.0D, 0.0D));
                block.setData((byte) 1);

                BlockState blockState = player.getWorld().getBlockAt(player.getLocation().add(0.0D, 1.0D, 0.0D)).getState();
                if (blockState instanceof Skull) {
                    Skull playerSkull = (Skull) blockState;
                    playerSkull.setSkullType(SkullType.PLAYER);
                    playerSkull.setOwner(player.getName());
                    playerSkull.update();
                }

                player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(player.getLevel() * 7);
            } else {
                player.setVelocity(new Vector(0, 0, 0));
                player.getLocation().getBlock().setType(Material.CHEST);
                Location loc = player.getLocation();
                Location timeLoc = loc;
                player.getLocation().add(0, 0, 1).getBlock().setType(Material.CHEST);

                player.getLocation().add(0, 1, 1).getBlock().setType(Material.AIR);
                player.getLocation().add(0, 1, 1).getBlock().setType(Material.AIR);

                // Put items in chest
                Chest chest = (Chest) player.getLocation().getBlock().getState();
                if (chest.getInventory().getHolder() instanceof DoubleChest) {
                    DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
                    DoubleChestInventory doubleChestInventory = (DoubleChestInventory) doubleChest.getInventory();

                    for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                        if (itemStack == null || itemStack.getType() == Material.AIR) {
                            continue;
                        }

                        doubleChestInventory.addItem(itemStack);
                    }

                    for (ItemStack itemStack : player.getInventory().getContents()) {
                        if (itemStack == null || itemStack.getType() == Material.AIR) {
                            continue;
                        }
                        doubleChestInventory.addItem(itemStack);
                    }
                    this.lastDeathInventory = doubleChestInventory; // Store for later use

                    if (Scenario.getByName("Barebones").isEnabled()) {
                        doubleChestInventory.addItem(new ItemStack(Material.DIAMOND, 1));
                        doubleChestInventory.addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
                        doubleChestInventory.addItem(new ItemBuilder(Material.GOLDEN_APPLE).setName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Golden Head").build());
                        doubleChestInventory.addItem(new ItemStack(Material.ARROW, 32));
                        doubleChestInventory.addItem(new ItemStack(Material.STRING, 2));
                    }

                    if (Scenario.getByName("Diamondless").isEnabled()) {
                        doubleChestInventory.addItem(new ItemStack(Material.DIAMOND, 1));
                    }

                    if (Scenario.getByName("Goldless").isEnabled()) {
                        doubleChestInventory.addItem(new ItemStack(Material.GOLD_INGOT, 12));
                        doubleChestInventory.addItem(new ItemBuilder(Material.GOLDEN_APPLE).setName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Golden Head").build());
                    }
                }

                this.lastDeathInventory.addItem(createGoldenHead());
                player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(player.getLevel() * 7);

                if (!loc.getWorld().getName().equalsIgnoreCase("Deathmatch")) {
                    new TimeBomb(player.getName(), player.getLocation()).runTaskLater(UHC.getInstance(), 20L * 30);
                    UHC.getInstance().getGameManager().setTimeBombCounter(timeLoc);
                }


                event.getDrops().clear();
            }

            if (!statLess) {
                playerProfile.setDeaths(playerProfile.getDeaths() + 1);
            }

            UHC.getInstance().getGameManager().getAlivePlayers().remove(player.getUniqueId());

            if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
                Player killerP = event.getEntity().getKiller();

                UHC.getInstance().getGameManager().getKills().put(killerP.getUniqueId(), UHC.getInstance().getGameManager().getKills().get(killerP.getUniqueId()) + 1);

                if (!statLess) {
                    Profile killerProfile = ProfileUtils.getInstance().getProfile(event.getEntity().getKiller().getUniqueId());

                    killerProfile.setKills(killerProfile.getKills() + 1);
                    killerProfile.setMatchKills(killerProfile.getMatchKills() + 1);
                    //killerProfile.getKillList().add(player.getUniqueId().toString());

                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                    player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------");
                    player.sendMessage(ChatColor.YELLOW + "You were killed by " + ChatColor.AQUA + killerP.getName());
                    player.sendMessage("");
                    player.sendMessage(ChatColor.translateAlternateColorCodes("&eKiller Information&7:"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes(" &7- &eHealth&7: &b" + decimalFormat.format(killerP.getHealth())));
                    player.sendMessage(ChatColor.translateAlternateColorCodes(" &7- &eKills&7: &b" + killerProfile.getMatchKills()));
                    player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------");
                } else {
                    Profile killerProfile = ProfileUtils.getInstance().getProfile(event.getEntity().getKiller().getUniqueId());

                    killerProfile.setMatchKills(killerProfile.getMatchKills() + 1);
                    //killerProfile.getKillList().add(player.getUniqueId().toString());

                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                    player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------");
                    player.sendMessage(ChatColor.YELLOW + "You were killed by " + ChatColor.AQUA + killerP.getName());
                    player.sendMessage("");
                    player.sendMessage(ChatColor.translateAlternateColorCodes("&eKiller Information&7:"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes(" &7- &eHealth&7: &b" + decimalFormat.format(killerP.getHealth())));
                    player.sendMessage(ChatColor.translateAlternateColorCodes(" &7- &eKills&7: &b" + killerProfile.getMatchKills()));
                    player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------");
                }

                if (teamSize.getValue() != null || (int) teamSize.getValue() >= 2) {
                    if (UHCTeam.getByUUID(killerP.getUniqueId()) != null) {
                        UHCTeam.getByUUID(killerP.getUniqueId()).setKills(UHCTeam.getByUUID(killerP.getUniqueId()).getKills() + 1);
                    }
                }

                UHC.getInstance().getGameManager().getKillNames().get(killerP.getUniqueId()).add(player.getName());
            }

            if (UHC.getInstance().getConfigurator().getBooleanOption("DEATHKICK").getValue()) {
                if (!player.hasPermission("rank.vip")) {
                    player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "You will be kicked from the server in 1 minute. We recommend you reporting if you died by a hacker. In case the hacker gets banned and you are in the server, you will be sent a message saying that you have been revived.");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.sendMessage(ChatColor.RED + "You have died in the uhc!");
                            player.kickPlayer("You have died in the uhc!");
                            player.setWhitelisted(false);
                        }
                    }.runTaskLater(UHC.getInstance(), 20 * 60);
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getGameManager().getKilledRevived().contains(event.getPlayer().getName())) {
            return;
        }

        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            UHC.getInstance().getSpectatorManager().setSpectator(player);

            for (Player spectator : UHC.getInstance().getSpectatorManager().getSpectators()) {
                if (!ProfileUtils.getInstance().getProfile(spectator.getUniqueId()).isSpectators()) {
                    spectator.hidePlayer(player);
                }
            }

            for (Player spectator : UHC.getInstance().getGameManager().getModerators()) {
                if (!ProfileUtils.getInstance().getProfile(spectator.getUniqueId()).isSpectators()) {
                    spectator.hidePlayer(player);
                }
            }

            for (Player spectator : UHC.getInstance().getGameManager().getHelpers()) {
                if (!ProfileUtils.getInstance().getProfile(spectator.getUniqueId()).isSpectators()) {
                    spectator.hidePlayer(player);
                }
            }

            if (UHC.getInstance().getGameManager().getHost() != null) {
                if (!ProfileUtils.getInstance().getProfile(UHC.getInstance().getGameManager().getHost().getUniqueId()).isSpectators()) {
                    UHC.getInstance().getGameManager().getHost().hidePlayer(player);
                }
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6Thanks you for playing a uhc hosted by the Silex Network!"));
        }

        if (UHC.getInstance().getGameManager().getGameState() == GameState.END) {
            UHC.getInstance().getSpectatorManager().setSpectator(player);

            for (Player spectator : UHC.getInstance().getSpectatorManager().getSpectators()) {
                if (!ProfileUtils.getInstance().getProfile(spectator.getUniqueId()).isSpectators()) {
                    spectator.hidePlayer(player);
                }
            }

            for (Player spectator : UHC.getInstance().getGameManager().getModerators()) {
                if (!ProfileUtils.getInstance().getProfile(spectator.getUniqueId()).isSpectators()) {
                    spectator.hidePlayer(player);
                }
            }

            if (UHC.getInstance().getGameManager().getHost() != null) {
                if (!ProfileUtils.getInstance().getProfile(UHC.getInstance().getGameManager().getHost().getUniqueId()).isSpectators()) {
                    UHC.getInstance().getGameManager().getHost().hidePlayer(player);
                }
            }
        }
    }

    public static ItemStack createGoldenHead() {
        ItemStack apple = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta appleMeta = apple.getItemMeta();
        appleMeta.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Golden Head");
        apple.setItemMeta(appleMeta);

        return apple;
    }

    private class TimeBomb extends BukkitRunnable {
        private Location location;
        private String name;

        public TimeBomb(String name, Location location) {
            this.name = name;
            this.location = location;
        }

        @Override
        public void run() {
            this.location.getWorld().spigot().strikeLightningEffect(this.location, true);
            this.location.getWorld().createExplosion(this.location, 8f);

            Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "TimeBomb" + ChatColor.GRAY + "] " + ChatColor.WHITE + this.name + ChatColor.GOLD + "'s corpse has exploded!");
        }

        @EventHandler
        public void onWeather(WeatherChangeEvent event) {
            if (event.toWeatherState()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEndEvent(GameEndEvent event) {
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            pasteSchematic("WIN", 0, 150, 0, "world");

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &6Winners will be tp'ed to the winner's pedestal in 15 seconds."));

            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                if (UHC.getInstance().getGameManager().getHost() != null) {
                    UHC.getInstance().getGameManager().getHost().teleport(new Location(Bukkit.getWorld("world"), 0, 152, 0));
                }

                UHC.getInstance().getGameManager().getAlivePlayers().forEach(uuid -> {
                    if (Bukkit.getPlayer(uuid) != null) {
                        Bukkit.getPlayer(uuid).teleport(new Location(Bukkit.getWorld("world"), 0, 152, 0));
                    }
                });
            }, 20 * 15);
        }, 20);
    }

    public void pasteSchematic(String schematicName, int x, int y, int z, String worldName) {
        File file = new File("plugins/WorldEdit/schematics/" + schematicName + ".schematic");
        if (file.exists() && !file.isDirectory()) {
            SchematicFormat format = SchematicFormat.getFormat(file);
            CuboidClipboard WEclipboard = null;
            try {
                WEclipboard = format.load(file);
            } catch (IOException | DataException e) {
                e.printStackTrace();
            }
            if (WEclipboard != null) {
                BukkitServerInterface WEinterface = new BukkitServerInterface((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit"), Bukkit.getServer());
                EditSession WEsessionEdit = null;
                for (LocalWorld WEworld : WEinterface.getWorlds()) {
                    if (WEworld.getName().equalsIgnoreCase(worldName)) {
                        WEsessionEdit = new EditSession(WEworld, -1);
                        break;
                    }
                }
                com.sk89q.worldedit.Vector WEvector = new com.sk89q.worldedit.Vector(x, y, z);

                try {
                    WEclipboard.paste(WEsessionEdit, WEvector, false);
                } catch (MaxChangedBlocksException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
