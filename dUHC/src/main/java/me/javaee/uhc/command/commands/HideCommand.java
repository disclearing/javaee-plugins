package me.javaee.uhc.command.commands;

import me.javaee.uhc.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class HideCommand extends BaseCommand {
    public HideCommand() {
        super("hide", Arrays.asList("hidea"), true, true);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target != null) {
            if (player.canSee(target)) {
                player.hidePlayer(target);
				player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou have hidden &a" + target.getName() + "&e."));
            } else {
                player.showPlayer(target);
                player.sendMessage(ChatColor.translateAlternateColorCodes("&eYou now can see &a" + target.getName() + "&e again."));
            }
        }
    }

    @Override
    public String getDescription() {
        return "Hides a player";
    }
}
