package me.redis.practice.duel.argument;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.duel.DuelRequest;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.match.type.SoloMatch;
import me.redis.practice.profile.Profile;
import me.redis.practice.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuelAcceptArgument extends CommandArgument {
    public DuelAcceptArgument() {
        super("accept");
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

            if (args.length < 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer with name '&7" + args[0] + "&c' does not exist."));
                return true;
            }

            Profile targetProfile = Practice.getPlugin().getProfileManager().getProfile(target.getUniqueId());

            if (targetProfile.getStatus() != ProfileStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + target.getName() + " is not in the spawn.");
                return true;
            }

            if (!profile.hasRequest(target)) {
                player.sendMessage(ChatColor.RED + "You have not a duel request from that player.");
                return true;
            }

            if (targetProfile.getStatus() != ProfileStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + "That player is not in the spawn.");
                return true;
            }


            DuelRequest request = profile.getRequest(target);
            profile.removeRequest(target);

            Arena arena = Practice.getPlugin().getArenaManager().getRandomArena();
            Arena sumo = Practice.getPlugin().getArenaManager().getRandomSumoArena();

            if (request.getLadder().getName().toLowerCase().equalsIgnoreCase("sumo")) {
                new SoloMatch(null, request.getLadder(), sumo, false, target, player);
            } else {
                new SoloMatch(null, request.getLadder(), arena, false, target, player);
            }
        }
        return true;
    }
}