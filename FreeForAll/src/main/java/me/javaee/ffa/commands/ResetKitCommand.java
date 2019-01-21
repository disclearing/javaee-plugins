package me.javaee.ffa.commands;

import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.utils.command.ExecutableCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetKitCommand extends ExecutableCommand {
    public ResetKitCommand() {
        super("resetkit", "Reset your kit in the database.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Information information = FFA.getPlugin().getInformationManager().getInformation();

            if (!information.getLobbyCuboid().contains(player.getLocation())) {
                player.sendMessage(ChatColor.RED + "You can only do this at the spawn.");
                return true;
            }

            Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

            if (profile == null) {
                player.sendMessage(ChatColor.RED + "Your profile hasn't been loaded yet.");
                return true;
            }

            profile.setKit(null);
            Bukkit.getScheduler().runTaskAsynchronously(FFA.getPlugin(), profile::save);

            player.sendMessage(ChatColor.GREEN + "Your kit has been reset successfully.");
        }

        return true;
    }
}
