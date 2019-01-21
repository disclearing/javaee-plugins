package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.managers.GameManager;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.team.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ModeratorCommand extends BaseCommand {
    GameManager gameManager = UHC.getInstance().getGameManager();

    public ModeratorCommand() {
        super("mod", Arrays.asList("moderators", "moderator"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes("&cUsage: /" + label + " <player>"));
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player with name '" + args[0] + "' not found.");
            } else {
                if (gameManager.getModerators().contains(target)) {
                    gameManager.getModerators().remove(target);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes("&f" + target.getName() + "&6 has been removed from the moderators list."));
                } else {
                    if (!target.hasPermission("litebans.tempban")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes("&c" + target.getName() + " could not be modded. He is not a mod."));
                        return;
                    }

                    gameManager.getModerators().add(target);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes("&f" + target.getName() + "&6 has been added to the moderators list."));

                    if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
                        if (UHC.getInstance().getGameManager().getAlivePlayers().contains(target.getUniqueId())) {
                            UHC.getInstance().getGameManager().getAlivePlayers().remove(target.getUniqueId());
                        }
                        UHC.getInstance().getStaffModeManager().setStaffMode(target);
                    }

                    ProfileUtils.getInstance().getProfile(target.getUniqueId()).setDead(true);
                    ProfileUtils.getInstance().getProfile(target.getUniqueId()).save(true);

                    UHCTeam team = UHCTeam.getByUUID(target.getUniqueId());

                    if (team != null) {
                        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
                            team.getPlayerList().remove(target.getUniqueId());
                        } else {
                            team.setDtr(team.getDtr() - 1);
                            team.getPlayerList().remove(target.getUniqueId());
                        }
                    }

                    Bukkit.getOnlinePlayers().forEach(online -> {
                        if (!online.hasPermission("litebans.tempban")) online.hidePlayer((Player) sender);
                    });
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "It adds moderators to the game";
    }
}
