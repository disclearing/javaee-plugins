package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import me.javaee.uhc.database.profile.ProfileUtils;
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
public class StaffChatCommand extends BaseCommand {
    public StaffChatCommand() {
        super("staffchat", Arrays.asList("sc", "gsc"), false, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (UHC.getInstance().getGameManager().getHost() == player || UHC.getInstance().getGameManager().getModerators().contains(player)) {

            if (args.length == 0) {
                if (ProfileUtils.getInstance().getProfile(player.getUniqueId()).isStaffChat()) {
                    ProfileUtils.getInstance().getProfile(player.getUniqueId()).setStaffChat(false);
                    player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &cdisabled &eyour staff chat"));
                } else {
                    ProfileUtils.getInstance().getProfile(player.getUniqueId()).setStaffChat(true);
                    player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have &aenabled &eyour staff chat"));
                }
            } else {
                String message = "";
                for (String part : args) {
                    if (message != "") message += " ";
                    message += part;
                }

                for (Player mods : UHC.getInstance().getGameManager().getModerators()) {
                    if (UHC.getInstance().getGameManager().getHost() == mods) {
                        return;
                    }

                    mods.sendMessage(ChatColor.translateAlternateColorCodes("&7[&dStaff Chat&7] &d" + player.getName() + "&7: &f" + message));
                }

                if (UHC.getInstance().getGameManager().getHost() != null) {
                    UHC.getInstance().getGameManager().getHost().sendMessage(ChatColor.translateAlternateColorCodes("&7[&dStaff Chat&7] &d" + player.getName() + "&7: &f" + message));
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You can't staff chat if you are not a mod or the host.");
        }
    }

    @Override
    public String getDescription() {
        return "Toggles your staffchat mode";
    }
}
