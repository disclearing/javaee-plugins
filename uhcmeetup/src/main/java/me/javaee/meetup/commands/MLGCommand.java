package me.javaee.meetup.commands;

import me.javaee.meetup.Meetup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MLGCommand implements CommandExecutor {

    public static boolean inMLG = false;

    // Players who are MLG'ing
    public static Set<UUID> mlgPlayers = new HashSet<>();

    // Players who are allowed to MLG (the winners)
    public static Set<UUID> allowedMLGPlayers = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        final Player player = (Player) sender;

        if (!MLGCommand.allowedMLGPlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can not do an MLG water bucket at this time!");
            return true;
        }

        if (MLGCommand.inMLG) {
            player.sendMessage(ChatColor.RED + "MLG has already started!");
            return true;
        }

        MLGCommand.allowedMLGPlayers.remove(player.getUniqueId());
        MLGCommand.mlgPlayers.add(player.getUniqueId());

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f" + player.getName() + "&6 is going to mlg!"));
        return true;
    }

    public static void doMLG() {
        MLGCommand.inMLG = true;

        // Is no one doing MLG?
        if (MLGCommand.mlgPlayers.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "None of the winners have the courage to mlg.");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.shutdown();
                }
            }.runTaskLater(Meetup.getPlugin(), 5 * 20L);
            return;
        }

        new BukkitRunnable() {
            int ticks = 5;
            int delay = 0;

            int mlgCount = 0;

            @Override
            public void run() {
                if (this.delay != 0) {
                    this.delay--;
                    return;
                }

                if (this.ticks == 0) {
                    // Have they MLG'd 3 times already?
                    if (this.mlgCount == 3) {
                        StringBuilder sb = new StringBuilder();
                        for (UUID uuid : MLGCommand.mlgPlayers) {
                            Player player = Meetup.getPlugin().getServer().getPlayer(uuid);

                            // Is player still alive?
                            if (Meetup.getPlugin().getGameManager().getAlivePlayers().contains(player)) {
                                sb.append(", ");
                                sb.append(player.getDisplayName());
                            }
                        }

                        // Announce players who could MLG 3 times
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6Congratulations to &f" + sb.toString().substring(2) + "&6 for being as good as JavaEE doing MLGs"));

                        this.cancel();
                        return;
                    }

                    Iterator<UUID> it = MLGCommand.mlgPlayers.iterator();
                    while (it.hasNext()) {
                        UUID uuid = it.next();
                        Player player = Meetup.getPlugin().getServer().getPlayer(uuid);

                        // Did player survive other MLG's?
                        if (Meetup.getPlugin().getGameManager().getAlivePlayers().contains(player)) {
                            // Get a good block location to MLG above
                            Block block = null;
                            while (block == null || block.isLiquid()) {
                                block = player.getWorld().getHighestBlockAt((int) (Math.random() * 20), (int) (Math.random() * 20));
                            }

                            // Give them a water bucket
                            player.getInventory().setHeldItemSlot(0);
                            player.getInventory().setItem(0, new ItemStack(Material.WATER_BUCKET));
                            player.getInventory().setItem(1, new ItemStack(Material.WATER_BUCKET));
                            player.getInventory().setItem(2, new ItemStack(Material.WATER_BUCKET));
                            player.getInventory().setItem(3, new ItemStack(Material.WATER_BUCKET));
                            player.getInventory().setItem(4, new ItemStack(Material.WATER_BUCKET));
                            player.getInventory().setItem(5, new ItemStack(Material.WATER_BUCKET));
                            player.getInventory().setItem(6, new ItemStack(Material.WATER_BUCKET));
                            player.getInventory().setItem(7, new ItemStack(Material.WATER_BUCKET));
                            player.getInventory().setItem(8, new ItemStack(Material.WATER_BUCKET));


                            player.updateInventory();

                            // Teleport to location
                            player.teleport(block.getLocation().add(0, Math.random() * 100, 0));
                        } else {
                            it.remove();
                        }
                    }

                    this.mlgCount++;

                    // Reset and count down again
                    this.ticks = 5;
                    this.delay = 5;
                } else {
                    // Is no one doing MLG?
                    if (MLGCommand.mlgPlayers.isEmpty()) {
                        this.cancel();
                        return;
                    }

                    boolean everyoneDied = true;

                    // Check if there's anyone alive
                    for (UUID uuid : MLGCommand.mlgPlayers) {
                        Player player = Bukkit.getPlayer(uuid);

                        if (Meetup.getPlugin().getGameManager().getAlivePlayers().contains(player)) {
                            everyoneDied = false;
                            break;
                        }
                    }

                    // Did everyone die?
                    if (everyoneDied) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6No one was able to complete the challenge!"));

                        this.cancel();
                        return;
                    }

                    String s = "1st";

                    if (this.mlgCount == 1) s = "2nd";
                    else if (this.mlgCount == 2) s = "3rd";

                    if (mlgCount >= 3) {
                        //Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6Congratulations to &f" + sb.toString().substring(2) + "&6 for being as good as JavaEE doing MLGs"));
                        return;
                    }

                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&l" + s + ". &6MLG in &f" + this.ticks + "&6!"));

                    this.ticks--;
                }
            }
        }.runTaskTimer(Meetup.getPlugin(), 20L, 20L);
    }

}
