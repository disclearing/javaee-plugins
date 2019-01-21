package me.javaee.meetup.listeners;

import javafx.util.Pair;
import me.javaee.disguise.Disguise;
import me.javaee.meetup.Meetup;
import me.javaee.meetup.enums.GameState;
import me.javaee.meetup.events.GameStartEvent;
import me.javaee.meetup.handlers.Scenario;
import me.javaee.meetup.kit.Kit;
import me.javaee.meetup.managers.GameManager;
import me.javaee.meetup.menu.menu.ScenariosMenu;
import me.javaee.meetup.profile.Profile;
import me.javaee.meetup.profile.ProfileUtils;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.silexpvp.nightmare.util.LuckPermsUtils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;Golden
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(null);
        player.setMaximumNoDamageTicks(19);

        int randomNum = ThreadLocalRandom.current().nextInt(0, 99 + 1);

        if (Meetup.getPlugin().getGameManager().getGameState() == GameState.WAITING || Meetup.getPlugin().getGameManager().getGameState() == GameState.STARTING) {
            Location location = Meetup.getPlugin().getSpawnsHandler().getScatterPoints().get(randomNum);

            new BukkitRunnable() {
                public void run() {
                    player.teleport(location);
                    new ScenariosMenu(player).open(player);
                }
            }.runTaskLater(Meetup.getPlugin(), 8);

            if (Meetup.getPlugin().getGameManager().getGameState() == GameState.WAITING) {
                if (Bukkit.getOnlinePlayers().size() == 4) {
                    Meetup.getPlugin().getGameManager().startCountdown();
                    Meetup.getPlugin().getRedisPublisher().write("meetup;" + Bukkit.getServerName());
                }
            }

            Kit kit = Meetup.getPlugin().getKitManager().getKits().get(new Random().nextInt(19 - 1));
            player.getInventory().setContents(kit.getInventory());
            player.getInventory().setArmorContents(kit.getArmor());
            player.getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE));

            if (ProfileUtils.getInstance().getProfile(player.getUniqueId()).getRerolls() <= 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou don't have any rerolls."));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have " + ProfileUtils.getInstance().getProfile(player.getUniqueId()).getRerolls() + " rerolls. You can use them with '&7/reroll'&e."));
            }
        } else {
            Meetup.getPlugin().getSpectatorManager().setSpectator(player);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (Meetup.getPlugin().getGameManager().getGameState() == GameState.WAITING || Meetup.getPlugin().getGameManager().getGameState() == GameState.STARTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        event.setExpLevelCost(event.getExpLevelCost() - 50);
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        if (event.getItem().getItemMeta().getDisplayName() != null) {
            if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + ChatColor.BOLD.toString() + "Golden Head")) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1));
            }
        }
    }

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        if (Meetup.getPlugin().getGameManager().getGameState() == GameState.LOADING) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "We are loading the spawns.");
        }
    }

    @EventHandler
    public void onFire(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String prefix = LuckPermsUtils.getPrefix(player);

        if (String.valueOf(event.getMessage().charAt(0)).equalsIgnoreCase("!") && player.isOp()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + player.getName() + "&7: &f") + event.getMessage().replace("!", ""));
            }
            event.setCancelled(true);
            return;
        }

        String message = "%2$s";
        if (Meetup.getPlugin().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);

            if (Meetup.getPlugin().getGameManager().getGameState() == GameState.END) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3Spectator&7] &3" + player.getName() + "&7: ") + event.getMessage());
            } else {
                for (Player spectators : Meetup.getPlugin().getSpectatorManager().getSpectators()) {
                    spectators.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3Spectator&7] &3" + player.getName() + "&7: ") + event.getMessage());
                }
            }
        } else {
            if (Disguise.getInstance().isDisguised(player)) {
                event.setFormat(ChatColor.translateAlternateColorCodes('&', "&6" + player.getName() + "&7: &f") + message);
            } else {
                event.setFormat(ChatColor.translateAlternateColorCodes('&', prefix + player.getName() + "&7: &f") + message);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Location location = event.getPlayer().getLocation();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        ItemStack item = event.getItem();

        if (Meetup.getPlugin().getGameManager().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
        }

        if (item != null) {
            if (item.getItemMeta() != null) {
                if (item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + ChatColor.BOLD.toString() + "Horse")) {
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        event.setCancelled(true);

                        int percentage = (int) (Math.random() * 100);

                        LivingEntity entity = (LivingEntity) world.getWorld().spawnEntity(location, EntityType.HORSE);
                        ((Horse) entity).getInventory().setSaddle(new ItemStack(Material.SADDLE));
                        ((Horse) entity).setStyle(Horse.Style.WHITE);
                        ((Horse) entity).setVariant(Horse.Variant.HORSE);
                        ((Horse) entity).setAdult();
                        ((Horse) entity).setTamed(true);

                        event.getPlayer().setItemInHand(new ItemStack(Material.AIR));

                        if (percentage <= 50) {
                            ((Horse) entity).getInventory().setArmor(new ItemStack(Material.IRON_BARDING));
                        } else if (percentage <= 75 && percentage > 50) {
                            ((Horse) entity).getInventory().setArmor(new ItemStack(Material.GOLD_BARDING));
                        } else if (percentage > 75) {
                            ((Horse) entity).getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING));
                        }

                        entity.setPassenger(event.getPlayer());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (Meetup.getPlugin().getGameManager().getGameState() == GameState.END) {
            event.setCancelled(false);
            return;
        }

        if (Meetup.getPlugin().getGameManager().getGameState() == GameState.INGAME) {
            if (event.getBlock().getLocation().getY() > 90) {
                player.sendMessage(ChatColor.RED + "You can't place blocks here.");
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        if (Meetup.getPlugin().getGameManager().getGameState() == GameState.END) {
            event.setCancelled(false);
            return;
        }

        if (Meetup.getPlugin().getGameManager().getGameState() == GameState.INGAME) {
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().getName().equalsIgnoreCase("JavaEE")) {
            if (event.getMessage().equalsIgnoreCase("/start")) {
                Meetup.getPlugin().getGameManager().startCountdown();
                Meetup.getPlugin().getRedisPublisher().write("meetup;" + Bukkit.getServerName());
                Bukkit.getLogger().info("sent the redis message...");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);

        if (Meetup.getPlugin().getGameManager().getAlivePlayers().contains(player)) {
            Meetup.getPlugin().getGameManager().getAlivePlayers().remove(player);
        }
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        Meetup.getPlugin().getMounted().clear();

        int i = 0;
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', "&7&m----------------------")));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', "&6&lScenarios Enabled&7:")));
        Bukkit.broadcastMessage("");
        for (Scenario scenario : Meetup.getPlugin().getScenarios()) {
            if (scenario.getVotes() >= 5) {
                if (scenario.getName().equalsIgnoreCase("No Scenarios")) {
                    scenario.setEnabled(true);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&l" + (i + 1) + ". &e" + scenario.getName()));
                    return;
                }

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&l" + (i + 1) + ". &e" + scenario.getName()));
                scenario.setEnabled(true);
                i++;
            }
        }
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', "&7&m----------------------")));

        if (Scenario.getByName("OP").isEnabled()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Meetup.getPlugin().getGameManager().getAlivePlayers().contains(player)) {
                    for (ItemStack items : player.getInventory().getContents()) {
                        if (items != null) {
                            if (items.getEnchantments().containsKey(Enchantment.DAMAGE_ALL)) {
                                items.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, (items.getEnchantments().get(Enchantment.DAMAGE_ALL) + 2));
                            } else if (items.getEnchantments().containsKey(Enchantment.ARROW_DAMAGE)) {
                                items.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, (items.getEnchantments().get(Enchantment.ARROW_DAMAGE) + 2));
                            }
                        }
                    }

                    for (ItemStack items : player.getInventory().getArmorContents()) {
                        if (items != null) {
                            if (items.getEnchantments().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                                items.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, (items.getEnchantments().get(Enchantment.PROTECTION_ENVIRONMENTAL) + 2));
                            }
                        }
                    }
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

            profile.setTotalGames(profile.getTotalGames() + 1);
        }
    }

    @EventHandler
    public void onDismount(VehicleExitEvent event) {
        if (Meetup.getPlugin().getGameManager().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
        }
    }

    public void spawn(Location location, Player player) {
        LivingEntity entity = (LivingEntity) Bukkit.getWorld("world").spawnEntity(location, EntityType.VILLAGER);

        entity.setMaxHealth(10);
        entity.setPassenger(player);
        entity.setFallDistance(0);
        entity.setHealth(entity.getMaxHealth());
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 125));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 10));

        player.setHealth(20);
    }

    private DoubleChestInventory lastDeathInventory;

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Entity killer = player.getKiller();

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

            player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.EXP_BOTTLE, 16));
        } else {
            player.getLocation().getBlock().setType(Material.CHEST);
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
            }

            this.lastDeathInventory.addItem(createGoldenHead());
            player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(player.getLevel() * 7);

            new TimeBomb(player.getName(), player.getLocation()).runTaskLater(Meetup.getPlugin(), 20L * 30);
/*
            final Integer[] counter = {30};

            Hologram hologram = HologramAPI.createHologram(player.getLocation().add(0, 1, 0.4), ChatColor.GOLD.toString() + counter[0]);
            hologram.spawn();

            new BukkitRunnable() {
                @Override
                public void run() {
                    hologram.setText(ChatColor.GOLD.toString() + counter[0]);
                    counter[0]--;

                    if (counter[0] <= 0) {
                        cancel();
                        hologram.despawn();
                    }
                }
            }.runTaskTimerAsynchronously(Meetup.getPlugin(), 0L, 20L);
*/
            event.getDrops().clear();
        }

        ProfileUtils.getInstance().getProfile(player.getUniqueId()).setDeaths(ProfileUtils.getInstance().getProfile(player.getUniqueId()).getDeaths() + 1);

        if (killer != null) {
            Player killerP = (Player) killer;

            int killerElo = ProfileUtils.getInstance().getProfile(killerP.getUniqueId()).getElo();
            int killedElo = ProfileUtils.getInstance().getProfile(player.getUniqueId()).getElo();

            ProfileUtils.getInstance().getProfile(killerP.getUniqueId()).setMatchKills(ProfileUtils.getInstance().getProfile(killerP.getUniqueId()).getMatchKills() + 1);
            ProfileUtils.getInstance().getProfile(killerP.getUniqueId()).setKills(ProfileUtils.getInstance().getProfile(killerP.getUniqueId()).getKills() + 1);

            ProfileUtils.getInstance().getProfile(killerP.getUniqueId()).setElo(ProfileUtils.getInstance().getNewRating(killerP, killedElo, 1));
            killerP.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour elo is now &b" + ProfileUtils.getInstance().getProfile(killerP.getUniqueId()).getElo()) + " " + ProfileUtils.getInstance().calculateColor(killerP) + "(" + ProfileUtils.getInstance().calculateRank(killerP) + ")");
            killerP.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7- &eYou have gained &b" + Math.abs(ProfileUtils.getInstance().getProfile(killerP.getUniqueId()).getElo() - killerElo)));

            ProfileUtils.getInstance().getProfile(player.getUniqueId()).setElo(ProfileUtils.getInstance().getNewRating(player, killerElo, 0));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYour elo is now &b" + ProfileUtils.getInstance().getProfile(player.getUniqueId()).getElo()) + " " + ProfileUtils.getInstance().calculateColor(player) + "(" + ProfileUtils.getInstance().calculateRank(player) + ")");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7- &eYou have lose " + Math.abs(ProfileUtils.getInstance().getProfile(player.getUniqueId()).getElo() - killedElo)));

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + killerP.getName() + " was " + ProfileUtils.getInstance().calculateColor(killerP) + ProfileUtils.getInstance().calculateRank(killerP) + "(" + ProfileUtils.getInstance().getProfile(killerP.getUniqueId()).getElo() + ")"));

            ProfileUtils.getInstance().getProfile(player.getUniqueId()).save(true);
            ProfileUtils.getInstance().getProfile(killerP.getUniqueId()).save(true);
        }

        player.getWorld().strikeLightningEffect(player.getLocation());

        Meetup.getPlugin().getSpectatorManager().setSpectator(player);

        Meetup.getPlugin().getGameManager().getAlivePlayers().remove(player);
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
            this.location.getWorld().spigot().strikeLightning(this.location, true);
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
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (Meetup.getPlugin().getGameManager().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }

        if (block.getType() == Material.SKULL || block.getType() == Material.ANVIL || block.getType() == Material.LEAVES_2 || block.getType() == Material.LEAVES || block.getType() == Material.COBBLESTONE || block.getType() == Material.OBSIDIAN || block.getType() == Material.getMaterial(175) || block.getType() == Material.getMaterial(31)) {
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (Meetup.getPlugin().getGameManager().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        GameManager game = Meetup.getPlugin().getGameManager();

        event.setMotd(game.getGameState().name().toUpperCase() + ";" + game.getAlivePlayers().size() + ";" + Meetup.getPlugin().getSpectatorManager().getSpectators().size() + ";" + (game.getWinner() == null ? "null" : game.getWinner().getName()));
    }

    @EventHandler
    public void onPortalEvent(PlayerPortalEvent event) {
        event.setCancelled(true);
    }
}
