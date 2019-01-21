package me.javaee.ffa.commands;

import me.javaee.ffa.FFA;
import me.javaee.ffa.information.Information;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.utils.SerializationUtils;
import me.javaee.ffa.utils.command.ExecutableCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveKitCommand extends ExecutableCommand {
    public SaveKitCommand() {
        super("savekit", "Saves your kit in the database.");
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

            if (profile.getKit() != null && profile.getKit().equals(SerializationUtils.playerInventoryToString(player.getInventory()))) {
                player.sendMessage(ChatColor.RED + "The kit you are trying to save is the same that you have in the database.");
                return true;
            }

            profile.setKit(SerializationUtils.playerInventoryToString(player.getInventory()));
            Bukkit.getScheduler().runTaskAsynchronously(FFA.getPlugin(), () -> profile.save());

            player.sendMessage(ChatColor.GREEN + "Your kit has been saved successfully.");
        }

        return true;
    }
}
