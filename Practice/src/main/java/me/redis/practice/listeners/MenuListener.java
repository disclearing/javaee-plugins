package me.redis.practice.listeners;

import me.redis.practice.Practice;
import me.redis.practice.duel.DuelRequest;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.profile.Profile;
import me.redis.practice.queue.IQueue;
import me.redis.practice.events.PlayerEnterQueueEvent;
import me.redis.practice.utils.HiddenStringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MenuListener implements Listener {

    public static Map<UUID, UUID> selectedPlayer = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (inventory == null) {
            return;
        }

        if (inventory.getTitle().contains("Information")) {
            event.setCancelled(true);
            return;
        }

        if (inventory.getTitle().equals(ChatColor.BLUE + "Send a duel")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (!selectedPlayer.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You need to select a player to duel first.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            Player target = Bukkit.getPlayer(selectedPlayer.get(player.getUniqueId()));

            if (target == null) {
                player.sendMessage(ChatColor.RED + "That player is no longer online.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            Profile targetProfile = Practice.getPlugin().getProfileManager().getProfile(target.getUniqueId());

            if (targetProfile.hasRequest(player)) {
                player.sendMessage(ChatColor.RED + "You have already sent a duel request to that player.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            /*if (targetProfile.isHidingDuels()) {
                player.sendMessage(ChatColor.RED + "That player is not currently receiving duel requests.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }TODO */

            Ladder ladder = Practice.getPlugin().getLadderManager().getLadder(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

            if (ladder != null) {
                DuelRequest request = new DuelRequest(player, Bukkit.getPlayer(selectedPlayer.get(player.getUniqueId())), ladder);

                targetProfile.addRequest(player, request);

                new BukkitRunnable() {
                    public void run() {
                        if (Bukkit.getPlayer(target.getUniqueId()) == null) return;
                        if (Practice.getPlugin().getProfileManager().getProfile(target.getUniqueId()) == null) return;

                        Profile targetProfile = Practice.getPlugin().getProfileManager().getProfile(target.getUniqueId());

                        if (targetProfile.hasRequest(player)) {
                            if (targetProfile.getRequest(player).getIdentifier().equals(request.getIdentifier())) {
                                targetProfile.removeRequest(player);

                                player.sendMessage(ChatColor.RED + "Your duel request to " + Bukkit.getPlayer(targetProfile.getUniqueId()).getName() + " has expired.");
                                Bukkit.getPlayer(targetProfile.getUniqueId()).sendMessage(ChatColor.RED + "The duel request sent by " + player.getName() + " to you has expired.");
                            }
                        }
                    }
                }.runTaskLater(Practice.getPlugin(), 20L * 60);
            } else {
                player.sendMessage(ChatColor.RED + "Could not find that ladder.");
            }

            player.closeInventory();
            event.setCancelled(true);
        } else if (inventory.getTitle().equals(ChatColor.GREEN + "Ranked Queues")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (profile.getStatus() != ProfileStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + "You need to be in the spawn to join a queue.");
                player.closeInventory();
                event.setCancelled(true);
            }

            String hidden = HiddenStringUtil.extractHiddenString(event.getCurrentItem().getItemMeta().getLore().get(0));
            IQueue queue = Practice.getPlugin().getQueueManager().getQueues().get(UUID.fromString(hidden));

            if (queue == null) {
                player.sendMessage(ChatColor.RED + "Could not find that queue.");
                return;
            }

            queue.addToQueue(player);

            PlayerEnterQueueEvent queueEvent = new PlayerEnterQueueEvent(player, queue);
            Bukkit.getPluginManager().callEvent(queueEvent);

            player.closeInventory();
            event.setCancelled(true);
        } else if (inventory.getTitle().equals(ChatColor.BLUE + "Un-Ranked Queues")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (profile.getStatus() != ProfileStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + "You need to be in the spawn to join a queue.");
                player.closeInventory();
                event.setCancelled(true);
            }

            String hidden = HiddenStringUtil.extractHiddenString(event.getCurrentItem().getItemMeta().getLore().get(0));
            IQueue queue = Practice.getPlugin().getQueueManager().getQueues().get(UUID.fromString(hidden));

            if (queue == null) {
                player.sendMessage(ChatColor.RED + "Could not find that queue.");
                return;
            }

            queue.addToQueue(player);

            PlayerEnterQueueEvent queueEvent = new PlayerEnterQueueEvent(player, queue);
            Bukkit.getPluginManager().callEvent(queueEvent);

            player.closeInventory();
            event.setCancelled(true);
        } else if (inventory.getTitle().equalsIgnoreCase(ChatColor.GOLD + "Kit editor")) {
            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (profile.getStatus() != ProfileStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + "You need to be in the spawn to edit a kit.");
                player.closeInventory();
                event.setCancelled(true);
            }

            if (Practice.getPlugin().getLadderManager().getLadder(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) != null) {
                Practice.getPlugin().getKitManager().startEditing(player, Practice.getPlugin().getLadderManager().getLadder(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())));
            }

            event.setCancelled(true);
        }
    }
}