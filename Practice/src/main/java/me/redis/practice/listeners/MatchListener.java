package me.redis.practice.listeners;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.match.IMatch;
import me.redis.practice.enums.MatchStatus;
import me.redis.practice.match.type.FreeForAllMatch;
import me.redis.practice.match.type.SoloMatch;
import me.redis.practice.profile.Profile;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MatchListener implements Listener {
    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

            if (profile.getStatus() == ProfileStatus.MATCH) {
                IMatch match = profile.getCurrentMatch();

                if (match.getStartTimestamp() == null) {
                    event.setCancelled(true);
                    return;
                }

                if (System.currentTimeMillis() - match.getStartTimestamp().getTime() < 30000) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            return;
        }

        player.spigot().respawn();

        IMatch match = profile.getCurrentMatch();
        match.handleDeath(player, player.getLocation(), ChatColor.YELLOW + player.getName() + " has died.");

        event.setDeathMessage(null);
        player.setHealth(20.0);

        for (ItemStack i : event.getDrops()) {
            i.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            return;
        }

        Projectile entity = event.getEntity();
        IMatch match = profile.getCurrentMatch();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (match.getPlayers().contains(p)) {
                continue;
            }

            if (match.getSpectators().contains(p.getUniqueId())) {
                continue;
            }

            Practice.getPlugin().getEntityHider().hideEntity(p, entity);
        }
    }

    @EventHandler
    public void onSplash(PotionSplashEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        if (event.getIntensity(player) < 0.4) {
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

            if (profile.getCurrentMatch() != null) {
                if (profile.getCurrentMatch() instanceof FreeForAllMatch) {
                    FreeForAllMatch freeForAllMatch = (FreeForAllMatch) profile.getCurrentMatch();

                    freeForAllMatch.getMissedPotions().put(player, freeForAllMatch.getMissedPotions().get(player) + 1);
                } else if (profile.getCurrentMatch() instanceof SoloMatch) {
                    SoloMatch soloMatch = (SoloMatch) profile.getCurrentMatch();

                    soloMatch.getMissedPotions().put(player, soloMatch.getMissedPotions().get(player) + 1);
                }
            }
        }
    }

    @EventHandler
    public void regainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);

            if (profile.getCurrentMatch() != null && profile.getCurrentMatch().getLadder().getName().equalsIgnoreCase("UHC")) {
                if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) event.setCancelled(true);
                if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            return;
        }

        IMatch match = profile.getCurrentMatch();

        if (event.getItemDrop().getItemStack().getType().equals(Material.DIAMOND_SWORD)) {
            event.setCancelled(true);
            return;
        } else if (event.getItemDrop().getItemStack().getType().equals(Material.GLASS_BOTTLE)) {
            event.getItemDrop().remove();
            return;
        } else if (event.getItemDrop().getItemStack().getType().equals(Material.ENCHANTED_BOOK)) {
            event.setCancelled(true);
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (match.getPlayers().contains(p)) {
                continue;
            }

            if (match.getSpectators().contains(p.getUniqueId())) {
                continue;
            }

            Practice.getPlugin().getEntityHider().hideEntity(p, event.getItemDrop());
        }

        new BukkitRunnable() {
            public void run() {
                if (event.getItemDrop() != null) {
                    event.getItemDrop().remove();
                }
            }
        }.runTaskLater(Practice.getPlugin(), 20L * 6);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            return;
        }

        if (profile.getCurrentMatch().getMatchStatus() == MatchStatus.STARTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            return;
        }

        IMatch match = profile.getCurrentMatch();

        if (match.getMatchStatus() == MatchStatus.STARTING) {
            event.setCancelled(true);
            return;
        }

        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Profile damagerProfile = Practice.getPlugin().getProfileManager().getProfile(damager.getUniqueId());

            if (damagerProfile.getStatus() != ProfileStatus.MATCH) {
                event.setCancelled(true);
                return;
            }

            if (match.isDead(damager)) {
                event.setCancelled(true);
                return;
            }

            if (match.getTeam(player).contains(damager)) {
                event.setCancelled(true);
                return;
            }

            if (player.getHealth() - event.getFinalDamage() <= 0.0) {
                player.setHealth(20);
                match.handleDeath(player, player.getLocation(), ChatColor.RED + player.getName() + ChatColor.YELLOW + " has been slain by " + ChatColor.RED + damager.getName() + ChatColor.YELLOW + ".");
            }
        } else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();

            if (arrow.getShooter() instanceof Player) {
                Player damager = (Player) arrow.getShooter();

                double healthDisplay;

                if (player.getHealth() - event.getFinalDamage() <= 0.0) {
                    healthDisplay = 0.0;
                } else {
                    healthDisplay = (player.getHealth() - event.getFinalDamage()) / 2.0;
                }

                NumberFormat formatter = new DecimalFormat("#0.0");
                String newFormat = formatter.format(healthDisplay);
                damager.sendMessage(ChatColor.RED + player.getName() + ChatColor.YELLOW + " is now at " + ChatColor.RED + newFormat + ChatColor.DARK_RED + " " + StringEscapeUtils.unescapeJava("\u2764"));

                if (player.getHealth() - event.getFinalDamage() <= 0.0) {
                    player.setHealth(20);
                    match.handleDeath(player, player.getLocation(), ChatColor.RED + player.getName() + ChatColor.YELLOW + " has been shot by " + ChatColor.RED + damager.getName() + ChatColor.YELLOW + ".");
                }
            }
        }

        if (player.getHealth() - event.getFinalDamage() <= 0.0) {
            match.handleDeath(player, player.getLocation(), ChatColor.RED + player.getName() + ChatColor.YELLOW + " has died.");
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            return;
        }

        if (!event.getItem().getItemMeta().hasDisplayName()) {
            return;
        }

        if (event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + ChatColor.BOLD.toString() + "Golden Head")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 240, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            return;
        }

        if (!profile.getCurrentMatch().getLadder().isRegainHealth()) {
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) event.setCancelled(true);
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            return;
        }

        IMatch match = profile.getCurrentMatch();
        match.handleDeath(player, null, ChatColor.RED + player.getName() + ChatColor.YELLOW + " has left the match.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            return;
        }

        IMatch match = profile.getCurrentMatch();
        match.handleDeath(player, null, ChatColor.RED + player.getName() + ChatColor.YELLOW + " has left the match.");
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);

        if (event.getItem() == null) {
            return;
        }

        if (profile.getStatus() != ProfileStatus.MATCH) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!event.getItem().getType().equals(Material.ENCHANTED_BOOK)) {
            return;
        }

        if (ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).startsWith("Custom")) {
            Integer kitNumber = Integer.valueOf(event.getItem().getItemMeta().getDisplayName().replace("#", "").split(" ")[3]);

            me.redis.practice.utils.SerializationUtils.playerInventoryFromString(profile.getKitByLadderAndNumber(profile.getCurrentMatch().getLadder(), kitNumber).getInventory(), player);
            player.updateInventory();
        } else if (ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).startsWith("Default")) {
            me.redis.practice.utils.SerializationUtils.playerInventoryFromString(profile.getCurrentMatch().getLadder().getDefaultInventory(), player);
        }
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getTo().getX() != event.getFrom().getX() && event.getTo().getZ() != event.getFrom().getZ()) {
            if (Practice.getPlugin().getProfileManager().getProfile(player).getCurrentMatch() != null) {
                IMatch match = Practice.getPlugin().getProfileManager().getProfile(player).getCurrentMatch();

                if (match.getLadder().getName().toLowerCase().equalsIgnoreCase("sumo")) {
                    if (player.getLocation().getBlock().getType() == Material.WATER || player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                        match.handleDeath(player, player.getLocation(), ChatColor.RED + player.getName() + ChatColor.YELLOW + " has died.");
                    }
                }
            }
        }
    }
}