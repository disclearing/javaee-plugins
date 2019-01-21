package me.javaee.ffa.commands;

import me.javaee.ffa.FFA;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.profiles.status.PlayerStatus;
import me.javaee.ffa.utils.command.ExecutableCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand extends ExecutableCommand {
    public FreezeCommand() {
        super("freeze", null, "ss");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(org.bukkit.ChatColor.RED + "Player with name '" + org.bukkit.ChatColor.GRAY + args[0] + org.bukkit.ChatColor.RED + "' was not found.");
                return true;
            }

            Profile targetProfile = FFA.getPlugin().getProfileManager().getProfile(target);

            if (targetProfile.getPlayerStatus() == PlayerStatus.FROZEN) {
                targetProfile.setPlayerStatus(PlayerStatus.PLAYING);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have unfrozen &c" + target.getName()));
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have unbeen frozen by &c" + player.getName()));
            } else {
                targetProfile.setPlayerStatus(PlayerStatus.FROZEN);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have frozen &c" + target.getName()));
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have been frozen by &c" + player.getName()));
            }
        }

        return true;
    }
}
