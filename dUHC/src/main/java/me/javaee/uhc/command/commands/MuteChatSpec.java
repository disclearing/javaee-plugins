package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class MuteChatSpec extends BaseCommand {
    public MuteChatSpec() {
        super("mutechatspec", Collections.singletonList("mcs"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (UHC.getInstance().getSpectatorManager().getMutedChat()) {
            UHC.getInstance().getSpectatorManager().setMutedChat(false);
            sender.sendMessage(ChatColor.GREEN + "Spectators chat has been unmuted.");
        } else {
            UHC.getInstance().getSpectatorManager().setMutedChat(true);
            sender.sendMessage(ChatColor.RED + "Spectators chat has been muted.");
        }
    }

    @Override
    public String getDescription() {
        return "It mutes the spectators chat";
    }
}
