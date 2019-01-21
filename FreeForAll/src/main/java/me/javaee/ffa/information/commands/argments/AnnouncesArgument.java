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

public class AnnouncesArgument extends CommandArgument {
    public AnnouncesArgument() {
        super("announces", null, "broadcasts");
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <add/remove> <message...>";
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length <= 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Information information = FFA.getPlugin().getInformationManager().getInformation();

            if (args[1].equalsIgnoreCase("remove")) {
                if (JavaUtils.isInteger(args[2])) {
                    player.sendMessage(ChatColor.RED + "You have deleted: " + ChatColor.translateAlternateColorCodes('&', information.getAnnounces().get(Integer.parseInt(args[2]))));
                    information.getAnnounces().remove(information.getAnnounces().get(Integer.parseInt(args[2])));
                } else {
                    player.sendMessage(ChatColor.RED + "You need to specify an index.");
                }
            } else if (args[1].equalsIgnoreCase("add")) {
                information.getAnnounces().add(StringUtils.join(args, " ", 2, args.length));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have added&7: " + StringUtils.join(args, " ", 2, args.length)));
            }

            information.save();
        }
        return true;
    }
}
