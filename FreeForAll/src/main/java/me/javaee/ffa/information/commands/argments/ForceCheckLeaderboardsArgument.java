package me.javaee.ffa.information.commands.argments;

import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.utils.JavaUtils;
import me.javaee.ffa.utils.LocationUtils;
import me.javaee.ffa.utils.command.CommandArgument;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceCheckLeaderboardsArgument extends CommandArgument {
    public ForceCheckLeaderboardsArgument() {
        super("forcecheckleaderboard", null, "fclb");
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName();
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage(getUsage(label));
                return true;
            }

            FFA.getPlugin().getTopsManager().check();
        }
        return true;
    }
}
