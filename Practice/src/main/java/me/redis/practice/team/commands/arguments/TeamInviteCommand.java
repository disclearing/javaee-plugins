package me.redis.practice.team.commands.arguments;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.profile.Profile;
import me.redis.practice.team.Team;
import me.redis.practice.utils.command.CommandArgument;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TeamInviteCommand extends CommandArgument {
    public TeamInviteCommand() {
        super("invite");
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

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            if (player.getName().equalsIgnoreCase(args[1])) {
                player.sendMessage(ChatColor.RED + "You can't invite yourself.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name '&7" + args[0] + "&c' could not be find."));
                return true;
            }

            if (team.hasInvited(target.getUniqueId())) {
                player.sendMessage(ChatColor.RED + target.getName() + " has already been invited.");
                return true;
            }

            if (profile.getTeam().getMembers().contains(target)) {
                player.sendMessage(ChatColor.RED + target.getName() + " is already a member of your team.");
                return true;
            }

            team.addInvite(target.getUniqueId());
            team.sendMessage(ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " has been invited to the team.");
            new FancyMessage("You have been invited to join ").color(ChatColor.YELLOW)
                    .then(player.getName() + "'s team ").color(ChatColor.GREEN)
                    .then("(Click here to accept)").color(ChatColor.GRAY).command("/team join " + player.getName())
                    .then(".").color(ChatColor.YELLOW).send(target);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}