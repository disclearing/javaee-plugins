package me.javaee.ffa.commands;

import me.javaee.ffa.FFA;
import me.javaee.ffa.profiles.Profile;
import me.javaee.ffa.utils.command.ExecutableCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatisticsCommand extends ExecutableCommand {
    public StatisticsCommand() {
        super("statistics", null, "stats");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                Profile profile = FFA.getPlugin().getProfileManager().getProfile(player);

                sendStatistics(profile, player);
            } else if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    Bukkit.getScheduler().runTaskAsynchronously(FFA.getPlugin(), () -> {
                        UUID offline = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                        Profile profile = FFA.getPlugin().getProfileManager().getNotCachedProfile(offline);

                        if (profile == null) {
                            player.sendMessage(ChatColor.RED + "Player with name '" + ChatColor.GRAY + args[0] + ChatColor.RED + "' was not found in the database.");
                            return;
                        }

                        sendStatistics(profile, player);
                    });
                } else {
                    Profile profile = FFA.getPlugin().getProfileManager().getProfile(target);

                    sendStatistics(profile, player);
                }
            }
        }

        return true;
    }

    public void sendStatistics(Profile profile, Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------------"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6" + profile.getName() + " statistics..."));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7 - &eELO&7: &f" + profile.getElo()));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7 - &eKills&7: &f" + profile.getKills()));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7 - &eDeaths&7: &f" + profile.getDeaths()));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------------"));
    }
}
