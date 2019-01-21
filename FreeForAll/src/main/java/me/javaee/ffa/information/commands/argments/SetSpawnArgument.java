package me.javaee.ffa.information.commands.argments;

import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.utils.LocationUtils;
import me.javaee.ffa.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnArgument extends CommandArgument {
    public SetSpawnArgument() {
        super("setspawn", null, "setspawnlocation");
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

            information.setLobbyLocation(LocationUtils.getString(player.getLocation().add(0.5, 0.5, 0.5)));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have saved the spawn coordinates."));
            information.save();
        }
        return true;
    }
}
