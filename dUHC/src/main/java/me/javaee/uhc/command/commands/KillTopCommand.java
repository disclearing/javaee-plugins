package me.javaee.uhc.command.commands;

import com.google.common.base.Strings;
import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class KillTopCommand extends BaseCommand {
    private static final String STRAIGHT_LINE_TEMPLATE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256);
    public static String STRAIGHT_LINE_DEFAULT = STRAIGHT_LINE_TEMPLATE.substring(0, 55);

    public KillTopCommand() {
        super("killtop", Arrays.asList("kt", "tk", "topkills"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Map<UUID, Integer> kills;
        int x;

        if (UHC.getInstance().getGameManager().getGameState() == GameState.WAITING) {
            sender.sendMessage(ChatColor.RED + "The UHC hasnt started.");
            return;
        }

        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------");
        sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Dominant Participants");
        sender.sendMessage("");

        Map<UUID, Integer> unsortedkills = UHC.getInstance().getGameManager().getKills();
        kills = sortByValue(unsortedkills);

        x = 1;
        for (Object object : kills.keySet()) {
            if (x == 11) {
                break;
            }

            UUID uuid = (UUID) object;
            if (kills.get(uuid) != 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes("&6&l" + x + ". &e" + Bukkit.getOfflinePlayer(uuid).getName() + "&7: &f" + kills.get(uuid)));
            }
            x++;
        }

        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------------");
    }

    @Override
    public String getDescription() {
        return "It gets a top 10 of players that killed the most";
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
