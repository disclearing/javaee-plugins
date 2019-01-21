package me.javaee.ffa.information.commands.argments;

import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopKothArgument extends CommandArgument {
    public StopKothArgument() {
        super("stopkoth", null, "stop");
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

            if (!information.isKothStarted()) {
                player.sendMessage(ChatColor.RED + "The koth isn't enabled.");
                return true;
            }

            information.setKothStarted(false);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[KingOfTheHill] &eThe &9Koth &ehas been shut down. &7(10:00)"));
        }
        return true;
    }
}
