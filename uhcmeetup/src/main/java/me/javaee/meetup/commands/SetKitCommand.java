package me.javaee.meetup.commands;

import me.javaee.meetup.Meetup;
import me.javaee.meetup.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class SetKitCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You can't do this in the Console.");
            return true;
        }

        if (!(sender.isOp())) return true;

        if (args.length == 0) {
            Meetup.getPlugin().getKitManager().createKit(new Kit(Meetup.getPlugin().getKitManager().getKits().size() + 1, ((Player) sender).getInventory().getContents(), ((Player) sender).getInventory().getArmorContents()));
            sender.sendMessage("Added kit.");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            for (Kit kit : Meetup.getPlugin().getKitManager().getKits()) {
                sender.sendMessage(ChatColor.RED + kit.getNumber().toString());
            }
            return true;
        } else if (args[0].equalsIgnoreCase("load")) {
            Kit kit = Meetup.getPlugin().getKitManager().getKits().get(Integer.parseInt(args[1]));

            ((Player) sender).getInventory().setArmorContents(kit.getArmor());
            ((Player) sender).getInventory().setContents(kit.getInventory());
            return true;
        }
        return true;
    }
}
