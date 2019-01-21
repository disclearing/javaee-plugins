package me.javaee.uhc.listeners.misc;

import me.javaee.uhc.UHC;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.events.BorderShrinkEvent;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.tasks.GameTimeTask;
import me.javaee.uhc.team.UHCTeam;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.silexpvp.nightmare.util.JavaUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class GameListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            CraftingInventory inventory = event.getInventory();
            if (inventory.getResult().getType() == Material.GOLDEN_APPLE && inventory.getResult().getDurability() == 1) {
                inventory.setResult(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getLocation().getWorld().getName().equalsIgnoreCase("lobby")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't break in this world.");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        boolean fuerza = UHC.getInstance().getConfigurator().getBooleanOption("STRENGTH").getValue();
        boolean velocidad = UHC.getInstance().getConfigurator().getBooleanOption("SPEED").getValue();
        boolean debuffs = UHC.getInstance().getConfigurator().getBooleanOption("DEBUFFS").getValue();

        int strength = UHC.getInstance().getConfigurator().getIntegerOption("STRENGTHLVL").getValue();
        int speed = UHC.getInstance().getConfigurator().getIntegerOption("SPEEDLVL").getValue();

        int[] fIds = new int[]{8201, 8233, 8265};
        int[] sIds = new int[]{8194, 8226, 8258};
        int[] dIds = new int[]{16428, 16460, 8236, 8268, 16452, 16420, 16388, 8260, 8228, 8196, 16456, 16424, 8264, 8232};

        if (item != null) {
            if (item.getType() == Material.POTION) {
                if (ArrayUtils.contains(fIds, item.getDurability())) {
                    if (fuerza) {
                        if (strength == 1) {
                            if (item.getDurability() == 8233) {
                                event.setCancelled(true);
                                player.sendMessage(ChatColor.RED + "Max strength is 1.");
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Strength is disabled.");
                    }
                } else if (ArrayUtils.contains(sIds, item.getDurability())) {
                    if (velocidad) {
                        if (speed == 1) {
                            if (item.getDurability() == 8226) {
                                event.setCancelled(true);
                                player.sendMessage(ChatColor.RED + "Max speed is 1.");
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Speed is disabled.");
                    }
                } else if (ArrayUtils.contains(dIds, item.getDurability())) {
                    if (debuffs) {
                        player.sendMessage(ChatColor.RED + "Debuffs are disabled.");
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void entityDamage(EntityDamageEvent event) {
        if (Scenario.getByName("Event Game").isEnabled()) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();

                double damage = event.getDamage() / 20 * 100;
                double health = (((Player) event.getEntity()).getHealth() / 20 * 100) - damage;

                if (UHC.getInstance().getGameManager().getHost() == player || UHC.getInstance().getGameManager().getModerators().contains(player) || UHC.getInstance().getGameManager().getHelpers().contains(player) || UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                    return;
                }

                if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) return;

                if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;

                    if (damageEvent.isCancelled()) {
                        return;
                    }

                    if (damageEvent.getDamager() instanceof Player) {
                        Player damager = (Player) damageEvent.getDamager();

                        if (UHC.getInstance().getGameManager().getHost() == damager || UHC.getInstance().getGameManager().getModerators().contains(damager) || UHC.getInstance().getSpectatorManager().getSpectators().contains(damager)) {
                            return;
                        }

                        TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7(&cPvP&7) &a" + damager.getName() + " &7> &c" + player.getName() + " &7[&c" + damage + "%&7] &7[&a" + JavaUtils.format(health) + "%&7] &7(&cMelee&7)"));
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tele " + player.getName()));
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&eClick here to teleport to &c" + player.getName())).create()));

                        for (Player spectators : UHC.getInstance().getSpectatorManager().getSpectators()) {
                            spectators.spigot().sendMessage(message);
                        }

                        if (UHC.getInstance().getGameManager().getHost() != null) {
                            UHC.getInstance().getGameManager().getHost().spigot().sendMessage(message);
                        }

                        for (Player mods : UHC.getInstance().getGameManager().getModerators()) {
                            mods.spigot().sendMessage(message);
                        }

                        for (Player mods : UHC.getInstance().getGameManager().getHelpers()) {
                            mods.spigot().sendMessage(message);
                        }
                    }
                }

                if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    return;
                }

                if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                    return;
                }

                TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7(&cPvE&7) &a" + player.getName() + " &7[&c" + damage + "%&7] &7[&a" + JavaUtils.format(health) + "%&7] &7(&c" + WordUtils.capitalizeFully(event.getCause().name()).replace("_", " ") + "&7)"));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tele " + player.getName()));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&eClick here to teleport to &c" + player.getName())).create()));

                for (Player spectators : UHC.getInstance().getSpectatorManager().getSpectators()) {
                    spectators.spigot().sendMessage(message);
                }

                if (UHC.getInstance().getGameManager().getHost() != null) {
                    UHC.getInstance().getGameManager().getHost().spigot().sendMessage(message);
                }

                for (Player mods : UHC.getInstance().getGameManager().getModerators()) {
                    mods.spigot().sendMessage(message);
                }

                for (Player mods : UHC.getInstance().getGameManager().getHelpers()) {
                    mods.spigot().sendMessage(message);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDisconnect(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            if (UHC.getInstance().getGameManager().getHost() == event.getPlayer()) {
                return;
            }

            if (UHC.getInstance().getGameManager().getHost() != event.getPlayer()) {
                if (UHC.getInstance().getGameManager().getModerators().contains(event.getPlayer())) {
                    return;
                }

                if (UHC.getInstance().getGameManager().getHelpers().contains(event.getPlayer())) {
                    return;
                }

                if (UHC.getInstance().getSpectatorManager().getSpectators().contains(event.getPlayer())) {
                    return;
                }
            }

            if (ProfileUtils.getInstance().getProfile(event.getPlayer().getUniqueId()).isDead()) {
                ProfileUtils.getInstance().getProfile(event.getPlayer().getUniqueId()).save(true);
            }
        }

        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            if (UHCTeam.getByUUID(event.getPlayer().getUniqueId()) != null) {
                if (UHCTeam.getByUUID(event.getPlayer().getUniqueId()).getPlayerList().size() <= 1) {
                    UHC.getInstance().getTeams().remove(UHCTeam.getByUUID(event.getPlayer().getUniqueId()));
                } else {
                    UHCTeam.getByUUID(event.getPlayer().getUniqueId()).getPlayerList().remove(event.getPlayer().getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onJoinLogger(PlayerJoinEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            if (!ProfileUtils.getInstance().getProfile(event.getPlayer().getUniqueId()).isDead()) {
                ProfileUtils.getInstance().getProfile(event.getPlayer().getUniqueId()).setAFKTimeLeft(0);
            }

            for (Player player : UHC.getInstance().getSpectatorManager().getSpectators()) {
                if (UHC.getInstance().getGameManager().getAlivePlayers().contains(event.getPlayer().getUniqueId())) {
                    event.getPlayer().hidePlayer(player);
                }
            }
        }
    }

    @EventHandler
    public void onAsyncCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().contains("/tc ")) {
            event.setMessage(event.getMessage().replace("/tc ", "/teamchat "));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

            if (!UHC.getInstance().isGameStarted()) {
                profile.setDead(false);
                profile.setLateScattered(false);
            }

            if (!profile.isDead() && !UHC.getInstance().getGameManager().getAlivePlayers().contains(player.getUniqueId())) {
                UHC.getInstance().getSpectatorManager().setSpectator(player);
            }

            if (profile.isDead()) {
                UHC.getInstance().getSpectatorManager().setSpectator(player);
            }

            if (UHC.getInstance().getGameManager().getAlivePlayers().contains(player.getUniqueId())) {
                for (Player spectators : UHC.getInstance().getSpectatorManager().getSpectators()) {
                    player.hidePlayer(spectators);
                }

                if (UHC.getInstance().getGameManager().getHost() != null) {
                    player.hidePlayer(UHC.getInstance().getGameManager().getHost());
                }

                for (Player mods : UHC.getInstance().getGameManager().getModerators()) {
                    player.hidePlayer(mods);
                }

                for (Player mods : UHC.getInstance().getGameManager().getHelpers()) {
                    player.hidePlayer(mods);
                }

                return;
            }

            if (player.hasPermission("command.latejoin")) {
                if (GameTimeTask.getNumOfSeconds() < 900) {
                    TextComponent msg = new TextComponent("You have vip rank, can join the uhc using /latejoin!");

                    msg.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    msg.setBold(true);
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.AQUA + "Click to be added to the uhc!").create()));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/latejoin"));

                    player.spigot().sendMessage(msg);
                    player.spigot().sendMessage(msg);
                    player.spigot().sendMessage(msg);
                    player.spigot().sendMessage(msg);
                }
            }
        }

        if (UHC.getInstance().getGameManager().getGameState() == GameState.END) {
            UHC.getInstance().getSpectatorManager().setSpectator(player);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAsyncPreJoin(AsyncPlayerPreLoginEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.SCATTER) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Scattering...");
        }
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        if (UHC.getInstance().getGameManager().getSuspendedPlayers().contains(event.getPlayer().getName().toLowerCase())) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You have been suspended from this UHC because you've cleaned in the final fights.");
            return;
        }

        if (Scenario.getByName("Weekend Event").isEnabled()) {
            if (!UHC.getInstance().getServer().hasWhitelist()) {
                if (UHC.getInstance().getWeekend().contains(event.getPlayer().getUniqueId())) {
                    if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING)
                    event.allow();
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You are not allowed to join this event.");
                }
            }

            if (event.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
                if (event.getPlayer().hasPermission("staff")) {
                    event.allow();
                    return;
                }
            }

            if (event.getPlayer().hasPermission("rank.vip")) {
                if (UHC.getInstance().getConfigurator().getBooleanOption("VIPJOIN").getValue()) {
                    event.allow();
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You need to wait the host.");
                }
            }

            return;
        }

        if (Scenario.getByName("Event Game").isEnabled()) {
            if (!UHC.getInstance().getConfigurator().getBooleanOption("CANJOIN").getValue()) {
                if (!event.getPlayer().isOp()) {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You can't join the uhc at the moment.");
                }
            }
        }

        if (event.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
            if (event.getPlayer().hasPermission("staff")) {
                event.allow();
                return;
            }

            if (event.getPlayer().hasPermission("rank.vip")) {
                if (!Scenario.getByName("Event Game").isEnabled()) {
                    if (UHC.getInstance().getConfigurator().getBooleanOption("VIPJOIN").getValue()) {
                        event.allow();
                    } else {
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You need to wait the host.");
                    }
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You can't join this event.");
                }
            }
        }
    }

    /**
     * @param event
     * @EventHandler public void onJoin(PlayerLoginEvent event) {
     * Player player = event.getPlayer();
     * if (player.hasPermission("staff")) {
     * event.allow();
     * return;
     * }
     * <p>
     * if (UHC.getInstance().getGameManager().getSuspendedPlayers().contains(player.getName().toLowerCase())) {
     * event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You have been suspended from this UHC because you've cleaned in the final fights.");
     * return;
     * }
     * <p>
     * if (Scenario.getByName("Event Game").isEnabled()) {
     * if (UHC.getInstance().getChampions().contains(player.getUniqueId())) {
     * if (UHC.getInstance().getConfigurator().getBooleanOption("CANJOIN").getValue()) {
     * event.setResult(PlayerLoginEvent.Result.ALLOWED);
     * } else {
     * event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', "&cEstás agregado a la WHITELIST del &6#SilexChampions.\n&c- Cumples con los requisitos. El evento empieza a las &622:00 HORA ESPAÑOLA&c, más información en &6@SilexGameFeed&c. Suerte y te esperamos!"));
     * }
     * } else {
     * event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', "&cNO estás agregado a la WHITELIST del &6#SilexChampions\n&c- No cumples con los requisitos, necesitas 1 WIN o 50 KILLS para poder ingresar. Suerte en la próxima season!"));
     * }
     * <p>
     * return;
     * } else {
     * if (!UHC.getInstance().getConfigurator().getBooleanOption("CANJOIN").getValue()) {
     * if (!player.isOp()) {
     * event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You can't join the uhc at the moment.");
     * }
     * }
     * }
     * <p>
     * if (event.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
     * if (player.hasPermission("rank.vip")) {
     * if (!Scenario.getByName("Event Game").isEnabled()) {
     * if (UHC.getInstance().getConfigurator().getBooleanOption("VIPJOIN").getValue()) {
     * event.allow();
     * } else {
     * event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You need to wait the host.");
     * }
     * } else {
     * event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You can't join this event.");
     * }
     * }
     * }
     * }
     */

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                if (!UHC.getInstance().getGameManager().isPvpEnable()) {
                    event.setCancelled(true);
                } else {
                    if (!UHC.getInstance().getConfigurator().getBooleanOption("FRIENDLYFIRE").getValue()) {
                        Player attacker = (Player) event.getDamager();
                        Player attacked = (Player) event.getEntity();

                        UHCTeam attackerTeam = UHCTeam.getByUUID(attacker.getUniqueId());
                        UHCTeam attackedTeam = UHCTeam.getByUUID(attacked.getUniqueId());

                        if (attackedTeam != null && attackerTeam != null) {
                            if (!(event.getDamager() instanceof FishHook) && ((Player) event.getDamager()).getItemInHand() != null) {
                                if (attackedTeam == attackerTeam) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFall(PlayerMoveEvent event) {
        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            if (UHC.getInstance().getPracticeManager().getPracticePlayers().contains(event.getPlayer())) {
                return;
            }

            if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) {
                event.getPlayer().teleport(Bukkit.getWorld("lobby").getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onBorderShrink(BorderShrinkEvent event) {
        if (event.getNewRadius() == 100) {
            if (UHC.getInstance().getConfigurator().getBooleanOption("RANDOMBORDER").getValue()) {
                for (Player players : Bukkit.getWorld("world").getPlayers()) {
                    if (players.getLocation().getX() < -event.getNewRadius() || players.getLocation().getX() > event.getNewRadius() || players.getLocation().getZ() < -event.getNewRadius() || players.getLocation().getZ() > event.getNewRadius()) {
                        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(players) || UHC.getInstance().getGameManager().getHost() == players || UHC.getInstance().getGameManager().getModerators().contains(players))
                            continue;

                        int x = new Random().nextInt(100 - 5);
                        int z = new Random().nextInt(100 - 5);

                        Location location = new Location(Bukkit.getWorld("world"), x, Bukkit.getWorld("world").getHighestBlockYAt(x, z), z);

                        players.teleport(location);
                    }
                }
            }
        }

        if (event.getNewRadius() <= 500) {
            for (Player player : Bukkit.getWorld("world_nether").getPlayers()) {
                Random localRandom = new Random();

                int i = localRandom.nextInt(event.getNewRadius() * 2) - event.getNewRadius();
                int j = localRandom.nextInt(event.getNewRadius() * 2) - event.getNewRadius();

                Location pedo = new Location(Bukkit.getWorld("world"), i, Bukkit.getWorld("world").getHighestBlockYAt(i, j) + 2.0D, j);

                player.teleport(pedo);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6You have been teleported out of the nether."));
            }
        } else {
            for (Player player : Bukkit.getWorld("world_nether").getPlayers()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThe &cNETHER &ewill be closed in the &c500x500 &eshrink."));
            }
        }

        if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 2) {
            if (event.getNewRadius() == 500 || event.getNewRadius() == 100) {
                World world = Bukkit.getWorld("world");

                Set<UHCTeam> uhcTeams = new HashSet<>();
                for (Player p : world.getPlayers()) {
                    if (p.getLocation().getX() < -event.getNewRadius() || p.getLocation().getX() > event.getNewRadius() || p.getLocation().getZ() < -event.getNewRadius() || p.getLocation().getZ() > event.getNewRadius()) {

                        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(p) || UHC.getInstance().getGameManager().getHost() == p || UHC.getInstance().getGameManager().getModerators().contains(p))
                            continue;

                        UHCTeam team = UHCTeam.getByUUID(p.getUniqueId());

                        uhcTeams.add(team);
                    }
                }

                scatterTeams(uhcTeams, event.getNewRadius());
            }
        }

        if (event.getNewRadius() == 25) {
            UHC.getInstance().getCombatLoggerManager().removeCombatLoggers();
        }
    }

    public static void scatterTeams(Set<UHCTeam> uhcTeams, int radius) {
        for (UHCTeam uhcTeam : uhcTeams) {
            int x = new Random().nextInt(radius - 5);
            int z = new Random().nextInt(radius - 5);

            Location location = new Location(Bukkit.getWorld("world"), x, Bukkit.getWorld("world").getHighestBlockYAt(x, z), z);

            for (UUID uuid : uhcTeam.getPlayerList()) {
                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    if (!UHC.getInstance().getSpectatorManager().getSpectators().contains(player) && UHC.getInstance().getGameManager().getHost() != player && !UHC.getInstance().getGameManager().getModerators().contains(player) && !UHC.getInstance().getGameManager().getHelpers().contains(player)) {
                        player.teleport(location);
                    }
                }
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
    public void onEntityDamageEventPvP(EntityDamageByEntityEvent event) {
        if (!UHC.getInstance().getGameManager().isPvpEnable()) {
            if (event.getEntity() instanceof Player) {
                if (event.getDamager() instanceof Arrow) {
                    if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
                        event.setCancelled(true);
                    }
                }

                if (event.getDamager() instanceof FishHook) {
                    if (((FishHook) event.getDamager()).getShooter() instanceof Player) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
