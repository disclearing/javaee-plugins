package me.redis.practice.listeners;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.profile.Profile;
import me.redis.practice.queue.IQueue;
import me.redis.practice.events.PlayerExitQueueEvent;
import me.redis.practice.queue.type.SoloQueue;
import me.redis.practice.utils.ItemBuilder;
import me.redis.practice.utils.PracticeUtils;
import me.redis.practice.utils.SerializationUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerListeners implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        initialize(player);
    }

    public void initialize(Player player) {
        Inventory inventory = player.getInventory();

        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setSaturation(14);
        player.setCanPickupItems(false);
        player.setAllowFlight(false);
        player.setNoDamageTicks(19);

        if (Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")) == null) {
            player.sendMessage(ChatColor.RED + "You need to create a world called 'Lobby' and then /setworldspawn to get spawned there.");
        } else {
            player.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});

        Practice.getPlugin().getEntityHider().hideAllPlayers(player);

        player.getInventory().setContents(PracticeUtils.getLobbyInventory());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (Practice.getPlugin().getProfileManager().getProfile(player).getStatus() != ProfileStatus.MATCH) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (Practice.getPlugin().getProfileManager().getProfile(player).getStatus() != ProfileStatus.MATCH) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() == ProfileStatus.LOBBY) {
            if (player.getItemInHand() != null) {
                if (player.getItemInHand().getType() == Material.BOOK) {
                    Inventory inventory = Bukkit.createInventory(null, getSize(), ChatColor.GOLD + "Kit editor");

                    for (Ladder ladder : Practice.getPlugin().getLadderManager().getLadders().values()) {
                        inventory.addItem(new ItemBuilder(SerializationUtils.itemStackFromString(ladder.getIcon())).setDisplayName("&a" + ladder.getName()).create());
                    }

                    player.openInventory(inventory);
                    return;
                }

                if (player.getItemInHand().getType() == Material.IRON_SWORD) {
                    Inventory inventory = Bukkit.createInventory(null, getSize(), ChatColor.BLUE + "Un-Ranked Queues");

                    if (!Practice.getPlugin().getQueueManager().getQueues().isEmpty()) {
                        for (IQueue queue : Practice.getPlugin().getQueueManager().getQueues().values()) {
                            if (queue instanceof SoloQueue && !queue.isRanked()) {
                                inventory.setItem(queue.getLadder().getPosition(), ((SoloQueue) queue).getQueueToInventory());
                            }
                        }
                    }

                    player.openInventory(inventory);

                    return;
                }

                if (player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null) {
                    if (player.getItemInHand().getItemMeta().getDisplayName().contains("Create")) {
                        player.performCommand("team create");
                    } else if (player.getItemInHand().getItemMeta().getDisplayName().contains("Leave the match")) {
                        Practice.getPlugin().getSpectatorManager().stopSpectating(player, false);
                    }
                }

                if (player.getItemInHand().getType() == Material.DIAMOND_SWORD) {
                    if (Practice.getPlugin().getConfig().getBoolean("MATCHES.MINIMUM_UNRANKED.ENABLED")) {
                        if (profile.getMatchesPlayed() <= Practice.getPlugin().getConfig().getInt("MATCHES.MINIMUM_UNRANKED.NUMBER")) {
                            player.sendMessage(ChatColor.RED + "You need to play " + (Practice.getPlugin().getConfig().getInt("MATCHES.MINIMUM_UNRANKED.NUMBER") - profile.getMatchesPlayed()) + " more matches in order to queue for a ranked match.");
                            return;
                        }
                    }

                    Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GREEN + "Ranked Queues");

                    if (!Practice.getPlugin().getQueueManager().getQueues().isEmpty()) {
                        for (IQueue queue : Practice.getPlugin().getQueueManager().getQueues().values()) {
                            if (queue instanceof SoloQueue && queue.isRanked()) {
                                inventory.setItem(queue.getLadder().getPosition(), ((SoloQueue) queue).getQueueToInventory());
                            }
                        }
                    }

                    player.openInventory(inventory);

                    return;
                }
            }

            if (!((player.hasPermission("practice.admin") || player.isOp()) && player.getGameMode() == GameMode.CREATIVE)) {
                event.setCancelled(true);
            }
        } else if (profile.getStatus() == ProfileStatus.QUEUE) {
            if (player.getItemInHand() != null) {
                if (player.getItemInHand().getType() == Material.INK_SACK) {
                    PlayerExitQueueEvent queueEvent = new PlayerExitQueueEvent(player, profile.getCurrentQueue());
                    Bukkit.getPluginManager().callEvent(queueEvent);
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getStatus() != ProfileStatus.MATCH) {
            event.setCancelled(true);
        }
    }

    public int getSize() {
        int size = Practice.getPlugin().getLadderManager().getLadders().size();

        if (size % 9 == 0) {
            return size;
        } else {
            if (size < 10) {
                return 9;
            } else {
                return 18;
            }
        }
    }
}
