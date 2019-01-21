package me.javaee.ffa.information.commands.argments;

import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.utils.LocationUtils;
import me.javaee.ffa.utils.command.CommandArgument;
import me.javaee.ffa.wand.Wand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartKothArgument extends CommandArgument {
    public StartKothArgument() {
        super("startkoth", null, "start");
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

            Information information = FFA.getPlugin().getInformationManager().getInformation();

            if (information.isKothStarted()) {
                player.sendMessage(ChatColor.RED + "The koth is already enabled.");
                return true;
            }

            information.setKothStarted(true);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[KingOfTheHill] &eThe &9Koth &ecan now be contested. &7(10:00)"));
        }
        return true;
    }
}
