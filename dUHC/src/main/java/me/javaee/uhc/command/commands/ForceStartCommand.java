package me.javaee.uhc.command.commands;

import com.google.common.base.Strings;
import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.team.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ForceStartCommand extends BaseCommand {
    private static final String STRAIGHT_LINE_TEMPLATE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256);
    public static String STRAIGHT_LINE_DEFAULT = STRAIGHT_LINE_TEMPLATE.substring(0, 55);

    public ForceStartCommand() {
        super("forcestart", Arrays.asList("start", "gamestart", "startgame"), true, false);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (UHC.getInstance().getGameManager().getGameState() != GameState.WAITING) {
            sender.sendMessage(ChatColor.RED + "You are not waiting for a game to start.");
        } else {
            if (!((boolean) UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.MAPGENERATED.name()).getValue())) {
                sender.sendMessage(ChatColor.RED + "The world must be generated.");
                return;
            }
            if (UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.PVPTIME.name()) == null) {
                sender.sendMessage(ChatColor.RED + "You need to set the PvPTime.");
                return;
            }
            if (UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.HEALTIME.name()) == null) {
                sender.sendMessage(ChatColor.RED + "You need to set the HealTime.");
                return;
            }
            if (UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.RADIUS.name()) == null) {
                sender.sendMessage(ChatColor.RED + "You need to set the Radius.");
                return;
            }

            UHCTeam red = UHCTeam.getByName("Red");
            UHCTeam blue = UHCTeam.getByName("Blue");

            if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() >= 50) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (UHC.getInstance().getGameManager().getHost() != player && !UHC.getInstance().getGameManager().getModerators().contains(player)) {
                        if (UHCTeam.getByUUID(player.getUniqueId()) == null) {
                            UHCTeam team = whoHasLessPlayers(red, blue);

                            if (team.getPlayerList().size() >= 50) {
                                player.sendMessage(ChatColor.RED + "You can't be added to a team. All the teams are full.");
                                player.kickPlayer(ChatColor.RED + "You can't be added to a team. All the teams are full.");
                                return;
                            }

                            team.getPlayerList().add(player.getUniqueId());
                            player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have been added to the " + team.getColor() + team.getName() + "&e team!"));
                        }
                    }
                });

                int counter = 0;
                int difference = Math.max(red.getPlayerList().size(), blue.getPlayerList().size()) - Math.min(red.getPlayerList().size(), blue.getPlayerList().size());

                if (difference >= 10) {
                    UHCTeam more = whoHasMorePlayers(red, blue);
                    UHCTeam less = whoHasLessPlayers(red, blue);

                    for (int i = 0; i <= more.getPlayerList().size(); i++) {
                        Player player = Bukkit.getPlayer(more.getPlayerList().get(i));

                        if (player != null) {
                            if (counter <= difference) {
                                more.getPlayerList().remove(player.getUniqueId());
                                less.getPlayerList().add(player.getUniqueId());
                            }

                            counter++;
                        }
                    }
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (UHC.getInstance().getGameManager().getModerators().contains(player) || UHC.getInstance().getGameManager().getHost() == player) {
                        player.sendMessage(ChatColor.RED + "You are a staff, your team hasn't been created.");
                    } else {
                        if (UHCTeam.getByUUID(player.getUniqueId()) == null) {
                            Bukkit.dispatchCommand(player, "team create");
                        }
                    }
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().setContents(new ItemStack[0]);
                player.getInventory().setArmorContents(null);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.setHealth(20);
                player.setFoodLevel(20);

                player.removePotionEffect(PotionEffectType.ABSORPTION);
                UHC.getInstance().getPracticeManager().getPracticePlayers().remove(player);
            }

            UHC.getInstance().getGameManager().setGameState(GameState.SCATTER);

            int[] counter = {15};

            new BukkitRunnable() {
                public void run() {
                    if (counter[0] < 1) {
                        cancel();

                        if (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() <= 1) {
                            UHC.getInstance().getGameManager().setCountdown(UHC.getInstance().getGameManager().getCountdown() + 5);
                        } else {
                            UHC.getInstance().getGameManager().setCountdown(UHC.getInstance().getGameManager().getCountdown() + 2);
                        }

                        UHC.getInstance().getGameManager().startCountdown();

                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&4&lRemember: &cDo not relog, if you do, you will be removed from the uhc."));
                    } else {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&eThe game is going to start in &a" + counter[0] + " seconds."));
                    }
                    counter[0]--;
                }
            }.runTaskTimer(UHC.getInstance(), 0L, 20L);
        }
    }

    @Override
    public String getDescription() {
        return "Starts the game";
    }

    public UHCTeam whoHasLessPlayers(UHCTeam team1, UHCTeam team2) {
        if (team1.getPlayerList().size() < team2.getPlayerList().size()) {
            return team1;
        } else {
            return team2;
        }
    }

    public UHCTeam whoHasMorePlayers(UHCTeam team1, UHCTeam team2) {
        if (team1.getPlayerList().size() > team2.getPlayerList().size()) {
            return team1;
        } else {
            return team2;
        }
    }
}
