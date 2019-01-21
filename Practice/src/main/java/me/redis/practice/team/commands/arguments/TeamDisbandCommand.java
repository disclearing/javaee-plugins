package me.redis.practice.team.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.profile.Profile;
import me.redis.practice.team.Team;
import me.redis.practice.events.PlayerDisbandTeamEvent;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamDisbandCommand extends CommandArgument {
    public TeamDisbandCommand() {
        super("disband");
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
                player.sendMessage(ChatColor.RED + "You must be in the spawn to disband a team.");
                return true;
            }

            if (profile.getTeam() == null) {
                player.sendMessage(ChatColor.RED + "You don't have a team.");
                return true;
            }

            Team team = profile.getTeam();

            if (team.getLeaderUuid() != player.getUniqueId()) {
                player.sendMessage(ChatColor.RED + "You must be the leader of your team.");
                return true;
            }

            // Just call the event in this argument, because there's a lot of events that will need this one! (like leader quits, etc).

            PlayerDisbandTeamEvent playerDisbandTeamEvent = new PlayerDisbandTeamEvent(player, team);
            Bukkit.getPluginManager().callEvent(playerDisbandTeamEvent);
        }
        return true;
    }
}