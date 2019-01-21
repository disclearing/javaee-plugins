package me.javaee.uhc.command.commands;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.team.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class BackpackCommand extends BaseCommand {
    public BackpackCommand() {
        super("backpack", Arrays.asList("bp", "backpacks"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (Scenario.getByName("BackPacks").isEnabled()) {
            if (UHC.getInstance().getGameManager().getGameState() == GameState.INGAME) {
                player.openInventory(UHCTeam.getByUUID(player.getUniqueId()).getInventory());
                player.sendMessage(ChatColor.GREEN + "Opened");
            } else {
                player.sendMessage(ChatColor.RED + "The game has not started yet.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Opens your team's backpack";
    }
}
