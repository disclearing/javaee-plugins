package me.redis.practice.kit;

import lombok.Getter;
import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.profile.Profile;
import me.redis.practice.utils.ItemBuilder;
import me.redis.practice.utils.PracticeUtils;
import me.redis.practice.utils.SerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitManager implements Listener {
    @Getter
    private Map<UUID, Ladder> editKits;
    @Getter
    private Map<UUID, Kit> renamingKits;

    public KitManager() {
        this.editKits = new HashMap<>();
        this.renamingKits = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
    }

    public void startEditing(Player player, Ladder ladder) {
        this.editKits.put(player.getUniqueId(), ladder);

        Practice.getPlugin().getEntityHider().hideAllPlayers(player);
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);

        if (profile.getStatus() != ProfileStatus.LOBBY) {
            player.sendMessage(ChatColor.RED + "You must be in the lobby to edit your kits.");
            return;
        }

        profile.setStatus(ProfileStatus.EDITING);

        PracticeUtils.resetPlayer(player);
        player.updateInventory();

        if (ladder.getDefaultInventory() != null) {
            SerializationUtils.playerInventoryFromString(ladder.getDefaultInventory(), player);
        }

        player.updateInventory();

        player.teleport(new Location(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.EDITOR.NAME")), Practice.getPlugin().getConfig().getDouble("WORLD.EDITOR.X"), Practice.getPlugin().getConfig().getDouble("WORLD.EDITOR.Y"), Practice.getPlugin().getConfig().getDouble("WORLD.EDITOR.Z")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou are now editing your &a" + ladder.getName() + "&e's kits."));
    }

    private void finishEditing(Player player) {
        this.editKits.remove(player.getUniqueId());

        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);
        profile.setStatus(ProfileStatus.LOBBY);

        PracticeUtils.resetPlayer(player);
        player.updateInventory();

        new BukkitRunnable() {
            public void run() {
                player.getInventory().setContents(PracticeUtils.getLobbyInventory());
                player.updateInventory();
            }
        }.runTaskLater(Practice.getPlugin(), 2L);

        player.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!editKits.containsKey(event.getWhoClicked().getUniqueId())) return;

        Inventory inv = event.getClickedInventory();

        if (inv == null) return;

        if (inv.getTitle().contains("Manage")) {
            if (event.getCurrentItem() == null) return;
            if (event.getCurrentItem().getType().equals(Material.AIR)) return;

            Player player = (Player) event.getWhoClicked();
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);
            Ladder ladder = editKits.get(player.getUniqueId());

            String item = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase());
            String number = item.replaceAll("\\D+", "");

            if (item.contains("save")) {
                switch (number) {
                    case "1": //Save kit 1
                        Kit kit1 = new Kit(editKits.get(player.getUniqueId()).getName() + " kit 1", editKits.get(player.getUniqueId()), 1);
                        kit1.setInventory(SerializationUtils.playerInventoryToString(player.getInventory()));

                        if (profile.getKitByLadderAndNumber(ladder, 1) != null) {
                            profile.getKits().remove(profile.getKitByLadderAndNumber(ladder, 1));
                            profile.getKits().add(kit1);
                        } else {
                            profile.getKits().add(kit1);
                        }

                        profile.save();
                        break;
                    case "2":
                        Kit kit2 = new Kit(editKits.get(player.getUniqueId()).getName() + " kit 2", editKits.get(player.getUniqueId()), 2);
                        kit2.setInventory(SerializationUtils.playerInventoryToString(player.getInventory()));
                        if (profile.getKitByLadderAndNumber(ladder, 2) != null) {
                            profile.getKits().remove(profile.getKitByLadderAndNumber(ladder, 2));
                            profile.getKits().add(kit2);
                        } else {
                            profile.getKits().add(kit2);
                        }
                        profile.save();
                        break;
                    case "3":
                        Kit kit3 = new Kit(editKits.get(player.getUniqueId()).getName() + " kit 3", editKits.get(player.getUniqueId()), 3);
                        kit3.setInventory(SerializationUtils.playerInventoryToString(player.getInventory()));
                        if (profile.getKitByLadderAndNumber(ladder, 3) != null) {
                            profile.getKits().remove(profile.getKitByLadderAndNumber(ladder, 3));
                            profile.getKits().add(kit3);
                        } else {
                            profile.getKits().add(kit3);
                        }
                        profile.save();
                        break;
                }

                player.closeInventory();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have saved one of your &a" + editKits.get(player.getUniqueId()).getName() + "&e's kits."));
            } else if (item.contains("load")) {
                switch (number) {
                    case "1":
                        SerializationUtils.playerInventoryFromString(profile.getKitByLadderAndNumber(ladder, 1).getInventory(), player);
                        break;
                    case "2":
                        SerializationUtils.playerInventoryFromString(profile.getKitByLadderAndNumber(ladder, 2).getInventory(), player);
                        break;
                    case "3":
                        SerializationUtils.playerInventoryFromString(profile.getKitByLadderAndNumber(ladder, 3).getInventory(), player);
                        break;
                }

                player.closeInventory();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have loaded one of your &a" + ladder.getName() + "&e's kits."));
            } else if (item.contains("delete")) {
                switch (number) {
                    case "1":
                        profile.getKits().remove(profile.getKitByLadderAndNumber(ladder, 1));
                        profile.save();
                        break;
                    case "2":
                        profile.getKits().remove(profile.getKitByLadderAndNumber(ladder, 2));
                        profile.save();
                        break;
                    case "3":
                        profile.getKits().remove(profile.getKitByLadderAndNumber(ladder, 3));
                        profile.save();
                        break;
                }

                player.closeInventory();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have deleted one of your &a" + ladder.getName() + "&e's kits."));
            } else if (item.contains("rename")) {
                switch (number) {
                    case "1":
                        renamingKits.put(player.getUniqueId(), profile.getKitsByLadder(ladder).get(0));
                        System.out.println("yea");
                        break;
                    case "2":
                        renamingKits.put(player.getUniqueId(), profile.getKitsByLadder(ladder).get(1));
                        break;
                    case "3":
                        renamingKits.put(player.getUniqueId(), profile.getKitsByLadder(ladder).get(2));
                        break;
                }

                player.closeInventory();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eType a new name for the kit &7(Type 'cancel' to cancel this action)."));
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (renamingKits.containsKey(uuid)) {
            if (event.getMessage().equalsIgnoreCase("cancel")) {
                renamingKits.remove(uuid);
                event.getPlayer().sendMessage(ChatColor.RED + "You have cancelled the kit renaming.");
            } else {
                if (event.getMessage().contains(" ")) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't put names with spaces in it.");
                    return;
                }

                renamingKits.get(uuid).setName(event.getMessage());
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have changed the name of your kit."));
                renamingKits.remove(uuid);

                Practice.getPlugin().getProfileManager().getProfile(uuid).save();
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        if (!this.editKits.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        Player player = event.getPlayer();
        Ladder ladder = editKits.get(player.getUniqueId());

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock().getType().equals(Material.ANVIL)) {
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);
            Inventory menu = Bukkit.createInventory(null, 27, "Manage " + editKits.get(player.getUniqueId()).getName() + " kits");

            menu.setItem(2, new ItemBuilder(Material.CHEST).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSave kit: &61")).create());
            menu.setItem(4, new ItemBuilder(Material.CHEST).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSave kit: &62")).create());
            menu.setItem(6, new ItemBuilder(Material.CHEST).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSave kit: &63")).create());

            if (profile.getKitByLadderAndNumber(ladder, 1) != null) {
                Kit kit1 = profile.getKitByLadderAndNumber(ladder, 1);

                menu.setItem(11, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eLoad kit: &6" + kit1.getName())).create());
                //menu.setItem(20, new ItemBuilder(Material.NAME_TAG).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eRename kit: &6" + kit1.getName())).create());
                menu.setItem(20, new ItemBuilder(Material.FIRE).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cDelete kit: &6" + kit1.getName())).create());
            }

            if (profile.getKitByLadderAndNumber(ladder, 2) != null) {
                Kit kit1 = profile.getKitByLadderAndNumber(ladder, 2);

                menu.setItem(13, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eLoad kit: &6" + kit1.getName())).create());
                //menu.setItem(22, new ItemBuilder(Material.NAME_TAG).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eRename kit: &6" + kit1.getName())).create());
                menu.setItem(22, new ItemBuilder(Material.FIRE).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cDelete kit: &6" + kit1.getName())).create());
            }

            if (profile.getKitByLadderAndNumber(ladder, 3) != null) {
                Kit kit1 = profile.getKitByLadderAndNumber(ladder, 3);

                menu.setItem(15, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eLoad kit: &6" + kit1.getName())).create());
                //menu.setItem(24, new ItemBuilder(Material.NAME_TAG).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eRename kit: &6" + kit1.getName())).create());
                menu.setItem(24, new ItemBuilder(Material.FIRE).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cDelete kit: &6" + kit1.getName())).create());
            }

            player.openInventory(menu);
            event.setCancelled(true);
        }

        if (event.getClickedBlock().getType().equals(Material.SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST) || event.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
            PracticeUtils.resetPlayer(player);
            finishEditing(player);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPearl(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
            if (this.editKits.containsKey(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onThrow(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();

            if (this.editKits.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (this.editKits.containsKey(event.getPlayer().getUniqueId())) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (renamingKits.containsKey(event.getPlayer().getUniqueId())) {
            renamingKits.remove(event.getPlayer().getUniqueId());
        }
        if (!this.editKits.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }


        this.editKits.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (!this.editKits.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        this.editKits.remove(event.getPlayer().getUniqueId());
    }

}