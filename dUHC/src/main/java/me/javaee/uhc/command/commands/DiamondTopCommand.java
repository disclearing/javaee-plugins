package me.javaee.uhc.command.commands;

import com.google.common.base.Strings;
import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class DiamondTopCommand extends BaseCommand {
    private static final String STRAIGHT_LINE_TEMPLATE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256);
    public static String STRAIGHT_LINE_DEFAULT = STRAIGHT_LINE_TEMPLATE.substring(0, 55);

    public DiamondTopCommand() {
        super("diamondtop", Arrays.asList("dt", "dtop", "diamondt"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        Map<UUID, Integer> diamonds;
        int x;

        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            sender.sendMessage(ChatColor.RED + "The UHC hasnt started.");
            return;
        }

        if (UHC.getInstance().getGameManager().getModerators().contains(player) || UHC.getInstance().getGameManager().getHost() == player) {
            sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------");
            sender.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "Diamonds Mined");
            sender.sendMessage("");

            Map<UUID, Integer> unsortedkills = UHC.getInstance().getGameManager().getDiamonds();
            diamonds = sortByValue(unsortedkills);

            x = 1;
            for (Object object : diamonds.keySet()) {
                if (x == 11) {
                    break;
                }

                UUID uuid = (UUID) object;
                if (diamonds.get(uuid) != 0) {
                    ComponentBuilder builder = new ComponentBuilder("");
                    builder.append(String.valueOf(x + ". ")).color(net.md_5.bungee.api.ChatColor.DARK_AQUA).color(net.md_5.bungee.api.ChatColor.BOLD);
                    if (Bukkit.getPlayer(uuid) == null) {
                        builder.append(Bukkit.getOfflinePlayer(uuid).getName()).color(net.md_5.bungee.api.ChatColor.AQUA).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "This player is offline").create()));
                    } else {
                        builder.append(Bukkit.getPlayer(uuid).getName()).color(net.md_5.bungee.api.ChatColor.AQUA).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click to teleport to " + Bukkit.getPlayer(uuid).getName()).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + Bukkit.getPlayer(uuid).getName()));;
                    }
                    builder.append(": ").color(net.md_5.bungee.api.ChatColor.GRAY);
                    builder.append(String.valueOf(diamonds.get(uuid))).color(net.md_5.bungee.api.ChatColor.WHITE);
                    builder.retain(ComponentBuilder.FormatRetention.FORMATTING);

                    BaseComponent[] components = builder.create();
                    ((Player) sender).spigot().sendMessage(components);
                }
                x++;
            }
        }
        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------");
    }

    @Override
    public String getDescription() {
        return "It gets a list of top 10 diamonds mined";
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList(map.entrySet());
        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
        Map<K, V> result = new LinkedHashMap();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
