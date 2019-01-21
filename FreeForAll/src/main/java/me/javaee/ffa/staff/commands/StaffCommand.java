package me.javaee.ffa.staff.commands;

import me.javaee.ffa.FFA;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.profiles.status.PlayerStatus;
import me.javaee.ffa.utils.command.ExecutableCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffCommand extends ExecutableCommand {
    public StaffCommand() {
        super("staffmode", null, "staff", "h", "mod", "modmode", "helper", "helpermode", "admin", "adminmode");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

            if (profile.getPlayerStatus() == PlayerStatus.STAFF) {
                FFA.getPlugin().getStaffManager().disable(player);
            } else if (profile.getPlayerStatus() == PlayerStatus.PLAYING) {
                FFA.getPlugin().getStaffManager().enable(player);
            }
        }

        return true;
    }
}
