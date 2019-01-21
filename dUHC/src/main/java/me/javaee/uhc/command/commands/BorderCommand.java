package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.events.BorderShrinkEvent;
import me.javaee.uhc.tasks.BorderShrinkTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class BorderCommand extends BaseCommand {
    public BorderCommand() {
        super("setborder", Arrays.asList("border", "bord"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <value>");
        } else {
            if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
                UHC.getInstance().getConfigurator().getIntegerOption("RADIUS").setValue(Integer.parseInt(args[0]));
                Bukkit.getPluginManager().callEvent(new BorderShrinkEvent(UHC.getInstance().getBorderShrinkTask().getCurrentRadius(), Integer.valueOf(args[0])));
                UHC.getInstance().getBorderShrinkTask().setCurrentRadius(Integer.valueOf(args[0]));

                UHC.getInstance().getGenerateSpawnsCommandHandler().scatterPoints.clear();
                int a = 0;
                for (int i = 0; i < 500; i++) {
                    UHC.getInstance().getGenerateSpawnsCommandHandler().addLocation();
                    a++;
                }

                UHC.getInstance().setBorderShrinkTask(new BorderShrinkTask(UHC.getInstance().getBorderShrinkTask().getStartTime(), UHC.getInstance().getBorderShrinkTask().getBorderShrinkInterval(), UHC.getInstance().getBorderShrinkTask().getBorderShrinkBlocks(), UHC.getInstance().getBorderShrinkTask().getBorderMinimunRadius(), -1, -1));

                sender.sendMessage(ChatColor.translateAlternateColorCodes("&6Border&7:"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes(" &7- &eSpawn locations loaded&7: &f" + a));
                sender.sendMessage(ChatColor.translateAlternateColorCodes(" &7- &eRadius&7: &f" + UHC.getInstance().getConfigurator().getIntegerOption("RADIUS").getValue()));
            } else {
                Bukkit.getPluginManager().callEvent(new BorderShrinkEvent(UHC.getInstance().getBorderShrinkTask().getCurrentRadius(), Integer.valueOf(args[0])));
                UHC.getInstance().getBorderShrinkTask().setCurrentRadius(Integer.valueOf(args[0]));

                UHC.getInstance().getGenerateSpawnsCommandHandler().scatterPoints.clear();
                for (int i = 0; i < 500; i++) {
                    UHC.getInstance().getGenerateSpawnsCommandHandler().addLocation();

                    if (i >= 499) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&aSpawn locations loaded."));
                    }
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "Changes the current border";
    }
}
