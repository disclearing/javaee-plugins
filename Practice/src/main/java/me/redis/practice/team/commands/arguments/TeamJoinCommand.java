package me.redis.practice.team.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.profile.Profile;
import me.redis.practice.team.Team;
import me.redis.practice.events.PlayerJoinTeamEvent;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamJoinCommand extends CommandArgument {
    public TeamJoinCommand() {
        super("join");
    }

    @Override
    public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <teamLeader>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            if (profile.getStatus() != ProfileStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + "You must be in the spawn to join a team.");
                return true;
            }

            if (profile.getTeam() != null) {
                player.sendMessage(ChatColor.RED + "You already have a team.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name '&7" + args[0] + "&c' could not be find."));
                return true;
            }

            Profile targetProfile = Practice.getPlugin().getProfileManager().getProfile(target.getUniqueId());
            Team team = targetProfile.getTeam();

            if (team == null) {
                player.sendMessage(ChatColor.RED + "That team does not exist.");
                return true;
            }

            if (team.isOpen()) {
                if (team.getMembers().size() >= 25) {
                    player.sendMessage(ChatColor.RED + "That team is full.");
                } else {
                    PlayerJoinTeamEvent partyEvent = new PlayerJoinTeamEvent(player, team, false);
                    Bukkit.getPluginManager().callEvent(partyEvent);
                }
            } else {
                if (!team.hasInvited(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You haven't been invited.");
                    return true;
                } else {
                    PlayerJoinTeamEvent partyEvent = new PlayerJoinTeamEvent(player, team, true);
                    Bukkit.getPluginManager().callEvent(partyEvent);
                }
            }
        }
        return true;
    }
}