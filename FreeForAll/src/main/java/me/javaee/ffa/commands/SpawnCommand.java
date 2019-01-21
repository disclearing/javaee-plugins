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

import java.io.Serializable;

public class SpawnCommand extends ExecutableCommand {
    public SpawnCommand() {
        super("spawn", "Teleports you to the spawn.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Information information = FFA.getPlugin().getInformationManager().getInformation();

            if (information.getLobbyCuboid().contains(player.getLocation())) {
                player.sendMessage(ChatColor.RED + "You can't do this at the spawn.");
                return true;
            }

            if (player.getNearbyEntities(10, 10, 10).size() > 0) {
                player.sendMessage(ChatColor.RED + "You are not safe. (You have nearby players)");
                return true;
            }

            FFA.getPlugin().getTimerManager().getTeleportTimer().setCooldown(player, player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are being sent to the spawn.");
        }

        return true;
    }
}
