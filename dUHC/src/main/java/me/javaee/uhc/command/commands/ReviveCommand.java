package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.listeners.misc.EndListener;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.InventorySerialization;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ReviveCommand extends BaseCommand {
    public ReviveCommand() {
        super("revive", Arrays.asList("respawn"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (UHC.getInstance().getGameManager().getHost() == player || UHC.getInstance().getGameManager().getModerators().contains(player)) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("all")) {
                    if (UHC.getInstance().getGameManager().getGameState() == GameState.END) {
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            Profile profile = ProfileUtils.getInstance().getProfile(players.getUniqueId());

                            if (UHC.getInstance().getSpectatorManager().getSpectators().contains(players)) {
                                UHC.getInstance().getSpectatorManager().getSpectators().remove(players);
                                players.setAllowFlight(false);
                                players.getInventory().clear();
                                players.setCanPickupItems(true);
                                players.spigot().setCollidesWithEntities(true);
                                players.getInventory().setArmorContents(null);

                                Inventory inv = players.getInventory();

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

                                players.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                                players.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                                players.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                                players.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));

                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    all.showPlayer(player);
                                    all.showPlayer(players);
                                }
                            }
                        }
                    }
                    return;
                }

                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player with name '" + args[0] + "' not found.");
                } else {
                    if (ProfileUtils.getInstance().getProfile(target.getUniqueId()).isDead()) {
                        Profile profile = ProfileUtils.getInstance().getProfile(target.getUniqueId());

                        UHC.getInstance().getSpectatorManager().getSpectators().remove(target);
                        target.setAllowFlight(false);
                        target.getInventory().clear();
                        target.getInventory().setArmorContents(null);
                        target.setCanPickupItems(true);
                        target.spigot().setCollidesWithEntities(true);

                        UHC.getInstance().getTimerManager().getNocleanTimer().setCooldown(target, target.getUniqueId());

                        try {
                            target.getInventory().setContents(InventorySerialization.itemStackArrayFromBase64(profile.getDeathInventory()));
                        } catch (IOException e) {
                            player.sendMessage(ChatColor.RED + "Error while trying to give the player it's inventory. (" + profile.getDeathInventory() + ")");
                        }

                        try {
                            target.getInventory().setArmorContents(InventorySerialization.itemStackArrayFromBase64(profile.getDeathArmor()));
                        } catch (IOException e) {
                            player.sendMessage(ChatColor.RED + "Error while trying to give the player it's armor. (" + profile.getDeathArmor() + ")");
                        }

                        if (!profile.getDeathLocation().equalsIgnoreCase("null")) {
                            String[] location = profile.getDeathLocation().split(";");

                            double x = Double.parseDouble(location[0]);
                            double z = Double.parseDouble(location[1]);

                            target.teleport(new Location(Bukkit.getWorld("world"), x, Bukkit.getWorld("world").getHighestBlockYAt((int) x, (int) z), z));
                        }

                        UHC.getInstance().getGameManager().getAlivePlayers().add(target.getUniqueId());

                        target.sendMessage(ChatColor.RED + "You have been revived.");
                        player.sendMessage(ChatColor.RED + args[0] + " has been revived.");

                        for (Player spectators : UHC.getInstance().getSpectatorManager().getSpectators()) {
                            target.hidePlayer(spectators);
                            spectators.showPlayer(target);
                        }

                        UHC.getInstance().getStaffModeManager().getStaffModeList().forEach(staff -> staff.showPlayer(target));

                        for (UUID alive : UHC.getInstance().getGameManager().getAlivePlayers()) {
                            if (Bukkit.getPlayer(alive) != null) {
                                Bukkit.getPlayer(alive).showPlayer(target);
                                target.showPlayer(Bukkit.getPlayer(alive));
                            }
                        }

                        ProfileUtils.getInstance().getProfile(target.getUniqueId()).setDead(false);
                        ProfileUtils.getInstance().getProfile(target.getUniqueId()).setDeaths(ProfileUtils.getInstance().getProfile(target.getUniqueId()).getDeaths() - 1);
                        ProfileUtils.getInstance().getProfile(target.getUniqueId()).save(true);

                        if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 2) {
                            if (UHCTeam.getByUUID(target.getUniqueId()) != null) {
                                UHCTeam.getByUUID(target.getUniqueId()).setDtr(UHCTeam.getByUUID(target.getUniqueId()).getDtr() + 1);
                            } else {
                                UHCTeam team = new UHCTeam(target.getUniqueId());
                                UHC.getInstance().getTeams().add(team);
                                UHCTeam.getByUUID(target.getUniqueId()).setDtr(UHCTeam.getByUUID(target.getUniqueId()).getDtr() + 1);
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "That player is not dead.");
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
        }
    }

    @Override
    public String getDescription() {
        return "Revives a player";
    }
}
