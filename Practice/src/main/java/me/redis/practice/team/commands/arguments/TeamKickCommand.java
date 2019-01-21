package me.redis.practice.team.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.profile.Profile;
import me.redis.practice.team.Team;
import me.redis.practice.events.PlayerKickTeamEvent;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamKickCommand extends CommandArgument {
    public TeamKickCommand() {
        super("kick");
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <name>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

            if (profile.getStatus() != ProfileStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + "You must be in the spawn to kick a player.");
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

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name '&7" + args[0] + "&c' could not be find."));
                return true;
            }

            if (player.equals(target)) {
                player.sendMessage(ChatColor.RED + "You can't kick yourself.");
                return true;
            }

            if (!profile.getTeam().getMembers().contains(target)) {
                player.sendMessage(ChatColor.RED + "That player is not a member of your team.");
                return true;
            }

            PlayerKickTeamEvent partyEvent = new PlayerKickTeamEvent(player, target, team, true, true);
            Bukkit.getPluginManager().callEvent(partyEvent);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Team team = Practice.getPlugin().getProfileManager().getProfile((Player) sender).getTeam();

            if (team != null && team.getLeaderUuid() == ((Player) sender).getUniqueId()) {
                return team.getMembersNames();
            }
        }

        return null;
    }
}