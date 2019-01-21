package me.javaee.uhc.listeners.misc;

import javafx.stage.Screen;
import me.javaee.disguise.Disguise;
import me.javaee.uhc.UHC;
import me.javaee.uhc.command.commands.ScreenshareCommand;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.managers.GameManager;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.team.UHCTeam;
import me.javaee.uhc.utils.Configurator;
import net.badlion.worldborder.WorldBorderFillMessageEvent;
import net.silexpvp.nightmare.Nightmare;
import net.silexpvp.nightmare.util.LuckPermsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ChatListener implements Listener {
    private GameManager gameManager = UHC.getInstance().getGameManager();

    private List<Player> slowChat = new ArrayList<>();
    private List<Player> slowCommands = new ArrayList<>();

    private Configurator.Option teamSize = UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.TEAMSIZE.name());

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("litebans.tempban")) {
            if (slowCommands.contains(player)) {
                player.sendMessage(ChatColor.RED + "You can't chat that fast.");
                event.setCancelled(true);
            } else {
                slowCommands.add(player);

                Bukkit.getScheduler().scheduleSyncDelayedTask(UHC.getInstance(), () -> slowCommands.remove(player), 30);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String prefix = LuckPermsUtils.getPrefix(player);

        if (Nightmare.getPlugin().getProfileManager().getProfile(player).isInStaffChat()) {
            event.setCancelled(true);
            return;
        }

        if (slowChat.contains(player)) {
            player.sendMessage(ChatColor.RED + "You can't chat that fast.");
            event.setCancelled(true);
            return;
        }

        if (event.getMessage().toLowerCase().contains("event cancelled")) {
            event.setCancelled(true);
            return;
        }

        if (ScreenshareCommand.screenShared.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes("&7[&cFrozen&7] &c" + player.getName() + "&7: &f" + event.getMessage()));

            UHC.getInstance().getStaffModeManager().getStaffModeList().forEach(staff -> {
                staff.sendMessage(ChatColor.translateAlternateColorCodes("&7[&cFrozen&7] &c" + player.getName() + "&7: &f" + event.getMessage()));
            });

            event.setCancelled(true);
            return;
        }

        if (UHC.getInstance().getSpectatorManager().getSpectators().contains(player)) {
            event.setCancelled(true);

            if (UHC.getInstance().getSpectatorManager().getMutedChat()) {
                player.sendMessage(ChatColor.RED + "Spectators chat is muted!");
                return;
            }

            if (UHC.getInstance().getGameManager().getGameState() == GameState.END) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&b(Spectator) " + prefix + player.getName() + "&7: &f") + event.getMessage());
            } else {
                for (Player spectators : UHC.getInstance().getSpectatorManager().getSpectators()) {
                    spectators.sendMessage(ChatColor.translateAlternateColorCodes("&b(Spectator) " + prefix + player.getName() + "&7:&f ") + event.getMessage());
                }

                if (UHC.getInstance().getGameManager().getHost() != null) {
                    UHC.getInstance().getGameManager().getHost().sendMessage(ChatColor.translateAlternateColorCodes("&b(Spectator) " + prefix + player.getName() + "&7: &f") + event.getMessage());
                }

                for (Player mods : UHC.getInstance().getGameManager().getModerators()) {
                    mods.sendMessage(ChatColor.translateAlternateColorCodes("&b(Spectator) " + prefix + player.getName() + "&7: &f") + event.getMessage());
                }

                for (Player mods : UHC.getInstance().getGameManager().getHelpers()) {
                    mods.sendMessage(ChatColor.translateAlternateColorCodes("&b(Spectator) " + prefix + player.getName() + "&7: &f") + event.getMessage());
                }
            }
        }

        if (gameManager.getHost() == event.getPlayer()) {
            event.setFormat(ChatColor.translateAlternateColorCodes("&7[&6&lHost&7]" + prefix + event.getPlayer().getName()) + ChatColor.GRAY + ": " + ChatColor.WHITE + "%2$s");
        } else if (gameManager.getModerators().contains(event.getPlayer())) {
            event.setFormat(ChatColor.translateAlternateColorCodes("&3(UHC-MOD)" + prefix + event.getPlayer().getName()) + ChatColor.GRAY + ": " + ChatColor.WHITE + "%2$s");
        } else if (gameManager.getHelpers().contains(event.getPlayer())) {
            event.setFormat(ChatColor.translateAlternateColorCodes("&3(UHC-HELPER)" + prefix + event.getPlayer().getName()) + ChatColor.GRAY + ": " + ChatColor.WHITE + "%2$s");
        } else {
            if ((int) teamSize.getValue() >= 2) {
                if (UHCTeam.getByUUID(event.getPlayer().getUniqueId()) == null) {
                    event.setFormat(ChatColor.translateAlternateColorCodes(prefix + event.getPlayer().getName() + "&7: &f") + "%2$s");
                } else {
                    Profile profile = ProfileUtils.getInstance().getProfile(event.getPlayer().getUniqueId());

                    if (profile.isTeamChat()) {
                        if (String.valueOf(event.getMessage().charAt(0)).equalsIgnoreCase("!")) {
                            event.setMessage(event.getMessage().replace("!", ""));
                            event.setFormat(ChatColor.translateAlternateColorCodes("&6[&fTeam #" + UHCTeam.getByUUID(event.getPlayer().getUniqueId()).getNumber() + "&6] " + prefix + event.getPlayer().getName() + "&7: &f") + "%2$s");
                        } else {
                            for (UUID players : UHCTeam.getByUUID(event.getPlayer().getUniqueId()).getPlayerList()) {
                                Player teamp = Bukkit.getPlayer(players);

                                teamp.sendMessage(ChatColor.translateAlternateColorCodes("&7(TC) &6" + event.getPlayer().getName() + "&7: &e" + event.getMessage()));
                            }
                            event.setCancelled(true);
                        }
                    } else {
                        if (String.valueOf(event.getMessage().charAt(0)).equalsIgnoreCase("@")) {
                            for (UUID players : UHCTeam.getByUUID(event.getPlayer().getUniqueId()).getPlayerList()) {
                                Player teamp = Bukkit.getPlayer(players);

                                teamp.sendMessage(ChatColor.translateAlternateColorCodes("&7(TC) &6" + event.getPlayer().getName() + "&7: &e" + event.getMessage().replace("@", "")));
                            }
                            event.setCancelled(true);
                        } else {
                            if (Disguise.getInstance().isDisguised(player)) {
                                event.setFormat(ChatColor.translateAlternateColorCodes("&6[&fTeam #" + UHCTeam.getByUUID(event.getPlayer().getUniqueId()).getNumber() + "&6] " + event.getPlayer().getName() + "&7: &f") + event.getMessage());
                            } else {
                                event.setFormat(ChatColor.translateAlternateColorCodes("&6[&fTeam #" + UHCTeam.getByUUID(event.getPlayer().getUniqueId()).getNumber() + "&6] " + prefix + event.getPlayer().getName() + "&7: &f") + event.getMessage());
                            }
                        }
                    }
                }
            } else {
                if (Disguise.getInstance().isDisguised(player)) {
                    event.setFormat(ChatColor.translateAlternateColorCodes("&6" + event.getPlayer().getName() + "&7: &f") + "%2$s");
                } else {
                    event.setFormat(ChatColor.translateAlternateColorCodes(prefix + event.getPlayer().getName() + "&7: &f") + "%2$s");
                }
            }

            if (!player.hasPermission("litebans.tempban")) {
                slowChat.add(player);

                Bukkit.getScheduler().scheduleSyncDelayedTask(UHC.getInstance(), () -> slowChat.remove(player), 20 * 5);
            }
        }
    }

    private void moveNorth(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes("&cYou have timed out."));
        player.kickPlayer("Timed Out");
    }

    @EventHandler
    public void onFill(WorldBorderFillMessageEvent event) {
        if (UHC.getInstance().getGameManager().getHost() != null) {
            UHC.getInstance().getGameManager().getHost().sendMessage(ChatColor.translateAlternateColorCodes("&aThe world is generating: &e" + event.getMessage()));
        }
    }

    @EventHandler
    public void onAsyncCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();

        if (command.equalsIgnoreCase("d") || command.equalsIgnoreCase("dis") || command.equalsIgnoreCase("disguise")) {
            if (UHC.getInstance().getGameManager().getGameState() != GameState.WAITING) {
                event.getPlayer().sendMessage(ChatColor.RED + "You need to be waiting for a game in order to disguise.");
                event.setCancelled(true);
            }
        }
    }
}
