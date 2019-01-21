package me.javaee.uhc.practice.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import me.javaee.uhc.practice.PracticeManager;
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
public class PracticeCommand extends BaseCommand {
    private PracticeManager practiceManager = UHC.getInstance().getPracticeManager();

    public PracticeCommand() {
        super("practice", Arrays.asList("prac", "pract"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (!UHC.getInstance().getConfigurator().getBooleanOption("PRACTICE").getValue()) {
            player.sendMessage(ChatColor.RED + "The Practice is currently disabled.");
            return;
        }

        if (UHC.getInstance().getGameManager().getHost() == player || UHC.getInstance().getGameManager().getModerators().contains(player)) {
            player.sendMessage(ChatColor.RED + "You can't join the practice if you are a mod or the host.");
            return;
        }

        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            if (practiceManager.getPracticePlayers().contains(player)) {
                practiceManager.removeFromPractice(player);
            } else {
                practiceManager.addToPractice(player);
                UHC.getInstance().getPracticeManager().getKillStreak().put(player, 0);
            }
        }
    }

    @Override
    public String getDescription() {
        return "You can enter the practice";
    }
}
