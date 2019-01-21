package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.menu.menu.ScenariosMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ScenarioCommand extends BaseCommand {
    public ScenarioCommand() {
        super("scenarios", Arrays.asList("scen", "scenario", "escenario", "escenarios"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        new ScenariosMenu(player).open(player);
    }

    @Override
    public String getDescription() {
        return "Lists you the scenarios";
    }
}
