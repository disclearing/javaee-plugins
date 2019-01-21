package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.events.TeamCreateEvent;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.tasks.GameTimeTask;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.ItemBuilder;
import net.silexpvp.nightmare.util.LuckPermsUtils;
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

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class LateJoinCommand extends BaseCommand {
    public LateJoinCommand() {
        super("latejoin", Arrays.asList("joinlate", "join"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (UHC.getInstance().isGameStarted()) {
            Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

            if (UHC.getInstance().getGameManager().getJoined().contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You have joined this UHC and died.");
                return;
            }

            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);

                if (target != null) {
                    if (UHC.getInstance().getSpectatorManager().getSpectators().contains(target)) {
                        UHC.getInstance().getSpectatorManager().getSpectators().remove(target);
                        target.setCanPickupItems(true);
                        profile.setDead(false);
                        profile.setLateScattered(true);
                        profile.setTotalGames(profile.getTotalGames() + 1);
                        target.getInventory().clear();
                        target.spigot().setCollidesWithEntities(true);
                        target.getInventory().setArmorContents(null);
                        target.setHealth(20);
                        target.setAllowFlight(false);
                        target.setFoodLevel(20);
                        target.setWhitelisted(true);

                        UHC.getInstance().getGameManager().getAlivePlayers().add(target.getUniqueId());
                        UHC.getInstance().getGameManager().setJoinedPlayers(UHC.getInstance().getGameManager().getJoinedPlayers() + 1);
                        UHC.getInstance().getGameManager().getJoined().add(target.getUniqueId());
                        UHC.getInstance().getGameManager().getKills().put(target.getUniqueId(), 0);
                        UHC.getInstance().getGameManager().getDiamonds().put(target.getUniqueId(), 0);

                        if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 2) {
                            UHCTeam team = new UHCTeam(target.getUniqueId());

                            UHC.getInstance().getTeams().add(team);
                            Bukkit.getPluginManager().callEvent(new TeamCreateEvent(target, team));
                        }

                        Random r = new Random();
                        int low = 1;
                        int high = 350;
                        int result = r.nextInt(high - low) + low;

                        Location lateJoin = UHC.getInstance().getGenerateSpawnsCommandHandler().scatterPoints.get(result);
                        lateJoin.setY(lateJoin.getWorld().getHighestBlockYAt(lateJoin.getBlockX(), lateJoin.getBlockZ()));

                        player.teleport(lateJoin);

                        for (Player online : Bukkit.getOnlinePlayers()) {
                            online.showPlayer(target);
                        }

                        for (UUID alive : UHC.getInstance().getGameManager().getAlivePlayers()) {
                            if (Bukkit.getPlayer(alive) != null) {
                                target.showPlayer(Bukkit.getPlayer(alive));
                                Bukkit.getPlayer(alive).showPlayer(target);
                            }
                        }

                        target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have been added to the game."));
                        profile.setMatchKills(0);
                        profile.save(true);

                        if (Scenario.getByName("BuildUHC").isEnabled()) {
                            Inventory inv = target.getInventory();

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

                            target.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                            target.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                            target.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                            target.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                        } else {
                            target.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
                        }
                    }
                }

                return;
            }

            if (Scenario.getByName("Event Game").isEnabled()) {
                player.sendMessage(ChatColor.RED + "You can't latejoin this game.");
                return;
            }

            if (player.hasPermission("superpro.pivipi")) {
                if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                    UHC.getInstance().getSpectatorManager().getSpectators().remove(player);
                    player.setCanPickupItems(true);
                    profile.setDead(false);
                    profile.setLateScattered(true);
                    profile.setTotalGames(profile.getTotalGames() + 1);
                    player.getInventory().clear();
                    player.spigot().setCollidesWithEntities(true);
                    player.getInventory().setArmorContents(null);
                    player.setHealth(20);
                    player.setAllowFlight(false);
                    player.setFoodLevel(20);
                    player.setWhitelisted(true);

                    UHC.getInstance().getGameManager().getAlivePlayers().add(player.getUniqueId());
                    UHC.getInstance().getGameManager().setJoinedPlayers(UHC.getInstance().getGameManager().getJoinedPlayers() + 1);
                    UHC.getInstance().getGameManager().getJoined().add(player.getUniqueId());
                    UHC.getInstance().getGameManager().getKills().put(player.getUniqueId(), 0);
                    UHC.getInstance().getGameManager().getDiamonds().put(player.getUniqueId(), 0);

                    if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 2) {
                        UHCTeam team = new UHCTeam(player.getUniqueId());

                        UHC.getInstance().getTeams().add(team);
                        Bukkit.getPluginManager().callEvent(new TeamCreateEvent(player, team));
                    }

                    Random r = new Random();
                    int low = 1;
                    int high = 350;
                    int result = r.nextInt(high - low) + low;

                    Location lateJoin = UHC.getInstance().getGenerateSpawnsCommandHandler().scatterPoints.get(result);
                    lateJoin.setY(lateJoin.getWorld().getHighestBlockYAt(lateJoin.getBlockX(), lateJoin.getBlockZ()));

                    player.teleport(lateJoin);

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.showPlayer(player);
                    }

                    for (UUID alive : UHC.getInstance().getGameManager().getAlivePlayers()) {
                        if (Bukkit.getPlayer(alive) != null) {
                            player.showPlayer(Bukkit.getPlayer(alive));
                            Bukkit.getPlayer(alive).showPlayer(player);
                        }
                    }

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have been added to the game."));
                    profile.setMatchKills(0);
                    profile.save(true);

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
                }
                return;
            }

            if (GameTimeTask.getNumOfSeconds() > 900) {
                player.sendMessage(ChatColor.RED + "You can only join in the first 15 minutes of the game.");
                return;
            }

            if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
                UHC.getInstance().getSpectatorManager().getSpectators().remove(player);
                player.setCanPickupItems(true);
                profile.setDead(false);
                profile.setLateScattered(true);
                profile.setTotalGames(profile.getTotalGames() + 1);
                player.getInventory().clear();
                player.spigot().setCollidesWithEntities(true);
                player.getInventory().setArmorContents(null);
                player.setHealth(20);
                player.setAllowFlight(false);
                player.setFoodLevel(20);
                player.setWhitelisted(true);

                UHC.getInstance().getGameManager().getAlivePlayers().add(player.getUniqueId());
                UHC.getInstance().getGameManager().setJoinedPlayers(UHC.getInstance().getGameManager().getJoinedPlayers() + 1);
                UHC.getInstance().getGameManager().getJoined().add(player.getUniqueId());
                UHC.getInstance().getGameManager().getKills().put(player.getUniqueId(), 0);
                UHC.getInstance().getGameManager().getDiamonds().put(player.getUniqueId(), 0);

                if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 2) {
                    UHCTeam team = new UHCTeam(player.getUniqueId());

                    UHC.getInstance().getTeams().add(team);
                    Bukkit.getPluginManager().callEvent(new TeamCreateEvent(player, team));
                }

                Random r = new Random();
                int low = 1;
                int high = 350;
                int result = r.nextInt(high - low) + low;

                Location lateJoin = UHC.getInstance().getGenerateSpawnsCommandHandler().scatterPoints.get(result);
                lateJoin.setY(lateJoin.getWorld().getHighestBlockYAt(lateJoin.getBlockX(), lateJoin.getBlockZ()));

                player.teleport(lateJoin);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.showPlayer(player);
                }

                for (UUID alive : UHC.getInstance().getGameManager().getAlivePlayers()) {
                    if (Bukkit.getPlayer(alive) != null) {
                        player.showPlayer(Bukkit.getPlayer(alive));
                        Bukkit.getPlayer(alive).showPlayer(player);
                    }
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have been added to the game."));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', LuckPermsUtils.getPrefix(player) + player.getName() + " &ehas been late scattered!"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &7- &4Remember: &cYou can buy a rank at &lshop.silexpvp.net &cto have this feature."));
                profile.setMatchKills(0);
                profile.save(true);

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
            }
        } else {
            player.sendMessage(ChatColor.RED + "The game hasn't started.");
        }
    }

    @Override
    public String getDescription() {
        return "Get added to the uhc after it started";
    }
}
