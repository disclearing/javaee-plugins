package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ListCommandsCommand extends BaseCommand {
    public ListCommandsCommand() {
        super("listcommands", Arrays.asList("listcommand", "listcmd"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        int counter = 0;

        for (BaseCommand baseCommand : UHC.getInstance().getCommands()) {
            if (baseCommand.getCommand().equalsIgnoreCase("listcommands")) continue;

            player.sendMessage(ChatColor.translateAlternateColorCodes("&6&l" + (counter + 1) + ". &e" + baseCommand.getCommand() + "&7- " + baseCommand.getDescription()));

            counter++;
        }
    }

    @Override
    public String getDescription() {
        return "It lists you the commands.";
    }
}
