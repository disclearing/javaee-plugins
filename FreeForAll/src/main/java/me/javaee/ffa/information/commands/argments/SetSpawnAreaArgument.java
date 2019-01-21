package me.javaee.ffa.information.commands.argments;

import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.utils.LocationUtils;
import me.javaee.ffa.utils.command.CommandArgument;
import me.javaee.ffa.wand.Wand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnAreaArgument extends CommandArgument {
    public SetSpawnAreaArgument() {
        super("setspawnarea", null);
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

            Wand wand = FFA.getPlugin().getWandManager().getWand(player);
            Information information = FFA.getPlugin().getInformationManager().getInformation();

            if (wand == null || wand.getFirstLocation() == null || wand.getSecondLocation() == null) {
                player.sendMessage(ChatColor.RED + "You must have wand locations.");
                return true;
            }

            information.setLobbyFirst(LocationUtils.getString(wand.getFirstLocation()));
            information.setLobbySecond(LocationUtils.getString(wand.getSecondLocation()));
            information.save();

            Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);
            profile.getFirstPillar().removePillar();
            profile.setFirstPillar(null);

            profile.getSecondPillar().removePillar();
            profile.setSecondPillar(null);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have saved the spawn coordinates."));
        }
        return true;
    }
}
