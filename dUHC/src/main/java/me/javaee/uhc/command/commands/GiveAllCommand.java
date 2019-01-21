package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import net.minecraft.util.org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class GiveAllCommand extends BaseCommand {
    public GiveAllCommand() {
        super("giveall", Arrays.asList("giveal", "gall"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <itemId> <amount>");
        } else {
            if (isInteger(args[0]) && isInteger(args[1])) {
                if (UHC.getInstance().getGameManager().getAlivePlayers().size() <= 0) {
                    player.sendMessage(ChatColor.RED + "There are not alive players LOL!");
                } else {

                    ItemStack itemstack = null;
                    if (NumberUtils.isDigits(args[0])) {
                        itemstack = new ItemStack(Material.getMaterial(Integer.valueOf(args[0])), Integer.parseInt(args[1]));
                    }

                    for (UUID players : UHC.getInstance().getGameManager().getAlivePlayers()) {
                        if (Bukkit.getPlayer(players) != null) Bukkit.getPlayer(players).getInventory().addItem(itemstack);
                    }

                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&6&lSILEX &7- &6All the players have been given &e" + args[1] + " " + WordUtils.capitalizeFully(String.valueOf(Material.getMaterial(Integer.valueOf(args[0]))).replace("_", " ")) + "s"));
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <itemId> <amount>");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Gives all the alive players an item";
    }

    public boolean isInteger(String i) {
        try {
            int integer = Integer.parseInt(i);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
