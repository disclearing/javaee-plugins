package me.javaee.uhc.command.commands;

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
public class PluginsCommand extends BaseCommand {
    public PluginsCommand() {
        super("plugins", Arrays.asList("pl"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        player.sendMessage("Plugins (4): " + ChatColor.translateAlternateColorCodes("&aUHC&f, &aNightmare&f, &aTerrainControl&f, &aWorldEdit"));
    }

    @Override
    public String getDescription() {
        return "It lists you the plugins";
    }
}
