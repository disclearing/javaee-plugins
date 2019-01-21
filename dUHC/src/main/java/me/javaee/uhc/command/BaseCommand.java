package me.javaee.uhc.command;

import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Contributors: gari
 *
 * Do not redistribute without permission from the author.
 */

public abstract class BaseCommand extends BukkitCommand {
    @Getter private final String command;
    @Getter private final Boolean requiresPermission;
    @Getter private final Boolean playerOnly;

    public BaseCommand(String command, List<String> aliases, Boolean requiresPermission, Boolean playerOnly) {
        super(command);

        setAliases(aliases);
        setDescription(command);

        this.command = command;
        this.requiresPermission = requiresPermission;
        this.playerOnly = playerOnly;
    }

    public abstract void execute(CommandSender sender, Command command, String label, String[] args);

    public abstract String getDescription();

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (getCommand().equalsIgnoreCase(getName())) {
            if (playerOnly) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (player.isOp()) {
                        execute(sender, this, label, args);
                        return true;
                    }

                    if (requiresPermission) {
                        if (player.hasPermission("command." + getName())) {
                            execute(sender, this, label, args);
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
                        }
                    } else {
                        execute(sender, this, label, args);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You can't execute this command on Console.");
                }
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (player.isOp()) {
                        execute(sender, this, label, args);
                        return true;
                    }

                    if (requiresPermission) {
                        if (player.hasPermission("command." + getName())) {
                            execute(sender, this, label, args);
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
                        }
                    } else {
                        execute(sender, this, label, args);
                    }
                } else {
                    execute(sender, this, label, args);
                }
            }
        }
        return true;
    }
}
