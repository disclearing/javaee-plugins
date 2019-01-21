package me.javaee.uhc.command.commands;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ScreenshareCommand extends BaseCommand {
    public static List<UUID> screenShared = new ArrayList<>();

    public ScreenshareCommand() {
        super("screenshare", Arrays.asList("screenshares", "ss", "freeze", "halt"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player with name '" + args[0] + "' not found.");
            return;
        }

        if (UHC.getInstance().getGameManager().getHost() == player || UHC.getInstance().getGameManager().getModerators().contains(player)) {
            if (screenShared.contains(target.getUniqueId())) {
                screenShared.remove(target.getUniqueId());
                sender.sendMessage(ChatColor.RED + "You have unfrozen " + target.getName() + ".");
                UHC.getInstance().getTimerManager().getNocleanTimer().setCooldown(target, target.getUniqueId());
            } else {
                screenShared.add(target.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "You have frozen " + target.getName() + ".");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You are not a mod/host.");
        }
    }

    @Override
    public String getDescription() {
        return "Freezes someone to get screenshared";
    }
}