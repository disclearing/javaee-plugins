package me.redis.practice.team.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.profile.Profile;
import me.redis.practice.team.Team;
import me.redis.practice.utils.command.CommandArgument;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamInfoCommand extends CommandArgument {
    public TeamInfoCommand() {
        super("info");
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

            if (profile.getStatus() != ProfileStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + "You must be in the spawn to see your team's info.");
                return true;
            }

            if (profile.getTeam() == null) {
                player.sendMessage(ChatColor.RED + "You don't have a team.");
                return true;
            }

            Team team = profile.getTeam();

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Your team:"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLeader&7: &f" + team.getLeader().getName()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMembers&7:&f " + StringUtils.join(team.getMembersNames(), ", ")));
        }
        return true;
    }
}