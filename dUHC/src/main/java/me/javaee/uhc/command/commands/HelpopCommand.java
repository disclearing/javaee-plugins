package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.handlers.Scenario;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */

public class HelpopCommand implements CommandExecutor {
    private ArrayList<Player> helpop = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <reason>");
                return true;
            }

            if (helpop.contains(player)) {
                player.sendMessage(ChatColor.RED + "You must wait 1 minute to request staff assistance again.");
            } else {
                String message = "";
                for (String part : args) {
                    if (message != "") message += " ";
                    message += part;
                }

                if (StringUtils.containsIgnoreCase(message, "border") || StringUtils.containsIgnoreCase(message, "borde")) {
                    player.sendMessage(ChatColor.GREEN + "Automatic Response:");
                    player.sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + "Current Border" + ChatColor.GRAY + ": " + ChatColor.WHITE + UHC.getInstance().getBorderShrinkTask().currentRadius + " x " + UHC.getInstance().getBorderShrinkTask().currentRadius);
                    player.sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + "First Shrink" + ChatColor.GRAY + ": " + ChatColor.WHITE + UHC.getInstance().getBorderShrinkTask().startTime + "m");
                } else if (StringUtils.containsIgnoreCase(message, "caves") || StringUtils.containsIgnoreCase(message, "cuevas")  || StringUtils.containsIgnoreCase(message, "mina")  || StringUtils.containsIgnoreCase(message, "minas")) {
                    player.sendMessage(ChatColor.GREEN + "Automatic Response:");
                    player.sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + "Caves" + ChatColor.GRAY + ": " + ChatColor.WHITE + "12.4, made by Silex");
                } else if (StringUtils.containsIgnoreCase(message, "apple") || StringUtils.containsIgnoreCase(message, "apple rate") || StringUtils.containsIgnoreCase(message, "rate")) {
                    player.sendMessage(ChatColor.GREEN + "Automatic Response:");
                    player.sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + "Apple Rate" + ChatColor.GRAY + ": " + ChatColor.WHITE + UHC.getInstance().getConfigurator().getIntegerOption("APPLERATE").getValue() + "%");
                } else if (StringUtils.containsIgnoreCase(message, "shears") || StringUtils.containsIgnoreCase(message, "tijeras")|| StringUtils.containsIgnoreCase(message, "shear")) {
                    player.sendMessage(ChatColor.GREEN + "Automatic Response:");
                    player.sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + "Shears" + ChatColor.GRAY + ": " + ChatColor.WHITE + UHC.getInstance().getConfigurator().getBooleanOption("SHEARS").getValue());
                } else if (StringUtils.containsIgnoreCase(message, "nether")) {
                    player.sendMessage(ChatColor.GREEN + "Automatic Response:");
                    player.sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + "Nether" + ChatColor.GRAY + ": " + ChatColor.WHITE + (Scenario.getByName("Nether").isEnabled() ? "true" : "false"));
                } else if (StringUtils.containsIgnoreCase(message, "pvp")) {
                    player.sendMessage(ChatColor.GREEN + "Automatic Response:");
                    player.sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + "PvP" + ChatColor.GRAY + ": " + ChatColor.WHITE + UHC.getInstance().getConfigurator().getIntegerOption("PVPTIME").getValue() + "m");
                } else if (StringUtils.containsIgnoreCase(message, "heal") || StringUtils.containsIgnoreCase(message, "final heal")) {
                    player.sendMessage(ChatColor.GREEN + "Automatic Response:");
                    player.sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + "Final Heal" + ChatColor.GRAY + ": " + ChatColor.WHITE + UHC.getInstance().getConfigurator().getIntegerOption("HEALTIME").getValue() + "m");
                } else {
                    for (Player mods : UHC.getInstance().getGameManager().getModerators()) {
                        if (UHC.getInstance().getGameManager().getHost() == mods) {
                            return true;
                        }

                        ComponentBuilder builder = new ComponentBuilder("");
                        builder.append("[").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append("HELPOP").color(net.md_5.bungee.api.ChatColor.DARK_RED);
                        builder.append("] ").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append(player.getName()).color(net.md_5.bungee.api.ChatColor.DARK_RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click to teleport to " + player.getName()).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));
                        builder.append(": ").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append(message).color(net.md_5.bungee.api.ChatColor.WHITE);
                        builder.retain(ComponentBuilder.FormatRetention.FORMATTING);

                        BaseComponent[] components = builder.create();
                        mods.spigot().sendMessage(components);
                    }

                    for (Player mods : UHC.getInstance().getGameManager().getHelpers()) {
                        if (UHC.getInstance().getGameManager().getHost() == mods) {
                            return true;
                        }

                        ComponentBuilder builder = new ComponentBuilder("");
                        builder.append("[").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append("HELPOP").color(net.md_5.bungee.api.ChatColor.DARK_RED);
                        builder.append("] ").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append(player.getName()).color(net.md_5.bungee.api.ChatColor.DARK_RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click to teleport to " + player.getName()).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));
                        builder.append(": ").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append(message).color(net.md_5.bungee.api.ChatColor.WHITE);
                        builder.retain(ComponentBuilder.FormatRetention.FORMATTING);

                        BaseComponent[] components = builder.create();
                        mods.spigot().sendMessage(components);
                    }

                    if (UHC.getInstance().getGameManager().getHost() != null) {
                        Player host = UHC.getInstance().getGameManager().getHost();

                        ComponentBuilder builder = new ComponentBuilder("");
                        builder.append("[").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append("HELPOP").color(net.md_5.bungee.api.ChatColor.DARK_RED);
                        builder.append("] ").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append(player.getName()).color(net.md_5.bungee.api.ChatColor.DARK_RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click to teleport to " + player.getName()).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));
                        builder.append(": ").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append(message).color(net.md_5.bungee.api.ChatColor.WHITE);
                        builder.retain(ComponentBuilder.FormatRetention.FORMATTING);

                        BaseComponent[] components = builder.create();
                        host.spigot().sendMessage(components);
                    }

                    player.sendMessage(ChatColor.translateAlternateColorCodes("&7[&4HELPOP&7] &4" + player.getName() + "&7: &f" + message));
                    helpop.add(player);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            helpop.remove(player);
                        }
                    }.runTaskLater(UHC.getInstance(), 60 * 20);
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You can't do this on console.");
        }
        return true;
    }
}
