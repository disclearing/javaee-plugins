package me.javaee.uhc.command.commands;

import me.javaee.uhc.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class NightVisionCommand extends BaseCommand {
    public NightVisionCommand() {
        super("nightvision", Arrays.asList("nightv", "nv"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (((Player) sender).hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage(ChatColor.RED + "Your night vision has been removed.");
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 1));
        player.sendMessage(ChatColor.GREEN + "You now have night vision.");
    }

    @Override
    public String getDescription() {
        return "It gives you night vision";
    }
}
